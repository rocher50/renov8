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

package org.jboss.renov8.pack;

/**
 *
 * @author Alexey Loubyansky
 */
public class PackLocation {

    public static PackLocation fromString(String str) {
        if(str == null || str.isEmpty()) {
            throw new IllegalArgumentException("str must be a non-empty string");
        }
        final int i = str.indexOf('@');
        if(i < 0) {
            return new PackLocation(str);
        }
        if(i == 0 || i == str.length() - 1) {
            throw new IllegalArgumentException("The expression does not follow format COORDS@REPO_TYPE: " + str);
        }
        return new PackLocation(str.substring(i + 1), str.substring(0,  i));
    }

    private final String repoType;
    private final String coords;

    public PackLocation(String coords) {
        this(null, coords);
    }

    public PackLocation(String repoType, String coords) {
        assert coords != null : "coords is null";
        this.repoType = repoType;
        this.coords = coords;
    }

    public String getRepoType() {
        return repoType;
    }

    public String getCoords() {
        return coords;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coords == null) ? 0 : coords.hashCode());
        result = prime * result + ((repoType == null) ? 0 : repoType.hashCode());
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
        PackLocation other = (PackLocation) obj;
        if (coords == null) {
            if (other.coords != null)
                return false;
        } else if (!coords.equals(other.coords))
            return false;
        if (repoType == null) {
            if (other.repoType != null)
                return false;
        } else if (!repoType.equals(other.repoType))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if(repoType == null) {
            return coords;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(coords).append('@').append(repoType);
        return buf.toString();
    }
}
