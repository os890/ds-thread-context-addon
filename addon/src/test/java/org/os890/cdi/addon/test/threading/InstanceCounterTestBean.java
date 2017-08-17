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

import org.os890.cdi.addon.api.scope.thread.ThreadScoped;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.identityHashCode;
import static org.apache.deltaspike.core.util.ExceptionUtils.throwAsRuntimeException;

@ThreadScoped
public class InstanceCounterTestBean {
    private static final AtomicInteger counter = new AtomicInteger(0);

    private int instanceId;

    @PostConstruct
    protected void init() {
        this.instanceId = counter.incrementAndGet();
    }

    @PreDestroy
    protected void cleanup() {
        try {
            Thread.sleep(400L);
        } catch (InterruptedException e) {
            throw throwAsRuntimeException(e);
        }
    }

    public int getInstanceId() {
        return instanceId;
    }

    public int getIdentityHashCode() {
        return identityHashCode(this);
    }
}
