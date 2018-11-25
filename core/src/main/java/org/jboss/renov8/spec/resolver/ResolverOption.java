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

import org.jboss.renov8.Renov8Exception;

/**
 *
 * @author Alexey Loubyansky
 */
public enum ResolverOption {

    DEP_ITERATOR_STRAIGHT   (ResolverOptionConstants.DEP_ITERATOR_STRAIGHT, ResolverOptionConstants.DEP_ITERATOR_MASK),
    DEP_ITERATOR_REVERSE    (ResolverOptionConstants.DEP_ITERATOR_REVERSE, ResolverOptionConstants.DEP_ITERATOR_MASK);

    private final int mask;
    private final int flag;

    ResolverOption(int flag, int mask) {
        this.flag = flag;
        this.mask = mask;
    }

    public boolean isSet(int options) {
        return (options & flag) > 0;
    }

    public int set(int options) throws Renov8Exception {
        if((options & flag) > 0) {
            return options;
        }
        if((options & mask) > 0) {
            throw new Renov8Exception(
                    "Resolver option " + this + " cannot be combined with " + ResolverOption.values()[getIndex(options & mask)]);
        }
        return options ^= flag;
    }

    public int clear(int options) {
        if ((options & flag) > 0) {
            options ^= flag;
        }
        return options;
    }

    private static int getIndex(int flag) {
        if(flag == 0) {
            throw new IllegalArgumentException("Flag is 0");
        }
        int i = 0;
        while((flag & 1) != 1) {
            flag >>= 1;
            ++i;
        }
        return i;
    }
}
