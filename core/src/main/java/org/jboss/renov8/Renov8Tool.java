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

import java.util.Arrays;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.installer.InstallContext;
import org.jboss.renov8.installer.InstallSpecHandler;
import org.jboss.renov8.pack.spec.loader.PackSpecLoader;
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.spec.resolver.InstallSpecResolver;
import org.jboss.renov8.spec.resolver.PackVersionOverridePolicy;

/**
 *
 * @author Alexey Loubyansky
 */
public class Renov8Tool {

    public static Renov8Tool newInstance() {
        return new Renov8Tool();
    }

    private InstallSpecResolver specResolver = InstallSpecResolver.newInstance();

    public Renov8Tool addPackSpecLoader(PackSpecLoader loader) {
        specResolver.addPackSpecLoader(loader);
        return this;
    }

    public Renov8Tool setVersionOverridePolicy(PackVersionOverridePolicy policy) {
        specResolver.setVersionOverridePolicy(policy);
        return this;
    }

    public Renov8Tool setHierarchicalDepsResolver(boolean hierarchicalDepsResolver) {
        specResolver.setHierarchicalResolution(hierarchicalDepsResolver);
        return this;
    }

    public ResolvedInstall resolveConfig(InstallConfig config) throws Renov8Exception {
        return specResolver.resolve(config);
    }

    public ResolvedInstall install(InstallConfig config, InstallSpecHandler... handlers) throws Renov8Exception {
        final ResolvedInstall spec = resolveConfig(config);
        InstallContext.newInstance(spec, Arrays.asList(handlers)).install();
        return spec;
    }
}
