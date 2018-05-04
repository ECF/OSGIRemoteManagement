/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceNotifier;
import org.eclipse.ecf.mgmt.framework.IServiceEventHandler;
import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.CallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ImportCallbackAssociation;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ImportCallbackAssociator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate=true)
public class RemoteServiceManagerComponent {

	private static RemoteServiceManagerComponent instance;

	public static RemoteServiceManagerComponent getInstance() {
		return instance;
	}

	public RemoteServiceManagerComponent() {
		instance = this;
	}
	
	private ImportCallbackAssociator importer;

	@Reference
	void bindCallbackAssociator(ImportCallbackAssociator ca) {
		this.importer = ca;
	}
	void unbindCallbackAssociator(ImportCallbackAssociator ca) {
		this.importer = null;
	}
	
	private IRemoteServiceNotifier notifier;
	
	@Reference
	void bindNotifier(IRemoteServiceNotifier n) {
		this.notifier = n;
	}
	
	void unbindNotifier(IRemoteServiceNotifier n) {
		this.notifier = null;
	}
	
	public IRemoteServiceNotifier getNotifier() {
		return this.notifier;
	}
	
	private ImportCallbackAssociation ica;
	
    @Activate
    public void activate(BundleContext context) throws Exception {
 		ica = this.importer.associateCallbackRegistrar(IServiceManagerAsync.class, new CallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(IServiceEventHandler.class, new ServiceEventHandler(importReference), null);
			}});
    }
    
    @Deactivate
	public void deactivate() {
    	if (ica != null) {
    		ica.disassociate();
    		ica = null;
    	}
		this.importer = null;
		this.notifier = null;
		instance = null;
    }
    
	@Reference(policy=ReferencePolicy.DYNAMIC,cardinality=ReferenceCardinality.MULTIPLE)
	void bindServicesManagerAsync(IServiceManagerAsync sm) {
		this.notifier.addServiceHolder(IServiceManagerAsync.class, sm);
	}

	void unbindServicesManagerAsync(IServiceManagerAsync sm) {
		this.notifier.removeServiceHolder(IServiceManagerAsync.class, sm);
	}

}
