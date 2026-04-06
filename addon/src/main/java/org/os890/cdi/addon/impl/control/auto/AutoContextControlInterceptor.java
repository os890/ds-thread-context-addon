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

package org.os890.cdi.addon.impl.control.auto;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Interceptor binding that triggers automatic {@code @ThreadScoped} context cleanup
 * when the outermost annotated method exits.
 */
@InterceptorBinding
@Documented
@Inherited

@Retention(RUNTIME)
@Target(TYPE)
public @interface AutoContextControlInterceptor {

    /** Annotation literal for programmatic use in CDI extensions. */
    Literal LITERAL = new Literal();

    /**
     * Annotation literal implementation for {@link AutoContextControlInterceptor}.
     */
    class Literal extends AnnotationLiteral<AutoContextControlInterceptor> implements AutoContextControlInterceptor {
        private static final long serialVersionUID = 1189092542638784524L;
    }
}
