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
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.pack.spec.InstallSpec;
import org.jboss.renov8.test.StrVersion;
import org.jboss.renov8.test.TestPack;

/**
 *
 * @author Alexey Loubyansky
 */
public class DependencyOverridesConfiguredOrderingTest extends ResolvedInstallTestBase {

    private static final PackLocation A_1 = PackLocation.create("A", new StrVersion("1"));
    private static final PackLocation B_1 = PackLocation.create("B", new StrVersion("1"));
    private static final PackLocation C_1 = PackLocation.create("C", new StrVersion("1"));
    private static final PackLocation D_1 = PackLocation.create("D", new StrVersion("1"));

    private TestPack A_1_SPEC;
    private TestPack B_1_SPEC;
    private TestPack C_1_SPEC;
    private TestPack D_1_SPEC;

    @Override
    protected void initPackSpecs() throws Exception {
        A_1_SPEC = writePackSpec(TestPack.builder(A_1)
                .addDependency(PackConfig.forLocation(B_1))
                .build());

        B_1_SPEC = writePackSpec(TestPack.builder(B_1).build());

        C_1_SPEC = writePackSpec(TestPack.builder(C_1).build());

        D_1_SPEC = writePackSpec(TestPack.builder(D_1)
                .addDependency(PackConfig.forLocation(C_1))
                .build());
    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(A_1))
                .addPack(PackConfig.forLocation(D_1))
                .addPack(PackConfig.forLocation(B_1))
                .addPack(PackConfig.forLocation(C_1))
                .build();
    }

    @Override
    protected InstallSpec<TestPack> installSpec() {
        return InstallSpec.<TestPack>builder()
                .addPack(B_1_SPEC)
                .addPack(A_1_SPEC)
                .addPack(C_1_SPEC)
                .addPack(D_1_SPEC)
                .build();
    }
}
