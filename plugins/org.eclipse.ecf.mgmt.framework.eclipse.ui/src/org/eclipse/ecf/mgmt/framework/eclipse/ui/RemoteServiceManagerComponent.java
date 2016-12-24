/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.framework.IServiceEventHandler;
import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ICallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ServiceImporterCallbackExporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class RemoteServiceManagerComponent extends RemoteServiceComponent implements RemoteServiceAdminListener {

	private static RemoteServiceManagerComponent instance;

	public static RemoteServiceManagerComponent getInstance() {
		return instance;
	}

	private ServiceImporterCallbackExporter importer;
	
	public RemoteServiceManagerComponent() {
		instance = this;
		importer = new ServiceImporterCallbackExporter();
	}

    @Activate
    public void activate(BundleContext context) throws Exception {
    	super.activate();
		this.importer.activate(context);
		this.importer.addImportedServiceCallback(IServiceManagerAsync.class, new ICallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(IServiceEventHandler.class, new ServiceEventHandler(importReference), null);
			}});
    }
    
    @Deactivate
	public void deactivate() {
		this.importer.removeImportedServiceCallback(IServiceManagerAsync.class);
		this.importer.deactivate();
		super.deactivate();

    }
    
    @Reference
	void bindContainerManager(IContainerManager c) {
		this.importer.bindContainerManager(c);
	}

	void unbindContainerManager(IContainerManager c) {
		this.importer.unbindContainerManager(c);
	}

	@Reference
	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.bindRemoteServiceAdmin(rsa);
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.unbindRemoteServiceAdmin(rsa);
	}

	@Reference(policy=ReferencePolicy.DYNAMIC,cardinality=ReferenceCardinality.MULTIPLE)
	void bindServicesManagerAsync(IServiceManagerAsync sm) {
		addServiceHolder(IServiceManagerAsync.class, sm);
	}

	void unbindServicesManagerAsync(IServiceManagerAsync sm) {
		removeServiceHolder(IServiceManagerAsync.class, sm);
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		importer.remoteAdminEvent(event);
	}
}
