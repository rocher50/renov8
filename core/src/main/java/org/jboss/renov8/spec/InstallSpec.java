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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.renov8.utils.StringUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class InstallSpec {

    public static class Builder {

        private Map<String, PackSpec> packs = new LinkedHashMap<>(0);

        protected Builder() {
        }

        public Builder addPack(PackSpec pack) {
            packs.put(pack.getId(), pack);
            return this;
        }

        public InstallSpec build() {
            return new InstallSpec(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Map<String, PackSpec> packs;

    protected InstallSpec(Builder builder) {
        packs = Collections.unmodifiableMap(builder.packs);
    }

    public boolean hasPacks() {
        return !packs.isEmpty();
    }

    public Map<String, PackSpec> getPacks() {
        return packs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packs == null) ? 0 : packs.hashCode());
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
        InstallSpec other = (InstallSpec) obj;
        if (packs == null) {
            if (other.packs != null)
                return false;
        } else if (!packs.equals(other.packs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        StringUtils.append(buf, packs.entrySet());
        return buf.append(']').toString();
    }

}