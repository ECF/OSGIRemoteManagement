/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceNotifier;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ICallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.IImportableServiceCallbackAssociator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate=true)
public class RemoteBundleManagerComponent {

	private static RemoteBundleManagerComponent instance;

	public static RemoteBundleManagerComponent getInstance() {
		return instance;
	}

	public RemoteBundleManagerComponent() {
		instance = this;
	}
	
	private IImportableServiceCallbackAssociator importer;

	@Reference
	void bindCallbackAssociator(IImportableServiceCallbackAssociator ca) {
		this.importer = ca;
	}
	void unbindCallbackAssociator(IImportableServiceCallbackAssociator ca) {
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
	
	@Activate
	public void activate(final BundleContext context) throws Exception {
		this.importer.associateCallbackRegistrar(IBundleManagerAsync.class, new ICallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(IBundleEventHandler.class, new BundleEventHandler(importReference), null);
			}});
	}
	
	@Deactivate
	public void deactivate() {
		this.importer.unassociateCallbackRegistrar(IBundleManagerAsync.class);
		this.importer = null;
		this.notifier = null;
		instance = null;
	}
	
	public IContainer getContainerForID(ID id) {
		return importer == null?null:importer.getContainerConnectedToID(id);
	}
	
	@Reference(policy=ReferencePolicy.DYNAMIC,cardinality=ReferenceCardinality.MULTIPLE)
	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		this.notifier.addServiceHolder(IBundleManagerAsync.class, bm);
	}

	void unbindBundleManagerAsync(IBundleManagerAsync bm) {
		this.notifier.removeServiceHolder(IBundleManagerAsync.class, bm);
	}

}
