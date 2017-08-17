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

import org.os890.cdi.addon.api.scope.thread.ThreadScoped;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.Logger;

import static org.os890.cdi.addon.test.lifecycle.PreDestroyTestBean.State.*;

@ThreadScoped
public class PreDestroyTestBean {
    private static ThreadLocal<State> beanDestroyed = ThreadLocal.withInitial(() -> NO_BEAN_CREATED);

    public int getValue() {
        return 42;
    }

    @PostConstruct
    protected void init() {
        beanDestroyed.set(BEAN_CREATED);
    }

    @PreDestroy
    protected void cleanup() {
        beanDestroyed.set(BEAN_DESTROYED);
    }

    public static State getConstructionState() {
        return beanDestroyed.get();
    }

    public enum State {
        NO_BEAN_CREATED, BEAN_CREATED, BEAN_DESTROYED
    }

    @Override
    public String toString() {
        Logger.getLogger(getClass().getName()).severe("\n***\nif you can see this message you are debugging and your IDE triggered it. " +
                "that can influence the junit-test which asserts the construction-state.\n***");
        return super.toString();
    }
}
