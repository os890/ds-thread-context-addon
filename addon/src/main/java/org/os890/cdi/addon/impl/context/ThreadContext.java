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

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.os890.cdi.addon.api.scope.ResetAware;
import org.os890.cdi.addon.api.scope.thread.ThreadScoped;
import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;
import org.os890.cdi.addon.impl.control.auto.DefaultAutoContextControlInterceptor;

import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

/**
 * CDI context implementation for the {@link ThreadScoped} scope.
 * Stores beans in a {@link BeanHolder} backed by {@link ThreadLocal} and
 * implements both {@link ResetAware} and {@link ManualThreadContextManager}.
 */
public class ThreadContext extends AbstractContext implements ResetAware, ManualThreadContextManager {
    //use:
    //private static ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> FALSE);
    //+ ACTIVE.get() in #isActive to force explicit de-/activation (e.g. via an entry-point)
    //however, it wouldn't allow that the entry-point itself is @ThreadScoped

    private BeanHolder beanHolder = new BeanHolder();

    private BeanManager beanManager;

    /**
     * Creates a new {@code ThreadContext} backed by the given bean manager.
     *
     * @param beanManager the CDI bean manager
     */
    protected ThreadContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        return beanHolder.getContextualStorage(this.beanManager, createIfNotExist, isPassivatingScope());
    }

    /** {@inheritDoc} */
    @Override
    public Class<? extends Annotation> getScope() {
        return ThreadScoped.class;
    }

    /**
     * Returns {@code true} unconditionally because this context is always active.
     *
     * @return {@code true}
     */
    @Override
    public boolean isActive() {
        return true; //for a more restrictive/explicit handling see the comment at the beginning...
    }

    /**
     * Destroys all beans held in the current thread's contextual storage,
     * invoking their {@code @PreDestroy} callbacks.
     */
    @Override
    public void reset() {
        beanHolder.destroyBeans();
    }

    /*
     * for optional manual control
     */

    /**
     * Simulates entering an entry-point by incrementing the nesting counter.
     * While the counter is above zero, the context will not be reset on method exit.
     */
    @Override
    public void enter() {
        DefaultAutoContextControlInterceptor.onManuelEnter();
    }

    /**
     * Simulates leaving an entry-point by decrementing the nesting counter.
     * If the outermost level is reached (counter hits zero), the context is reset.
     */
    @Override
    public void leave() {
        if (DefaultAutoContextControlInterceptor.onManuelLeave()) {
            reset();
        }
    }

    /**
     * Force-stops the context by resetting the nesting counter to zero and
     * destroying all thread-scoped beans.
     */
    @Override
    public void stop() {
        DefaultAutoContextControlInterceptor.onManuelStop();
        reset();
    }
}
