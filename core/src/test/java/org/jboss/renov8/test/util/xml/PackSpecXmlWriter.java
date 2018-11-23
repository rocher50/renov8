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

import javax.xml.stream.XMLStreamException;

import org.jboss.renov8.config.PackConfig;
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.test.util.xml.PackSpecXmlParser10.Element;
import org.jboss.renov8.xml.BaseXmlWriter;
import org.jboss.renov8.xml.ElementNode;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackSpecXmlWriter extends BaseXmlWriter<PackSpec> {

    private static PackSpecXmlWriter instance;

    public static PackSpecXmlWriter getInstance() {
        return instance == null ? instance = new PackSpecXmlWriter() : instance;
    }

    private PackSpecXmlWriter() {
    }

    @Override
    protected ElementNode toElement(PackSpec spec) throws XMLStreamException {
        final ElementNode specEl = addElement(null, Element.PACK_SPEC);

        writeLocation(addElement(specEl, Element.LOCATION), spec.getLocation());

        if(spec.hasDependencies()) {
            final ElementNode depsEl = addElement(specEl, Element.DEPS);
            for(PackConfig config : spec.getDependencies()) {
                writeLocation(addElement(depsEl, Element.DEP), config.getLocation());
            }
        }

        return specEl;
    }

    private void writeLocation(final ElementNode locEl, final PackLocation loc) {
        final PackId packId = loc.getPackId();
        addElement(locEl, Element.PRODUCER).addElementText(packId.getProducer());
        addElement(locEl, Element.VERSION).addElementText(packId.getVersion().toString());
        if(loc.getRepoType() != null) {
            addElement(locEl, Element.REPO_TYPE).addElementText(loc.getRepoType());
        }
    }
}
