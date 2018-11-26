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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.renov8.PackLocation;
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
    private final List<ProducerRef<P>> visited = new ArrayList<>();

    protected InstallSpecResolver(PackResolver<P> packResolver) {
        this.packResolver = packResolver;
    }

    public InstallSpec<P> resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }

        resolveDeps(null, config.getPacks());

        final InstallSpec.Builder<P> specBuilder = InstallSpec.builder();
        for(PackConfig packConfig : config.getPacks()) {
            addResolvedPack(specBuilder, producers.get(packConfig.getLocation().getProducer()));
        }
        return specBuilder.build();
    }

    private void addResolvedPack(InstallSpec.Builder<P> installBuilder, ProducerRef<P> pRef) throws Renov8Exception {
        if(pRef.spec.hasDependencies()) {
            pRef.setFlag(ProducerRef.VISITED);
            for(ProducerRef<P> depRef : pRef.getDeps()) {
                if(!depRef.isFlagOn(ProducerRef.ORDERED) && !depRef.isFlagOn(ProducerRef.VISITED)) {
                    addResolvedPack(installBuilder, depRef);
                }
            }
            pRef.clearFlag(ProducerRef.VISITED);
        }
        pRef.setFlag(ProducerRef.ORDERED);
        installBuilder.addPack(pRef.spec);
    }

    private void resolveDeps(ProducerRef<P> parentRef, List<PackConfig> depConfigs) throws Renov8Exception {
        final int visitedOffset = visited.size();
        int i = 0;
        while (i < depConfigs.size()) {
            final PackLocation pLoc = depConfigs.get(i++).getLocation();
            ProducerRef<P> depRef = producers.get(pLoc.getProducer());
            if(depRef == null) {
                depRef = new ProducerRef<P>(pLoc.getProducer(), packResolver.resolve(pLoc));
                producers.put(pLoc.getProducer(), depRef);
                depRef.setFlag(ProducerRef.VISITED);
                visited.add(depRef);
            } else if(depRef.isFlagOn(ProducerRef.VISITED) ||
                    depRef.spec.getLocation().getVersion().equals(pLoc.getVersion())) {
                parentRef.addDepRef(depRef);
            } else {
                throw new Renov8Exception(depRef.producer + " version conflict: " + depRef.spec.getLocation().getVersion() + " vs " + pLoc.getVersion());
            }
        }
        if(visited.size() == visitedOffset) {
            return;
        }
        i = visitedOffset;
        while (i < visited.size()) {
            final ProducerRef<P> depRef = visited.get(i++);
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
}
