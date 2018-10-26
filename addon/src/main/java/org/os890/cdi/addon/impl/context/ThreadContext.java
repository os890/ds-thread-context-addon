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
import org.os890.cdi.addon.api.scope.thread.ThreadScoped;
import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

public class ThreadContext extends AbstractContext implements ManualThreadContextManager {
    private BeanHolder beanHolder = new BeanHolder();

    private BeanManager beanManager;

    ThreadContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        //the parameters aren't needed because there is just one storage (for the current thread) which is initialized (or not)
        return beanHolder.getContextualStorage();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ThreadScoped.class;
    }

    @Override
    public boolean isActive() {
        return beanHolder.getContextualStorage() != null;
    }

    @Override
    public void start() {
        beanHolder.init(beanManager, isPassivatingScope());
    }

    @Override
    public void stop() {
        beanHolder.destroyBeans();
    }
}
