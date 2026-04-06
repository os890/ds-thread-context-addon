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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.logging.Logger;

import static org.os890.cdi.addon.test.lifecycle.PreDestroyTestBean.State.*;

/**
 * A {@code @ThreadScoped} bean that tracks its lifecycle state via a {@link ThreadLocal},
 * used to verify that {@code @PostConstruct} and {@code @PreDestroy} callbacks fire correctly.
 */
@ThreadScoped
public class PreDestroyTestBean {

    private static ThreadLocal<State> beanDestroyed = ThreadLocal.withInitial(() -> NO_BEAN_CREATED);

    /**
     * Returns a fixed sentinel value used to trigger bean creation through the proxy.
     *
     * @return the value {@code 42}
     */
    public int getValue() {
        return 42;
    }

    /** Marks the bean as created when CDI invokes the post-construct callback. */
    @PostConstruct
    protected void init() {
        beanDestroyed.set(BEAN_CREATED);
    }

    /** Marks the bean as destroyed when CDI invokes the pre-destroy callback. */
    @PreDestroy
    protected void cleanup() {
        beanDestroyed.set(BEAN_DESTROYED);
    }

    /**
     * Returns the current lifecycle state of the bean on the calling thread.
     *
     * @return the current {@link State}
     */
    public static State getConstructionState() {
        return beanDestroyed.get();
    }

    /** Tracks the lifecycle state of a {@link PreDestroyTestBean} instance. */
    public enum State {
        /** No bean instance has been created on this thread yet. */
        NO_BEAN_CREATED,
        /** A bean instance has been created (post-construct fired). */
        BEAN_CREATED,
        /** The bean instance has been destroyed (pre-destroy fired). */
        BEAN_DESTROYED
    }

    @Override
    public String toString() {
        Logger.getLogger(getClass().getName()).severe("\n***\nif you can see this message you are debugging and your IDE triggered it. " +
                "that can influence the junit-test which asserts the construction-state.\n***");
        return super.toString();
    }
}
