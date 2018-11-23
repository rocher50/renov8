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

package org.jboss.renov8.install.config.resolved.override.lastresolved.test;

import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.install.config.resolved.test.ResolvedInstallTestBase;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.resolved.ResolvedPack;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.spec.resolver.PackVersionOverridePolicy;
import org.jboss.renov8.test.StrVersion;

/**
 *
 * @author Alexey Loubyansky
 */
public class LastResolvedTest extends ResolvedInstallTestBase {

    private static final PackLocation A_1 = PackLocation.create("A", new StrVersion("1"));
    private static final PackLocation B_1 = PackLocation.create("B", new StrVersion("1"));
    private static final PackLocation B_2 = PackLocation.create("B", new StrVersion("2"));
    private static final PackLocation C_1 = PackLocation.create("C", new StrVersion("1"));
    private static final PackLocation D_1 = PackLocation.create("D", new StrVersion("1"));
    private static final PackLocation E_1 = PackLocation.create("E", new StrVersion("1"));
    private static final PackLocation E_2 = PackLocation.create("E", new StrVersion("2"));
    private static final PackLocation F_1 = PackLocation.create("F", new StrVersion("1"));
    private static final PackLocation G_1 = PackLocation.create("G", new StrVersion("1"));

    @Override
    protected PackVersionOverridePolicy versionOverride() {
        return PackVersionOverridePolicy.LAST_RESOLVED;
    }

    @Override
    protected void initPackSpecs() throws Exception {

        writePackSpec(PackSpec.builder(A_1)
                .addDependency(B_2)
                .build());

        writePackSpec(PackSpec.builder(B_1)
                .addDependency(E_1)
                .build());

        writePackSpec(PackSpec.builder(B_2)
                .addDependency(E_2)
                .build());

        writePackSpec(PackSpec.builder(C_1)
                .addDependency(F_1)
                .build());

        writePackSpec(PackSpec.builder(D_1)
                .addDependency(G_1)
                .build());

        writePackSpec(PackSpec.builder(E_1).build());
        writePackSpec(PackSpec.builder(E_2).build());

        writePackSpec(PackSpec.builder(F_1).build());

        writePackSpec(PackSpec.builder(G_1)
                .addDependency(F_1)
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
    protected ResolvedInstall resolvedInstall() {
        return ResolvedInstall.builder()
                .addPack(ResolvedPack.forLocation(E_1))

                .addPack(ResolvedPack.builder(B_1)
                        .addDependency(E_1.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(A_1)
                        .addDependency(B_1.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.forLocation(F_1))

                .addPack(ResolvedPack.builder(G_1)
                        .addDependency(F_1.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(D_1)
                        .addDependency(G_1.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(C_1)
                        .addDependency(F_1.getPackId().getProducer())
                        .build())
                .build();
    }
}