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
package org.os890.cdi.addon.api.scope.thread.control;

/**
 * use enter/leave manually if you have 1-n listeners which are called outside of the callstack of the "main operation"
 * e.g.:
 * #1 call 0-n onBefore listeners
 * #2 call (main-) actions
 * #3 call onAfter on the same listeners or onStop on something like a cleanup/finish listener
 * <p>
 * you mainly need it if there isn't an entry-point which triggers all actions as "nested" calls
 * see e.g. ManualThreadContextControlTest
 */
public interface ManualThreadContextManager {
    void enter();

    void leave();

    void stop();
}
