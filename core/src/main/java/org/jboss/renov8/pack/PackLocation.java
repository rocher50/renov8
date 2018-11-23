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

    public static PackLocation create(String producer, PackVersion version) {
        return new PackLocation(new PackId(producer, version));
    }

    private final String repoType;
    private final PackId packId;

    public PackLocation(PackId packId) {
        this(null, packId);
    }

    public PackLocation(String repoType, PackId packId) {
        assert packId != null : "packId is null";
        this.repoType = repoType;
        this.packId = packId;
    }

    public String getRepoType() {
        return repoType;
    }

    public PackId getPackId() {
        return packId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packId == null) ? 0 : packId.hashCode());
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
        if (packId == null) {
            if (other.packId != null)
                return false;
        } else if (!packId.equals(other.packId))
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
            return packId.toString();
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(packId).append('@').append(repoType);
        return buf.toString();
    }
}
