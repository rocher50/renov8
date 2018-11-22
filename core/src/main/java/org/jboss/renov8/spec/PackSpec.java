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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jboss.renov8.pack.PackLocation;
import org.jboss.renov8.utils.StringUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackSpec {

    protected final PackLocation location;
    protected final String id;
    protected final String version;
    protected final List<String> deps;

    protected PackSpec(PackSpecBuilder builder) {
        this.location = builder.location;
        this.id = builder.id;
        this.version = builder.version;
        this.deps = builder.deps.isEmpty() ? Collections.emptyList() : Arrays.asList(builder.deps.toArray(new String[builder.deps.size()]));
    }

    public PackLocation getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasDependencies() {
        return !deps.isEmpty();
    }

    public List<String> getDependencies() {
        return deps;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deps == null) ? 0 : deps.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackSpec other = (PackSpec) obj;
        if (deps == null) {
            if (other.deps != null)
                return false;
        } else if (!deps.equals(other.deps))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(location);
        buf.append(" id=").append(id);
        buf.append(" version=").append(version);
        if(!deps.isEmpty()) {
            buf.append(" deps=");
            StringUtils.append(buf, deps);
        }
        return buf.append(']').toString();
    }
}
