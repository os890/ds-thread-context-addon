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

package org.os890.cdi.addon.test.lifecycle;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.os890.cdi.addon.dynamictestbean.EnableTestBeans;
import org.os890.cdi.addon.test.EntryPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.os890.cdi.addon.test.lifecycle.PreDestroyTestBean.State.*;

/**
 * Tests verifying that {@code @PostConstruct} and {@code @PreDestroy} lifecycle callbacks
 * are invoked correctly for {@code @ThreadScoped} beans.
 */
@EnableTestBeans
public class ThreadContextLifecycleCallbackTest {

    @Inject
    private PreDestroyTestBean preDestroyTestBean;

    @Inject
    private EntryPoint entryPoint;

    /** Verifies the full bean lifecycle: no-bean, post-construct, pre-destroy across context resets. */
    @Test
    public void beanLifecycle() {
        assertEquals(NO_BEAN_CREATED, PreDestroyTestBean.getConstructionState()); //ATTENTION: this line fails IF you debug it and put a break-point here because IDEs call #toString which triggers the bean-creation
        assertEquals(42, preDestroyTestBean.getValue());
        assertEquals(BEAN_DESTROYED, PreDestroyTestBean.getConstructionState());

        entryPoint.run(() -> {
            assertEquals(42, preDestroyTestBean.getValue());
            assertEquals(BEAN_CREATED, PreDestroyTestBean.getConstructionState());
        });
        assertEquals(BEAN_DESTROYED, PreDestroyTestBean.getConstructionState());
    }
}
