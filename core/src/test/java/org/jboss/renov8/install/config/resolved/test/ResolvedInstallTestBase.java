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
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.jboss.renov8.config.InstallConfig;
import org.jboss.renov8.pack.spec.InstallSpec;
import org.jboss.renov8.test.Renov8TestBase;
import org.jboss.renov8.test.TestPack;
import org.junit.Test;

/**
 *
 * @author Alexey Loubyansky
 */
public abstract class ResolvedInstallTestBase extends Renov8TestBase {

    @Test
    public void test() throws Exception {
        final String[] errors = errors();
        final InstallSpec<TestPack> expected = installSpec();
        try {
            final InstallSpec<TestPack> actual = tool.resolveConfig(installConfig());
            if(errors != null) {
                fail("Expected failures: " + Arrays.asList(errors));
            }
            assertEquals(expected.toString(), actual.toString());
        } catch(AssertionError e) {
            throw e;
        } catch(Throwable t) {
            if (errors == null) {
                throw t;
            } else {
                assertErrors(t, errors);
            }
            if(expected != null) {
                fail("Expected install " + expected);
            }
        }
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

    protected String[] errors() {
        return null;
    }

    protected abstract InstallConfig installConfig();

    protected InstallSpec<TestPack> installSpec() {
        return null;
    }

    protected void assertErrors(Throwable t, String... msgs) {
        int i = 0;
        if(msgs != null) {
            while (t != null && i < msgs.length) {
                assertEquals(msgs[i++], t.getLocalizedMessage());
                t = t.getCause();
            }
        }
        if(t != null) {
            fail("Unexpected error: " + t.getLocalizedMessage());
        }
        if(i < msgs.length - 1) {
            fail("Not reported error: " + msgs[i]);
        }
    }
}
