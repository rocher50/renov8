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
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.resolved.ResolvedPack;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.test.StrVersion;

/**
 *
 * @author Alexey Loubyansky
 */
public class CircularDepsTest extends ResolvedInstallTestBase {

    private static final PackLocation prod1 = PackLocation.create("producer1", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod2 = PackLocation.create("producer2", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod3 = PackLocation.create("producer3", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod4 = PackLocation.create("producer4", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod5 = PackLocation.create("producer5", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod6 = PackLocation.create("producer6", new StrVersion("1.0.0.GA"));
    private static final PackLocation prod7 = PackLocation.create("producer7", new StrVersion("1.0.0.GA"));

    @Override
    protected void initPackSpecs() throws Exception {

        writePackSpec(PackSpec.builder(prod1)
                .addDependency(prod2)
                .build());

        writePackSpec(PackSpec.builder(prod2)
                .addDependency(prod3)
                .build());

        writePackSpec(PackSpec.builder(prod3)
                .addDependency(prod4)
                .build());

        writePackSpec(PackSpec.builder(prod4)
                .addDependency(prod5)
                .build());

        writePackSpec(PackSpec.builder(prod5)
                .addDependency(prod6)
                .build());

        writePackSpec(PackSpec.builder(prod6)
                .addDependency(prod7)
                .build());

        writePackSpec(PackSpec.builder(prod7)
                .addDependency(prod1)
                .build());

    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(prod1))
                .build();
    }

    @Override
    protected ResolvedInstall resolvedInstall() {
        return ResolvedInstall.builder()
                .addPack(ResolvedPack.builder(prod7)
                        .addDependency(prod1.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod6)
                        .addDependency(prod7.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod5)
                        .addDependency(prod6.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod4)
                        .addDependency(prod5.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod3)
                        .addDependency(prod4.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod2)
                        .addDependency(prod3.getPackId().getProducer())
                        .build())

                .addPack(ResolvedPack.builder(prod1)
                        .addDependency(prod2.getPackId().getProducer())
                        .build())
                .build();
    }
}
