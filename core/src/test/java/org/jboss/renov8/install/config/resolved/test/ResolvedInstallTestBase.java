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

package org.jboss.renov8.install.config.resolved.test;

import static org.junit.Assert.assertEquals;

import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.resolved.ResolvedInstall;
import org.jboss.renov8.test.Renov8TestBase;
import org.junit.Test;

/**
 *
 * @author Alexey Loubyansky
 */
public abstract class ResolvedInstallTestBase extends Renov8TestBase {

    @Test
    public void test() throws Exception {
        final ResolvedInstall actual = tool.resolveConfig(installConfig());
        final ResolvedInstall expected = resolvedInstall();

        assertEquals(expected.toString(), actual.toString());
        /*
        final Map<String, ResolvedPack> actualPacks = actual.getPacks();
        final Map<String, ResolvedPack> expectedPacks = expected.getPacks();

        if(actualPacks.size() != expectedPacks.size()) {
            assertEquals(expectedPacks, actualPacks);
        }

        final Iterator<ResolvedPack> expectedOrder = expectedPacks.values().iterator();
        final Iterator<ResolvedPack> actualOrder = actualPacks.values().iterator();
        while(expectedOrder.hasNext()) {
            if(expectedOrder.next().equals(actualOrder.next())) {
                continue;
            }
        }
        */
    }

    protected abstract InstallConfig installConfig();

    protected abstract ResolvedInstall resolvedInstall();
}
