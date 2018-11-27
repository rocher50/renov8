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

package org.jboss.renov8.config.resolved.transitive.test;

import org.jboss.renov8.PackLocation;
import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.config.resolved.test.ResolvedSpecTestBase;
import org.jboss.renov8.spec.InstallSpec;
import org.jboss.renov8.test.TestPack;

/**
 *
 * @author Alexey Loubyansky
 */
public class TransitiveDepVersionUpgradeTest extends ResolvedSpecTestBase {

    private static final PackLocation A_1 = location("A");
    private static final PackLocation B_1 = location("B");
    private static final PackLocation B_2 = location("B", "2");
    private static final PackLocation B_3 = location("B", "3");
    private static final PackLocation C_1 = location("C");
    private static final PackLocation D_1 = location("D");
    private static final PackLocation E_1 = location("E");

    @Override
    protected void createPacks() throws Exception {
        createPack(TestPack.builder(A_1)
                .addDependency(PackConfig.forLocation(B_1))
                .addDependency(PackConfig.forLocation(C_1))
                .build());

        createPack(TestPack.builder(B_1)
                .addDependency(PackConfig.forLocation(D_1))
                .build());
        createPack(TestPack.builder(B_2)
                .addDependency(PackConfig.forLocation(E_1))
                .build());
        createPack(TestPack.builder(B_3)
                .addDependency(PackConfig.forLocation(E_1))
                .build());

        createPack(TestPack.builder(C_1)
                .build());

        // To make sure it's not even loaded
        //createPack(TestPack.builder(D_1)
        //        .build());

        createPack(TestPack.builder(E_1)
                .build());
    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(A_1))
                .build();
    }

    @Override
    protected String[] resolveLatest() {
        return new String[] {"B"};
    }

    @Override
    protected InstallSpec<TestPack> installSpec() {
        return InstallSpec.<TestPack>builder()
                .addPack(TestPack.builder(E_1)
                        .build())
                .addPack(TestPack.builder(B_3)
                        .addDependency(PackConfig.forLocation(E_1))
                        .build())
                .addPack(TestPack.builder(C_1)
                        .build())
                .addPack(TestPack.builder(A_1)
                        .addDependency(PackConfig.forLocation(B_1))
                        .addDependency(PackConfig.forLocation(C_1))
                        .build())
                .build();
    }
}
