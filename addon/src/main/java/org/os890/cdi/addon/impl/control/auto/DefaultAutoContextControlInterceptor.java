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

package org.os890.cdi.addon.impl.control.auto;

import org.os890.cdi.addon.api.scope.ResetAware;
import org.os890.cdi.addon.api.scope.thread.ThreadScoped;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import static jakarta.interceptor.Interceptor.Priority.LIBRARY_BEFORE;

/**
 * CDI interceptor that tracks nested {@code @ThreadScoped} entry-points via a counter
 * and resets the thread context when the outermost call exits.
 */
// CDI proxies handle serialization of injected dependencies transparently,
// so the non-transient @Inject field is safe in this Serializable interceptor.
@SuppressWarnings("serial")
@Priority(LIBRARY_BEFORE)
@AutoContextControlInterceptor
@Interceptor
public class DefaultAutoContextControlInterceptor implements Serializable {

    private static final long serialVersionUID = 1189092542638784524L;

    private static ThreadLocal<AtomicInteger> nestedCallDetection = ThreadLocal.withInitial(() -> new AtomicInteger(0));

    @Inject
    private BeanManager beanManager;

    /**
     * Intercepts method invocations on {@code @ThreadScoped} beans.
     * Increments the nesting counter on entry and resets the context when the outermost call exits.
     *
     * @param invocationContext the CDI invocation context
     * @return the result of the intercepted method
     * @throws Exception if the intercepted method throws
     */
    @AroundInvoke
    public Object execute(InvocationContext invocationContext) throws Exception {
        try {
            nestedCallDetection.get().incrementAndGet();
            return invocationContext.proceed();
        } finally {
            int level = nestedCallDetection.get().decrementAndGet();

            if (level == 0) {
                //we could also inject ManualThreadContextManager and call #close
                ((ResetAware) beanManager.getContext(ThreadScoped.class)).reset();
            }
        }
    }

    /**
     * Called by manual control to simulate entering an entry-point.
     */
    public static void onManuelEnter() {
        nestedCallDetection.get().incrementAndGet();
    }

    /**
     * Called by manual control to simulate leaving an entry-point.
     *
     * @return {@code true} if the outermost level was reached (counter hit zero)
     */
    public static boolean onManuelLeave() {
        return nestedCallDetection.get().decrementAndGet() == 0;
    }

    /**
     * Called by manual control to force-stop the context and reset the counter to zero.
     */
    public static void onManuelStop() {
        nestedCallDetection.set(new AtomicInteger(0));
    }
}
