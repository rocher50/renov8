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

package org.jboss.renov8.test.util.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.test.TestPackSpec;
import org.jboss.renov8.xml.XmlParsers;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackSpecXmlParser {

    private static PackSpecXmlParser instance;

    public static PackSpecXmlParser getInstance() {
        return instance == null ? instance = new PackSpecXmlParser() : instance;
    }

    public static PackSpec parse(Path p) throws IOException, XMLStreamException {
        try(BufferedReader reader = Files.newBufferedReader(p)) {
            return getInstance().parse(reader);
        }
    }

    private PackSpecXmlParser() {
        new PackSpecXmlParser10().plugin(XmlParsers.getInstance());
    }

    public PackSpec parse(final Reader input) throws XMLStreamException {
        final TestPackSpec.Builder builder = TestPackSpec.builder();
        XmlParsers.parse(input, builder);
        return builder.build();
    }
}
