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

package org.jboss.renov8.installer;

import java.util.Collections;
import java.util.List;

import org.jboss.renov8.Renov8Exception;
import org.jboss.renov8.resolved.ResolvedInstall;

/**
 *
 * @author Alexey Loubyansky
 */
public class InstallContext {

    public static InstallContext newInstance(ResolvedInstall spec, List<InstallSpecHandler> handlers) {
        return new InstallContext(spec, handlers);
    }

    private ResolvedInstall spec;
    private List<InstallSpecHandler> handlers = Collections.emptyList();

    private InstallContext(ResolvedInstall spec, List<InstallSpecHandler> handlers) {
        this.spec = spec;
        this.handlers = handlers;
    }

    public ResolvedInstall getSpec() {
        return spec;
    }

    public void install() throws Renov8Exception {
        if(handlers.isEmpty()) {
            throw new Renov8Exception("Handlers have not been initialized");
        }
        for(InstallSpecHandler handler : handlers) {
            handler.handleSpec(this);
        }
    }
}
