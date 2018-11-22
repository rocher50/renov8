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
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.pack.spec.loader.PackSpecLoader;
import org.jboss.renov8.spec.InstallSpec;
import org.jboss.renov8.spec.PackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
public class InstallSpecResolver {

    private static InstallSpecResolver instance;

    public static synchronized InstallSpecResolver getInstance() throws Renov8Exception {
        return instance == null ? instance = new InstallSpecResolver() : instance;
    }

    private List<PackSpecLoader> loaders;

    protected InstallSpecResolver() throws Renov8Exception {
        final Iterator<PackSpecLoader> i = ServiceLoader.load(PackSpecLoader.class).iterator();
        if(!i.hasNext()) {
            throw new Renov8Exception("No PackSpecLoader found on the classpath");
        }
        PackSpecLoader loader = i.next();
        if(i.hasNext()) {
            loaders = new ArrayList<>();
            loaders.add(loader);
            while(i.hasNext()) {
                loaders.add(i.next());
            }
        } else {
            loaders = Collections.singletonList(loader);
        }
    }

    public InstallSpec resolve(InstallConfig config) throws Renov8Exception {
        if(!config.hasPacks()) {
            throw new Renov8Exception("Config is empty");
        }
        final InstallSpec.Builder specBuilder = InstallSpec.builder();
        for(PackConfig packConfig : config.getPacks()) {
            specBuilder.addPack(resolvePack(packConfig.getLocation()));
        }
        return specBuilder.build();
    }

    protected PackSpec resolvePack(PackLocation location) throws Renov8Exception {
        for(PackSpecLoader loader : loaders) {
            final PackSpec packSpec = loader.loadSpec(location);
            if(packSpec != null) {
                return packSpec;
            }
        }
        throw new Renov8Exception("Failed to load component spec");
    }
}
