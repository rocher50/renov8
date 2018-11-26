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

package org.jboss.renov8.install.config.resolved.basic.test;

import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.install.config.resolved.test.ResolvedInstallTestBase;
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.resolved.ResolvedPack;
import org.jboss.renov8.test.StrVersion;
import org.jboss.renov8.test.TestPackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
public class SinglePackWoDepsTest extends ResolvedInstallTestBase {

    @Override
    protected void initPackSpecs() throws Exception {
        writePackSpec(TestPackSpec.builder(new PackLocation(new PackId("org.test:test", new StrVersion("1.0.0.GA")))).build());
    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(new PackLocation(new PackId("org.test:test", new StrVersion("1.0.0.GA")))))
                .build();
    }

    @Override
    protected ResolvedInstall resolvedInstall() {
        return ResolvedInstall.builder()
                .addPack(ResolvedPack.builder(new PackLocation(new PackId("org.test:test", new StrVersion("1.0.0.GA")))).build())
                .build();
    }
}
