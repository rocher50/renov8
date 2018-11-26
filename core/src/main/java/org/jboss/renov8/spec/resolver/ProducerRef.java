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

package org.jboss.renov8.spec.resolver;

import java.util.ArrayList;
import java.util.List;

import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.spec.PackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
class ProducerRef<P extends PackSpec> {

    static final int ORDERED           = 0b1;
    static final int VISITED           = 0b01;

    final String producer;
    final P spec;
    private List<ProducerRef<P>> deps = new ArrayList<>(0);
    private int status;

    protected ProducerRef(String producer, P spec) {
        this.producer = producer;
        this.spec = spec;
    }

    boolean isFlagOn(int flag) {
        return (status & flag) > 0;
    }

    boolean setFlag(int flag) {
        if((status & flag) > 0) {
            return false;
        }
        status ^= flag;
        return true;
    }

    void clearFlag(int flag) {
        if((status & flag) > 0) {
            status ^= flag;
        }
    }

    PackId getPackId() {
        return spec.getLocation().getPackId();
    }

    void addDepRef(ProducerRef<P> dep) {
        deps.add(dep);
    }

    boolean hasDeps() {
        return !deps.isEmpty();
    }

    List<ProducerRef<P>> getDeps() {
        return deps;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(producer);
        if(spec != null) {
            buf.append(" spec=").append(spec);
        }
        return producer;
    }
}
