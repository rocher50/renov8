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

    public static InstallSpecResolver newInstance() throws Renov8Exception {
        return newInstance(getDefaultSpecLoaders());
    }

    public static InstallSpecResolver newInstance(List<PackSpecLoader> specLoaders) throws Renov8Exception {
        if(specLoaders.isEmpty()) {
            throw new Renov8Exception("No PackSpecLoader configured");
        }
        return new InstallSpecResolver(specLoaders);
    }

    private List<PackSpecLoader> specLoaders;
    private PackVersionOverridePolicy versionPolicy = PackVersionOverridePolicy.FIRST_RESOLVED;
    private Map<String, ProducerRef> producers = new HashMap<>();

    protected InstallSpecResolver(List<PackSpecLoader> specLoaders) throws Renov8Exception {
        this.specLoaders = specLoaders;
    }

    public ResolvedInstall resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }

        for(PackConfig packConfig : config.getPacks()) {
            resolvePack(null, packConfig);
        }

        final ResolvedInstall.Builder specBuilder = ResolvedInstall.builder();
        for(PackConfig packConfig : config.getPacks()) {
            addResolvedPack(specBuilder, producers.get(packConfig.getLocation().getPackId().getProducer()));
        }
        return specBuilder.build();
    }

    private void addResolvedPack(ResolvedInstall.Builder installBuilder, ProducerRef pRef) throws Renov8Exception {
        final PackSpec spec = pRef.getSpec();
        final ResolvedPack.Builder packBuilder = ResolvedPack.builder(spec.getLocation());
        if(pRef.hasDeps()) {
            pRef.setFlag(ProducerRef.RESOLVE_BRANCH);
            for(ProducerRef depRef : pRef.getDeps()) {
                packBuilder.addDependency(depRef.getPackId().getProducer());
                if(!depRef.isFlagOn(ProducerRef.RESOLVE_BRANCH)) {
                    addResolvedPack(installBuilder, depRef);
                }
            }
            pRef.clearFlag(ProducerRef.RESOLVE_BRANCH);
        }
        installBuilder.addPack(packBuilder.build());
    }

    private boolean resolvePack(ProducerRef parent, PackConfig packConfig) throws Renov8Exception {
        final PackLocation pLoc = packConfig.getLocation();
        ProducerRef pRef = producers.get(pLoc.getPackId().getProducer());
        if(pRef == null) {
            pRef = new ProducerRef(pLoc.getPackId().getProducer());
            producers.put(pLoc.getPackId().getProducer(), pRef);
        }
        if(!pRef.hasVersion()) {
            pRef.setSpec(loadPack(pLoc));
            pRef.increase();
            pRef.setFlag(ProducerRef.RESOLVE_BRANCH);
            if(parent != null) {
                parent.addDep(pRef);
            }
        } else if(!versionPolicy.override(pRef.getPackId(), pLoc.getPackId().getVersion())) {
            pRef.increase();
            if(parent != null) {
                parent.addDep(pRef);
            }
            return true;
        } else {
            pRef.reset();
            pRef.setSpec(loadPack(pLoc));
            pRef.increase();
            if(!pRef.setFlag(ProducerRef.RESOLVE_BRANCH)) {
                pRef.setFlag(ProducerRef.RERESOLVE_BRANCH);
                return false;
            }
        }

        if (!pRef.getSpec().hasDependencies()) {
            pRef.clearFlag(ProducerRef.RESOLVE_BRANCH);
            return true;
        }

        if(pRef.getSpec().hasDependencies()) {
            List<PackConfig> deps = pRef.getSpec().getDependencies();
            while (true) {
                int i = 0;
                while (i < deps.size()) {
                    if (!resolvePack(pRef, deps.get(i++))) {
                        pRef.decrease();
                        return false;
                    }
                }
                if (pRef.isFlagOn(ProducerRef.RERESOLVE_BRANCH)) {
                    pRef.clearFlag(ProducerRef.RERESOLVE_BRANCH);
                    deps = pRef.getSpec().getDependencies();
                    if (!deps.isEmpty()) {
                        continue;
                    }
                }
                break;
            }
        }

        pRef.clearFlag(ProducerRef.RESOLVE_BRANCH);
        return true;
    }

    protected PackSpec loadPack(PackLocation location) throws Renov8Exception {
        for(PackSpecLoader loader : specLoaders) {
            final PackSpec packSpec = loader.loadSpec(location);
            if(packSpec != null) {
                return packSpec;
            }
        }
        throw new Renov8Exception("Failed to load component spec");
    }
}
