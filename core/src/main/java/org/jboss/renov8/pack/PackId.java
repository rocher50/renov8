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
public class PackId {

    private final String producer;
    private final PackVersion version;

    public PackId(String producer, PackVersion version) {
        this.producer = producer;
        this.version = version;
    }

    public String getProducer() {
        return producer;
    }

    public PackVersion getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((producer == null) ? 0 : producer.hashCode());
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
        PackId other = (PackId) obj;
        if (producer == null) {
            if (other.producer != null)
                return false;
        } else if (!producer.equals(other.producer)) {
            return false;
        }
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return producer + ':' + version;
    }
}
