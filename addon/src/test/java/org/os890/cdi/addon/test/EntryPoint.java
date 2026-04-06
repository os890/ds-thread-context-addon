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

package org.os890.cdi.addon.test;

import org.os890.cdi.addon.api.scope.thread.ThreadScoped;

import java.util.concurrent.Callable;

import static org.apache.deltaspike.core.util.ExceptionUtils.throwAsRuntimeException;

/**
 * A {@code @ThreadScoped} entry-point bean used in tests to establish an outer
 * thread-scoped boundary for interceptor-managed context lifecycle.
 */
@ThreadScoped
public class EntryPoint {

    /**
     * Executes the given {@link Runnable} inside the thread-scoped context.
     *
     * @param runnable the task to run
     */
    public void run(Runnable runnable) {
        runnable.run();
    }

    /**
     * Executes the given {@link Callable} inside the thread-scoped context and returns the result.
     *
     * @param <T> the result type
     * @param callable the task to call
     * @return the result of the callable
     */
    public <T> T run(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw throwAsRuntimeException(e);
        }
    }
}
