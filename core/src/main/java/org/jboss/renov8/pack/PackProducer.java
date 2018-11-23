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
public class PackProducer {

    public static PackProducer fromString(String str) {
        if(str == null || str.isEmpty()) {
            throw new IllegalArgumentException("str must be a non-empty string");
        }
        final int i = str.indexOf('@');
        if(i < 0) {
            return new PackProducer(str);
        }
        if(i == 0 || i == str.length() - 1) {
            throw new IllegalArgumentException("The expression does not follow format COORDS@REPO_TYPE: " + str);
        }
        return new PackProducer(str.substring(i + 1), str.substring(0,  i));
    }

    private final String repoType;
    private final String name;

    public PackProducer(String name) {
        this(null, name);
    }

    public PackProducer(String repoType, String name) {
        assert name != null : "name is null";
        this.repoType = repoType;
        this.name = name;
    }

    public String getRepoType() {
        return repoType;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        PackProducer other = (PackProducer) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
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
            return name;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(name).append('@').append(repoType);
        return buf.toString();
    }
}
