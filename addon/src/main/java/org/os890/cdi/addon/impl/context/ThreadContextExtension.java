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

package org.os890.cdi.addon.impl.context;

import org.os890.cdi.addon.api.scope.thread.ThreadScoped;
import org.os890.cdi.addon.impl.control.auto.AutoContextControlInterceptor;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;

/**
 * CDI portable extension that registers the {@link ThreadContext} and applies
 * the {@link AutoContextControlInterceptor} to all {@link ThreadScoped} beans.
 */
public class ThreadContextExtension implements Extension {

    /**
     * Adds the auto-context-control interceptor binding to every {@link ThreadScoped} annotated type.
     *
     * @param <T> the type being processed
     * @param pat the CDI process-annotated-type event
     */
    protected <T> void addAutoContextControl(@Observes @WithAnnotations(ThreadScoped.class) ProcessAnnotatedType<T> pat) {
        pat.configureAnnotatedType()
                .add(AutoContextControlInterceptor.LITERAL);
    }

    /**
     * Registers the {@link ThreadContext} as a custom CDI context.
     *
     * @param afterBeanDiscovery the CDI after-bean-discovery event
     * @param beanManager        the CDI bean manager
     */
    protected void registerContexts(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
        ThreadContext threadContext = new ThreadContext(beanManager);
        afterBeanDiscovery.addContext(threadContext);
    }
}
