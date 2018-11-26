/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.renov8.spec.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.pack.spec.loader.PackSpecLoader;
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.resolved.ResolvedPack;
import org.jboss.renov8.spec.PackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
public class InstallSpecResolver {

    private static List<PackSpecLoader> defaultSpecLoaders;

    private static synchronized List<PackSpecLoader> getDefaultSpecLoaders() throws Renov8Exception {
        return defaultSpecLoaders == null ? defaultSpecLoaders = initSpecLoaders() : defaultSpecLoaders;
    }

    private static List<PackSpecLoader> initSpecLoaders() throws Renov8Exception {
        final Iterator<PackSpecLoader> i = ServiceLoader.load(PackSpecLoader.class).iterator();
        if(!i.hasNext()) {
            throw new Renov8Exception("No PackSpecLoader found on the classpath");
        }
        final PackSpecLoader loader = i.next();
        if(!i.hasNext()) {
            return Collections.singletonList(loader);
        }
        final List<PackSpecLoader> loaders = new ArrayList<>();
        loaders.add(loader);
        while (i.hasNext()) {
            loaders.add(i.next());
        }
        return loaders;
    }

    public static InstallSpecResolver newInstance() {
        return new InstallSpecResolver();
    }

    private List<PackSpecLoader> specLoaders = Collections.emptyList();
    private Map<String, ProducerRef> producers = new HashMap<>();
    private final List<ProducerRef> visited = new ArrayList<>();

    protected InstallSpecResolver() {
    }

    public InstallSpecResolver addPackSpecLoader(PackSpecLoader loader) {
        if(specLoaders.isEmpty()) {
            specLoaders = Collections.singletonList(loader);
            return this;
        }
        if(specLoaders.size() == 1) {
            specLoaders = new ArrayList<>(specLoaders);
        }
        specLoaders.add(loader);
        return this;
    }

    public ResolvedInstall resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }

        if(specLoaders == null) {
            specLoaders = getDefaultSpecLoaders();
        }

        resolveDeps(null, config.getPacks());

        final ResolvedInstall.Builder specBuilder = ResolvedInstall.builder();
        for(PackConfig packConfig : config.getPacks()) {
            addResolvedPack(specBuilder, producers.get(packConfig.getLocation().getPackId().getProducer()));
        }
        return specBuilder.build();
    }

    private void addResolvedPack(ResolvedInstall.Builder installBuilder, ProducerRef pRef) throws Renov8Exception {
        final ResolvedPack.Builder packBuilder = ResolvedPack.builder(pRef.spec.getLocation());
        if(pRef.hasDeps()) {
            pRef.setFlag(ProducerRef.VISITED);
            for(ProducerRef depRef : pRef.getDeps()) {
                packBuilder.addDependency(depRef.getPackId().getProducer());
                if(!depRef.isFlagOn(ProducerRef.ORDERED) && !depRef.isFlagOn(ProducerRef.VISITED)) {
                    addResolvedPack(installBuilder, depRef);
                }
            }
            pRef.clearFlag(ProducerRef.VISITED);
        }
        pRef.setFlag(ProducerRef.ORDERED);
        installBuilder.addPack(packBuilder.build());
    }

    private void resolveDeps(ProducerRef parentRef, List<PackConfig> depConfigs) throws Renov8Exception {
        final int visitedOffset = visited.size();
        int i = 0;
        while (i < depConfigs.size()) {
            final PackLocation pLoc = depConfigs.get(i++).getLocation();
            ProducerRef depRef = producers.get(pLoc.getPackId().getProducer());
            if(depRef == null) {
                depRef = new ProducerRef(pLoc.getPackId().getProducer(), loadPack(pLoc));
                producers.put(pLoc.getPackId().getProducer(), depRef);
                depRef.setFlag(ProducerRef.VISITED);
                visited.add(depRef);
            } else if(depRef.isFlagOn(ProducerRef.VISITED) ||
                    depRef.getPackId().getVersion().equals(pLoc.getPackId().getVersion())) {
                parentRef.addDepRef(depRef);
            } else {
                throw new Renov8Exception(depRef.getPackId().getProducer() + " version conflict: " + depRef.getPackId().getVersion() + " vs " + pLoc.getPackId().getVersion());
            }
        }
        if(visited.size() == visitedOffset) {
            return;
        }
        i = visitedOffset;
        while (i < visited.size()) {
            final ProducerRef depRef = visited.get(i++);
            if (depRef.spec.hasDependencies()) {
                resolveDeps(depRef, depRef.spec.getDependencies());
            }
            if (parentRef != null) {
                parentRef.addDepRef(depRef);
            }
        }
        i = visited.size();
        while (i > visitedOffset) {
            visited.remove(--i).clearFlag(ProducerRef.VISITED);
        }
    }

    protected PackSpec loadPack(PackLocation location) throws Renov8Exception {
        for(PackSpecLoader loader : specLoaders) {
            final PackSpec packSpec = loader.loadSpec(location);
            if(packSpec != null) {
                return packSpec;
            }
        }
        throw new Renov8Exception("Failed to locate spec for " + location);
    }
}
