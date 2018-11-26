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

package org.jboss.renov8.config;

import org.jboss.renov8.PackLocation;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackConfig {

    public static PackConfig forLocation(PackLocation pl) {
        return new PackConfig(pl);
    }

    private final PackLocation pl;

    protected PackConfig(PackLocation pl) {
        this.pl = pl;
    }

    public PackLocation getLocation() {
        return pl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pl == null) ? 0 : pl.hashCode());
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
        PackConfig other = (PackConfig) obj;
        if (pl == null) {
            if (other.pl != null)
                return false;
        } else if (!pl.equals(other.pl))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[').append(pl);
        return buf.append(']').toString();
    }
}
