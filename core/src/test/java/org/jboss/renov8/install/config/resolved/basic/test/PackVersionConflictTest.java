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
import org.jboss.renov8.test.TestPack;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackVersionConflictTest extends ResolvedInstallTestBase {

    private static final PackLocation A_1 = location("A");
    private static final PackLocation B_1 = location("B");
    private static final PackLocation C_1 = location("C", "1");
    private static final PackLocation C_2 = location("C", "2");

    @Override
    protected void initPackSpecs() throws Exception {
        writePackSpec(TestPack.builder(A_1)
                .addDependency(PackConfig.forLocation(C_1))
                .build());

        writePackSpec(TestPack.builder(B_1)
                .addDependency(C_2)
                .build());

        writePackSpec(TestPack.builder(C_1).build());
        writePackSpec(TestPack.builder(C_2).build());
    }

    @Override
    protected InstallConfig installConfig() {
        return InstallConfig.builder()
                .addPack(PackConfig.forLocation(A_1))
                .addPack(PackConfig.forLocation(B_1))
                .build();
    }

    protected String[] errors() {
        return new String[] {"C version conflict: 1 vs 2"};
    }
}
