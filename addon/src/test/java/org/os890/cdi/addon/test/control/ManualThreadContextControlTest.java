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
import org.os890.cdi.addon.api.scope.thread.control.ManualThreadContextManager;
import org.os890.cdi.addon.test.EntryPoint;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(CdiTestRunner.class)
public class ManualThreadContextControlTest {
    @Inject
    private TestBean testBean;

    @Inject
    private ManualThreadContextManager manualThreadContextManager;

    @Inject
    private EntryPoint entryPoint;

    @Test
    public void autoVsManualControl() {
        // #1
        // accessing a @ThreadScoped bean multiple times >without manual control< just leads to the same instance if
        // there is a different @ThreadScoped in the callstack above
        // in this case the entryPoint bean is the outermost @ThreadScoped bean which closes the context at the end
        entryPoint.run(() -> {
            int value = testBean.getIdentityHashCode();
            assertThat(testBean.getIdentityHashCode(), is(value));
            return value;
        });

        // #2
        // -> no entry-point above -> each call leads to a new instance (because the outermost @ThreadScoped bean closes the context
        // in this case both calls are on the "same level"
        assertThat(testBean.getIdentityHashCode(), is(not(testBean.getIdentityHashCode())));

        // #3
        // calling #enter simulates an outermost entry-point
        // any call to a @ThreadScoped will use the same context
        manualThreadContextManager.enter();
        assertThat(testBean.getIdentityHashCode(), is(testBean.getIdentityHashCode()));
        manualThreadContextManager.leave();

        // #4
        // the previous #leave is an implicit #stop because "closes" the manuel #enter
        // therefore the following calls are again on the "same level" (see #2)
        assertThat(testBean.getIdentityHashCode(), is(not(testBean.getIdentityHashCode())));

        // #5
        // this cases simulates multiple listeners each calling #enter
        // if there isn't the same amount of #leave calls (because they are just onBefore listeners),
        // a #stop is needed in an "exit" listener (that allows different amount of before/after listeners)
        manualThreadContextManager.enter();
        manualThreadContextManager.enter();
        assertThat(testBean.getIdentityHashCode(), is(testBean.getIdentityHashCode()));
        manualThreadContextManager.stop();

        // #6
        // the previous #stop closed the context
        // therefore the following calls are again on the "same level" (see #2)
        assertThat(testBean.getIdentityHashCode(), is(not(testBean.getIdentityHashCode())));
    }
}
