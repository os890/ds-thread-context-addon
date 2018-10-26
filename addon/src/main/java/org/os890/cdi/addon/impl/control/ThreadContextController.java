/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.os890.cdi.addon.impl.control;

import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;
import org.os890.cdi.addon.api.scope.thread.control.ThreadContextStarter;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

import static javax.interceptor.Interceptor.Priority.LIBRARY_BEFORE;

@Priority(LIBRARY_BEFORE)
@ThreadContextStarter
@Interceptor
public class ThreadContextController implements Serializable {
    private static final long serialVersionUID = 1189092542638784524L;

    @Inject
    private ManualThreadContextManager manualThreadContextManager;

    @AroundInvoke
    public Object execute(InvocationContext invocationContext) throws Exception {
        try {
            manualThreadContextManager.start();
            return invocationContext.proceed();
        } finally {
            manualThreadContextManager.stop();
        }
    }
}
