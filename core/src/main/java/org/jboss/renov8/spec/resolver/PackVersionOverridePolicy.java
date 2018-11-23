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
import org.jboss.renov8.pack.PackId;
import org.jboss.renov8.pack.PackVersion;

/**
 *
 * @author Alexey Loubyansky
 */
public interface PackVersionOverridePolicy {

    PackVersionOverridePolicy FIRST_RESOLVED = new PackVersionOverridePolicy() {
        @Override
        public boolean override(PackId resolvedId, PackVersion newVersion) {
            return false;
        }
    };

    PackVersionOverridePolicy LAST_RESOLVED = new PackVersionOverridePolicy() {
        @Override
        public boolean override(PackId resolvedId, PackVersion newVersion) {
            return true;
        }
    };

    PackVersionOverridePolicy HIGHER_VERSION = new PackVersionOverridePolicy() {
        @Override
        public boolean override(PackId resolvedId, PackVersion newVersion) {
            return newVersion.compareTo(resolvedId.getVersion()) > 0;
        }
    };

    PackVersionOverridePolicy ERROR = new PackVersionOverridePolicy() {
        @Override
        public boolean override(PackId resolvedId, PackVersion newVersion) throws Renov8Exception {
            throw new Renov8Exception(resolvedId.getProducer() + " version conflict: " + resolvedId.getVersion() + " vs " + newVersion);
        }
    };

    boolean override(PackId resolvedId, PackVersion newVersion) throws Renov8Exception;
}