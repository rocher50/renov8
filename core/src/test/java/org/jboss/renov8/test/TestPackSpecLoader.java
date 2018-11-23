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

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.pack.spec.loader.PackSpecLoader;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.test.util.xml.PackSpecXmlParser;
import org.jboss.renov8.utils.StringUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class TestPackSpecLoader implements PackSpecLoader {

    private final Path baseDir;

    public TestPackSpecLoader(Path dir) {
        this.baseDir = dir;
    }

    @Override
    public PackSpec loadSpec(PackLocation location) throws Renov8Exception {
        final PackId id = location.getPackId();
        final Path p = baseDir.resolve(StringUtils.ensureValidFileName(id.getProducer()))
                .resolve(StringUtils.ensureValidFileName(id.getVersion().toString()));
        if(!Files.exists(p)) {
            return null;
        }
        System.out.println("Load spec " + location);
        try {
            return PackSpecXmlParser.parse(p);
        } catch (Exception e) {
            throw new Renov8Exception("Failed to parse pack-spec for " + location + " stored in " + p, e);
        }
    }
}
