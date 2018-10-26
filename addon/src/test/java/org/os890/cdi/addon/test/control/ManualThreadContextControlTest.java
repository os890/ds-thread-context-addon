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
package org.os890.cdi.addon.test.control;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.cdi.addon.api.scope.thread.ThreadScoped;
import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;
import org.os890.cdi.addon.test.EntryPoint;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(CdiTestRunner.class)
public class ManualThreadContextControlTest {
    @Inject
    private TestBean testBean;

    @Inject
    private ManualThreadContextManager manualThreadContextManager;

    @Inject
    private EntryPoint entryPoint;

    @Inject
    private BeanManager beanManager;

    @Test
    public void accessViaEntryPoint() {
        // nested access of a @ThreadScoped bean within a @ThreadContextStarter bean
        entryPoint.run(() -> {
            int value = testBean.getIdentityHashCode();
            assertThat(testBean.getIdentityHashCode(), is(value));
            return value;
        });
    }

    @Test
    public void manualControl() {
        // manual control without @ThreadContextStarter
        manualThreadContextManager.start();
        assertThat(testBean.getIdentityHashCode(), is(testBean.getIdentityHashCode()));
        manualThreadContextManager.stop();

        // simulation of multiple listeners each calling #start
        // if there isn't the same amount of #stop calls (because they are just onBefore listeners),
        // a #stop is needed in an "exit" listener (that allows different amount of before/after listeners)
        manualThreadContextManager.start();
        manualThreadContextManager.start();
        assertThat(testBean.getIdentityHashCode(), is(testBean.getIdentityHashCode()));
        manualThreadContextManager.stop();
    }

    @Test(expected = ContextNotActiveException.class)
    public void missingManualControl() {
        testBean.getIdentityHashCode();
    }

    @Test(expected = ContextNotActiveException.class)
    public void stoppedContext() {
        try {
            manualThreadContextManager.start();
            Context context = beanManager.getContext(ThreadScoped.class);

            assertThat(testBean.getIdentityHashCode(), is(testBean.getIdentityHashCode()));
            manualThreadContextManager.stop();
            assertFalse(context.isActive());
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertFalse(beanManager.getContext(ThreadScoped.class).isActive());
    }
}
