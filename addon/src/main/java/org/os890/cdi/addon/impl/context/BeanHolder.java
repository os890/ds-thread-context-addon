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

import javax.enterprise.inject.spi.BeanManager;

import static java.util.Optional.ofNullable;

public class BeanHolder {
    private static ThreadLocal<ContextualStorage> storageThreadLocal = new ThreadLocal<>();

    public ContextualStorage getContextualStorage() {
        return storageThreadLocal.get();
    }

    public void init(BeanManager beanManager, boolean passivationCapable) {
        ContextualStorage contextualStorage = new ContextualStorage(beanManager, false /*since it's stored in a ThreadLocal*/, passivationCapable);
        storageThreadLocal.set(contextualStorage);
    }

    public void destroyBeans() {
        try {
            ofNullable(storageThreadLocal.get()).ifPresent(AbstractContext::destroyAllActive);
        } finally {
            storageThreadLocal.set(null);
            storageThreadLocal.remove();
        }
    }
}
