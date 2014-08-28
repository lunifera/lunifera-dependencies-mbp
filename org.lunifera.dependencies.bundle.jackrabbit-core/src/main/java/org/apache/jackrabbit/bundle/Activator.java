package org.apache.jackrabbit.bundle;

/*
 * #%L
 * Lunifera Dependencies : Apache JackRabbit
 * %%
 * Copyright (C) 2012 - 2014 C4biz Softwares ME, Loetz KG
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
