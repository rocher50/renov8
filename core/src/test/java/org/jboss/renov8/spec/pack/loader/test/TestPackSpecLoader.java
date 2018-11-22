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

package org.jboss.renov8.spec.pack.loader.test;

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.pack.spec.loader.PackSpecLoader;
import org.jboss.renov8.spec.PackSpec;
import org.jboss.renov8.spec.PackSpecBuilder;

/**
 *
 * @author Alexey Loubyansky
 */
public class TestPackSpecLoader implements PackSpecLoader {

    @Override
    public PackSpec loadSpec(PackLocation location) throws Renov8Exception {
        System.out.println("Loading pack spec for " + location);
        final PackSpecBuilder builder = new PackSpecBuilder();
        builder.setLocation(location);
        builder.setId(location.getCoords());
        builder.setVersion("latest");
        return builder.build();
    }
}
