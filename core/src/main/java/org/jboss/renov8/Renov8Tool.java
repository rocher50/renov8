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

package org.jboss.renov8;

import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.pack.spec.InstallSpec;
import org.jboss.renov8.pack.spec.PackSpec;
import org.jboss.renov8.pack.spec.PackSpecLoader;
import org.jboss.renov8.spec.resolver.InstallSpecResolver;

/**
 *
 * @author Alexey Loubyansky
 */
public class Renov8Tool<P extends PackSpec> {

    public static <P extends PackSpec> Renov8Tool<P> newInstance() {
        return new Renov8Tool<P>();
    }

    private PackSpecLoader<P> packLoader;

    public Renov8Tool<P> setPackSpecLoader(PackSpecLoader<P> packLoader) {
        this.packLoader = packLoader;
        return this;
    }

    public InstallSpec<P> resolveConfig(InstallConfig config) throws Renov8Exception {
        return InstallSpecResolver.newInstance(packLoader).resolve(config);
    }
}
