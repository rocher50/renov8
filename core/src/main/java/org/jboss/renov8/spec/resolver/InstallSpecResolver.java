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

    public interface DepsResolver {
        boolean resolveDeps(ProducerRef parent, List<PackConfig> depConfigs) throws Renov8Exception;
    }

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
    private PackVersionOverridePolicy versionPolicy = PackVersionOverridePolicy.FIRST_RESOLVED;
    private Map<String, ProducerRef> producers = new HashMap<>();
    private boolean hierarchicalDepsResolver;
    private DepsResolver depsResolver;

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

    public InstallSpecResolver setVersionOverridePolicy(PackVersionOverridePolicy policy) {
        this.versionPolicy = policy;
        return this;
    }

    public InstallSpecResolver setHierarchicalResolution(boolean hierarchicalResolver) {
        this.hierarchicalDepsResolver = hierarchicalResolver;
        return this;
    }

    public ResolvedInstall resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }

        depsResolver = hierarchicalDepsResolver ?
                new DepsResolver() {
                    @Override
                    public boolean resolveDeps(ProducerRef parent, List<PackConfig> depConfigs) throws Renov8Exception {
                        final List<ProducerRef> levelRefs = new ArrayList<>(depConfigs.size());
                        int i = 0;
                        while (i < depConfigs.size()) {
                            final ProducerRef depRef = resolveRef(parent, depConfigs.get(i++));
                            if(depRef == null) {
                                return false;
                            }
                            levelRefs.add(depRef);
                        }
                        for(ProducerRef depRef : levelRefs) {
                            if(depRef.isMissingDeps()) {
                                resolveMissingDeps(depRef);
                            }
                        }
                        return true;
                    }
                } : new DepsResolver() {
                    @Override
                    public boolean resolveDeps(ProducerRef parent, List<PackConfig> depConfigs) throws Renov8Exception {
                        int i = 0;
                        while (i < depConfigs.size()) {
                            if (!resolvePack(parent, depConfigs.get(i++))) {
                                return false;
                            }
                        }
                        return true;
                    }
                };

        if(specLoaders == null) {
            specLoaders = getDefaultSpecLoaders();
        }

        depsResolver.resolveDeps(null, config.getPacks());

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

    private boolean resolvePack(ProducerRef parent, PackConfig packConfig) throws Renov8Exception {
        final ProducerRef pRef = resolveRef(parent, packConfig);
        if(pRef == null) {
            return false;
        }

        if (pRef.isMissingDeps()) {
            if(!resolveMissingDeps(pRef)) {
                pRef.decrease();
                pRef.clearFlag(ProducerRef.VISITED);
                return false;
            }
        }

        pRef.clearFlag(ProducerRef.VISITED);
        return true;
    }

    private ProducerRef resolveRef(ProducerRef parent, PackConfig packConfig) throws Renov8Exception {
        final PackLocation pLoc = packConfig.getLocation();
        ProducerRef pRef = producers.get(pLoc.getPackId().getProducer());
        if(pRef == null) {
            pRef = new ProducerRef(pLoc.getPackId().getProducer());
            producers.put(pLoc.getPackId().getProducer(), pRef);
        }
        if(parent != null) {
            parent.addDep(pRef);
        }
        if(!pRef.hasVersion()) {
            pRef.setSpec(loadPack(pLoc));
            pRef.increase();
            pRef.setFlag(ProducerRef.VISITED);
        } else if(!versionPolicy.override(pRef.getPackId(), pLoc.getPackId().getVersion())) {
            pRef.increase();
        } else {
            pRef.reset();
            pRef.setSpec(loadPack(pLoc));
            pRef.increase();
            if(!pRef.setFlag(ProducerRef.VISITED)) {
                pRef.setFlag(ProducerRef.RERESOLVE_BRANCH);
                return null;
            }
        }
        return pRef;
    }

    private boolean resolveMissingDeps(ProducerRef parent) throws Renov8Exception {
        List<PackConfig> deps = parent.getSpec().getDependencies();
        while (true) {
            if(!depsResolver.resolveDeps(parent, deps)) {
                return false;
            }
            if (parent.isFlagOn(ProducerRef.RERESOLVE_BRANCH)) {
                parent.clearFlag(ProducerRef.RERESOLVE_BRANCH);
                deps = parent.getSpec().getDependencies();
                if (!deps.isEmpty()) {
                    continue;
                }
            }
            break;
        }
        return true;
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
