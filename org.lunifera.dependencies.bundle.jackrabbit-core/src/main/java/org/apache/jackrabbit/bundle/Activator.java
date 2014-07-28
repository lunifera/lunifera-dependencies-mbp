/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.bundle;

/*
 * #%L
 * org.lunifera.dependencies.bundle.jackrabbit-core
 * %%
 * Copyright (C) 2012 - 2014 Lunifera.org
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

import java.io.File;
import java.util.Hashtable;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private volatile RepositoryImpl repository;

    private volatile ServiceRegistration registration;

    public void start(BundleContext context) throws Exception {
        repository = RepositoryImpl.create(
                RepositoryConfig.install(new File("jackrabbit")));

        Hashtable<String, String> properties = new Hashtable<String, String>();
        for (String key : repository.getDescriptorKeys()) {
            String descriptor = repository.getDescriptor(key);
            if (descriptor != null) {
                properties.put(key, descriptor);
            }
        }
        registration = context.registerService(
                Repository.class.getName(), repository, properties);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();

        repository.shutdown();
    }

}
