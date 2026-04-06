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

package org.os890.cdi.addon.test.context;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.os890.cdi.addon.dynamictestbean.EnableTestBeans;
import org.os890.cdi.addon.api.scope.ResetAware;
import org.os890.cdi.addon.api.scope.thread.ThreadScoped;
import org.os890.cdi.addon.test.EntryPoint;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests verifying that the {@code @ThreadScoped} context correctly manages
 * bean identity, cleanup on entry-point exit, and explicit context reset.
 */
@EnableTestBeans
public class SimpleThreadContextTest {

    @Inject
    private TestBean testBean;

    @Inject
    private BeanManager beanManager;

    @Inject
    private EntryPoint entryPoint;

    /** Verifies that the injected reference is a CDI proxy, not the contextual instance. */
    @Test
    public void proxyOfInjectionPoint() {
        assertThat(System.identityHashCode(testBean), is(not(testBean.getIdentityHashCode())));
    }

    /** Verifies that the context is reset when the outermost entry-point exits. */
    @Test
    public void cleanupOnEntryPointExit() {
        int value1 = entryPoint.run(() -> {
            int value = testBean.getIdentityHashCode();
            assertThat(testBean.getIdentityHashCode(), is(value));
            return value;
        });

        entryPoint.run(() -> {
            int value2 = testBean.getIdentityHashCode();
            assertThat(value2, is(not(value1)));
        });
    }

    /** Verifies that an explicit {@code reset()} destroys and recreates beans. */
    @Test
    public void contextReset() {
        entryPoint.run(() -> {
            int value = testBean.getIdentityHashCode();
            assertThat(testBean.getIdentityHashCode(), is(value));

            ((ResetAware) beanManager.getContext(ThreadScoped.class)).reset();

            assertThat(testBean.getIdentityHashCode(), is(not(value)));
        });
    }
}
