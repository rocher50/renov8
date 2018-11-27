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

package org.jboss.renov8.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.renov8.PackLocation;
import org.jboss.renov8.PackVersion;
import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.spec.InstallSpec;
import org.jboss.renov8.spec.PackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
public class InstallSpecResolver<P extends PackSpec> {

    public static <P extends PackSpec> InstallSpecResolver<P> newInstance(PackResolver<P> packLoader) {
        return new InstallSpecResolver<P>(packLoader);
    }

    private PackResolver<P> packResolver;
    private Map<String, ProducerRef<P>> producers = new HashMap<>();
    private Set<String> resolveLatest = Collections.emptySet();
    private final List<ProducerRef<P>> visited = new ArrayList<>();

    protected InstallSpecResolver(PackResolver<P> packResolver) {
        this.packResolver = packResolver;
    }

    public InstallSpec<P> resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }
        return doResolve(config);
    }

    public InstallSpec<P> resolveLatest(InstallConfig config, String... producers) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }

        switch(producers.length) {
            case 0:
                final List<PackConfig> packs = config.getPacks();
                resolveLatest = new HashSet<>(packs.size());
                for(int i = 0; i < packs.size(); ++i) {
                    resolveLatest.add(packs.get(i).getLocation().getProducer());
                }
                break;
            case 1:
                resolveLatest = Collections.singleton(producers[0]);
                break;
            default:
                resolveLatest = new HashSet<>(producers.length);
                for(String producer : producers) {
                    resolveLatest.add(producer);
                }
        }

        return doResolve(config);
    }

    private InstallSpec<P> doResolve(InstallConfig config) throws Renov8Exception {
        resolveDeps(null, config.getPacks());

        final InstallSpec.Builder<P> specBuilder = InstallSpec.builder();
        for(PackConfig packConfig : config.getPacks()) {
            final ProducerRef<P> pRef = this.producers.get(packConfig.getLocation().getProducer());
            if(!packConfig.isTransitive()) {
                addResolvedPack(specBuilder, pRef);
            }
        }
        return specBuilder.build();
    }

    private void addResolvedPack(InstallSpec.Builder<P> installBuilder, ProducerRef<P> pRef) throws Renov8Exception {
        if(pRef.hasDependencies()) {
            pRef.setFlag(ProducerRef.VISITED);
            for(ProducerRef<P> depRef : pRef.getDependencies()) {
                if(!depRef.isFlagOn(ProducerRef.ORDERED) && !depRef.isFlagOn(ProducerRef.VISITED)) {
                    addResolvedPack(installBuilder, depRef);
                }
            }
            pRef.clearFlag(ProducerRef.VISITED);
        }
        pRef.setFlag(ProducerRef.ORDERED);
        installBuilder.addPack(pRef.getSpec());
    }

    private void resolveDeps(ProducerRef<P> parent, List<PackConfig> depConfigs) throws Renov8Exception {
        final int visitedOffset = visited.size();
        int i = 0;
        while (i < depConfigs.size()) {
            final PackConfig pConfig = depConfigs.get(i);
            PackLocation pLoc = pConfig.getLocation();
            ProducerRef<P> depRef = producers.get(pLoc.getProducer());
            if(depRef == null) {
                if(resolveLatest.contains(pLoc.getProducer())) {
                    final PackVersion latestVersion = packResolver.getLatestVersion(pLoc);
                    if(!latestVersion.equals(pLoc.getVersion())) {
                        pLoc = new PackLocation(pLoc.getRepoId(), pLoc.getProducer(), pLoc.getChannel(), pLoc.getFrequency(), latestVersion);
                    }
                }

                depRef = new ProducerRef<P>(pLoc);
                if(parent != null || !pConfig.isTransitive()) {
                    depRef.setSpec(packResolver.resolve(pLoc));
                    visited.add(depRef);
                }
                producers.put(pLoc.getProducer(), depRef);
                depRef.setFlag(ProducerRef.VISITED);
            } else if(depRef.isFlagOn(ProducerRef.VISITED)) {
                if(depRef.isLoaded()) {
                    parent.setDependency(i, depRef);
                } else {
                    // relevant root transitive dependency
                    depRef.setSpec(packResolver.resolve(depRef.location));
                    visited.add(depRef);
                }
            } else if(depRef.location.getVersion().equals(pLoc.getVersion())) {
                parent.setDependency(i, depRef);
            } else {
                throw new Renov8Exception(depRef.location.getProducer() + " version conflict: " + depRef.location.getVersion() + " vs " + pLoc.getVersion());
            }
            ++i;
        }
        if(visited.size() == visitedOffset) {
            return;
        }
        i = visitedOffset;
        int depIndex = -i;
        while (i < visited.size()) {
            final ProducerRef<P> depRef = visited.get(i);
            final List<PackConfig> deps = depRef.getSpec().getDependencies();
            if (!deps.isEmpty()) {
                resolveDeps(depRef, deps);
            }
            if (parent != null) {
                if(depIndex < 0) {
                    depIndex += i;
                }
                while(parent.isDependencySet(depIndex)) {
                    ++depIndex;
                }
                parent.setDependency(depIndex++, depRef);
            }
            ++i;
        }
        i = visited.size();
        while (i > visitedOffset) {
            visited.remove(--i).clearFlag(ProducerRef.VISITED);
        }
    }
}
