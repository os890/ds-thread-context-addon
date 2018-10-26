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

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;
import org.os890.cdi.addon.test.EntryPoint;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class SimpleThreadContextTest {
    @Inject
    private TestBean testBean;

    @Inject
    private EntryPoint entryPoint;

    @Inject
    private ManualThreadContextManager threadContextManager;

    @Test
    public void proxyOfInjectionPoint() {
        threadContextManager.start();
        assertThat(System.identityHashCode(testBean), is(not(testBean.getIdentityHashCode())));
        threadContextManager.stop();
    }

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

    @Test
    public void contextReset() {
        entryPoint.run(() -> {
            int value = testBean.getIdentityHashCode();
            assertThat(testBean.getIdentityHashCode(), is(value));

            threadContextManager.stop();
            threadContextManager.start();

            assertThat(testBean.getIdentityHashCode(), is(not(value)));
        });
    }
}
