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

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.spec.PackSpec;

/**
 *
 * @author Alexey Loubyansky
 */
class ProducerRef {

    static final int DEAD_REF_BRANCH   = 0b0001;
    static final int ORDERED           = 0b0010;
    static final int RERESOLVE_BRANCH  = 0b0100;
    static final int VISITED           = 0b1000;

    final String producer;
    private int count;
    private PackSpec spec;
    private List<ProducerRef> deps = new ArrayList<>(0);
    private int status;

    protected ProducerRef(String producer) {
        this.producer = producer;
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

    void setSpec(PackSpec spec) {
        this.spec = spec;
    }

    PackSpec getSpec() {
        return spec;
    }

    PackId getPackId() {
        return spec == null ? null : spec.getId();
    }

    void addDep(ProducerRef dep) {
        deps.add(dep);
    }

    boolean hasDeps() {
        return !deps.isEmpty();
    }

    List<ProducerRef> getDeps() {
        return deps;
    }

    void increase() {
        ++count;
    }

    void decrease() throws Renov8Exception {
        if(count == 0) {
            throw new IllegalStateException("Negative reference counter for producer " + producer);
        }
        if(--count == 0) {
            reset();
        }
    }

    void reset() throws Renov8Exception {
        count = 0;
        if (!deps.isEmpty()) {
            final boolean clearFlag = setFlag(DEAD_REF_BRANCH);
            for(ProducerRef dep : deps) {
                if(!dep.setFlag(DEAD_REF_BRANCH)) {
                    continue;
                }
                dep.decrease();
                dep.clearFlag(DEAD_REF_BRANCH);
            }
            if(clearFlag) {
                clearFlag(DEAD_REF_BRANCH);
            }
            deps.clear();
        }
        spec = null;
    }

    boolean hasVersion() {
        return spec != null;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        buf.append(producer);
        buf.append(" refs=").append(count);
        if(spec != null) {
            buf.append(" spec=").append(spec);
        }
        return producer;
    }
}
