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

package org.jboss.renov8.spec;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.renov8.pack.PackLocation;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackSpecBuilder {

    protected PackLocation location;
    protected String id;
    protected String version;
    protected Set<String> deps = new LinkedHashSet<>(0);

    public PackSpecBuilder() {
    }

    public PackSpecBuilder setLocation(PackLocation location) {
        this.location = location;
        return this;
    }

    public PackSpecBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public PackSpecBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public PackSpecBuilder addDependency(String id) {
        deps.add(id);
        return this;
    }

    public PackSpec build() {
        return new PackSpec(this);
    }
}
