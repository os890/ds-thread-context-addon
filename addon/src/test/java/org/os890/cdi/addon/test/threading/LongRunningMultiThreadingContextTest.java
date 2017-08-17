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
package org.os890.cdi.addon.test.threading;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.os890.cdi.addon.test.EntryPoint;

import javax.inject.Inject;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static org.apache.deltaspike.core.util.ExceptionUtils.throwAsRuntimeException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(CdiTestRunner.class)
public class LongRunningMultiThreadingContextTest {
    private ExecutorService executor = newFixedThreadPool(3);

    @Inject
    private InstanceCounterTestBean instanceCounterTestBean;

    @Inject
    private EntryPoint entryPoint;

    @Test
    public void longRunningMultiThread() {
        CopyOnWriteArrayList<Future<Integer>> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            Future<Integer> result = executor.submit(() -> entryPoint.run(() -> {
                int identityHashCode = instanceCounterTestBean.getIdentityHashCode();
                assertEquals(identityHashCode, instanceCounterTestBean.getIdentityHashCode());
                return instanceCounterTestBean.getInstanceId();
            }));
            results.add(result);
        }

        assertTrue(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .containsAll(results.stream().map(this::get).collect(toList())));

        assertEquals(11, instanceCounterTestBean.getInstanceId());
    }

    private int get(Future<Integer> entry) {
        try {
            return entry.get();
        } catch (Exception e) {
            throw throwAsRuntimeException(e);
        }
    }
}
