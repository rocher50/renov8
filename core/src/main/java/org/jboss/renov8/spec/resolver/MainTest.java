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

/**
 * @author olubyans
 *
 */
public class MainTest {

    public static void main(String[] args) throws Exception {

        int options = 0;
        options = ResolverOption.DEP_ITERATOR_REVERSE.set(options);
        options = ResolverOption.DEP_ITERATOR_STRAIGHT.set(options);
    }


    public static int getIndex(int flag) {
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
