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

package org.jboss.renov8.config.resolved.basic.test;

import org.jboss.renov8.PackLocation;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.config.resolved.test.ResolvedSpecTestBase;
import org.jboss.renov8.spec.InstallSpec;
import org.jboss.renov8.test.StrVersion;
import org.jboss.renov8.test.TestPack;

/**
 *
 * @author Alexey Loubyansky
 */
public class SinglePackWithDepsTest extends ResolvedSpecTestBase {

    private static final PackConfig PROD_3_CONFIG = PackConfig.forLocation(PackLocation.create("producer3", new StrVersion("1.0.0.GA")));
    private static final PackConfig PROD_2_CONFIG = PackConfig.forLocation(PackLocation.create("producer2", new StrVersion("1.0.0.GA")));
    private static final PackConfig PROD_4_CONFIG = PackConfig.forLocation(PackLocation.create("producer4", new StrVersion("1.0.0.GA")));

    @Override
    protected void createPacks() throws Exception {
        createPack(TestPack.builder(PackLocation.create("producer1", new StrVersion("1.0.0.GA")))
                .addDependency(PROD_2_CONFIG)
                .addDependency(PROD_3_CONFIG)
                .build());
        createPack(TestPack.builder(PackLocation.create("producer2", new StrVersion("1.0.0.GA")))
                .addDependency(PROD_4_CONFIG)
                .build());
        createPack(TestPack.builder(PackLocation.create("producer3", new StrVersion("1.0.0.GA")))
                .build());
        createPack(TestPack.builder(PackLocation.create("producer4", new StrVersion("1.0.0.GA")))
                .build());
    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(PackLocation.create("producer1", new StrVersion("1.0.0.GA"))))
                .build();
    }

    @Override
    protected InstallSpec<TestPack> installSpec() {
        return InstallSpec.<TestPack>builder()
                .addPack(TestPack.builder(PackLocation.create("producer4", new StrVersion("1.0.0.GA")))
                        .build())
                .addPack(TestPack.builder(PackLocation.create("producer2", new StrVersion("1.0.0.GA")))
                        .addDependency(PROD_4_CONFIG)
                        .build())
                .addPack(TestPack.builder(PackLocation.create("producer3", new StrVersion("1.0.0.GA")))
                        .build())
                .addPack(TestPack.builder(PackLocation.create("producer1", new StrVersion("1.0.0.GA")))
                        .addDependency(PROD_2_CONFIG)
                        .addDependency(PROD_3_CONFIG)
                        .build())
                .build();
    }
}
