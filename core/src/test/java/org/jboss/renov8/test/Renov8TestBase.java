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

package org.jboss.renov8.test;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jboss.renov8.Renov8Tool;
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.test.util.xml.PackSpecXmlWriter;
import org.jboss.renov8.utils.IoUtils;
import org.jboss.renov8.utils.StringUtils;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Alexey Loubyansky
 */
public class Renov8TestBase {

    private Path workDir;
    private Path packSpecsDir;
    private TestPackSpecLoader packSpecLoader;
    protected Renov8Tool tool;

    @Before
    public void init() throws Exception {
        workDir = IoUtils.createRandomTmpDir();
        packSpecsDir = workDir.resolve("pack-specs");
        Files.createDirectories(packSpecsDir);
        packSpecLoader = new TestPackSpecLoader(packSpecsDir);
        tool = Renov8Tool.newInstance().addPackSpecLoader(packSpecLoader);
        initPackSpecs();
    }

    protected void initPackSpecs() throws Exception {
    }

    @After
    public void cleanup() {
        IoUtils.recursiveDelete(workDir);
    }

    protected void writePackSpec(PackSpec packSpec) throws Exception {
        final PackId id = packSpec.getLocation().getPackId();
        Path p = packSpecsDir.resolve(StringUtils.ensureValidFileName(id.getProducer()))
                .resolve(StringUtils.ensureValidFileName(id.getVersion().toString()));
        PackSpecXmlWriter.getInstance().write(packSpec, p);
    }

    protected PackSpec loadPackSpec(PackLocation location) throws Exception {
        return packSpecLoader.loadSpec(location);
    }
}