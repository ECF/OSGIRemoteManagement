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
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ServiceImporterCallbackExporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class RemoteBundleManagerComponent extends RemoteServiceComponent implements RemoteServiceAdminListener {

	private ServiceReference<IBundleEventHandler> behRef;

	private static RemoteBundleManagerComponent instance;

	public static RemoteBundleManagerComponent getInstance() {
		return instance;
	}

	private ServiceImporterCallbackExporter importer;
	
	public RemoteBundleManagerComponent() {
		instance = this;
		importer = new ServiceImporterCallbackExporter();
	}

	void bindBundleEventHandler(ServiceReference<IBundleEventHandler> ref) {
		this.behRef = ref;
	}

	void unbindBundleEventHandler(ServiceReference<IBundleEventHandler> ref) {
		this.behRef = null;
	}

	public void activate(BundleContext context) throws Exception {
		super.activate();
		this.importer.activate(context);
		this.importer.addCallbackForService(IBundleManagerAsync.class, behRef);
	}
	
	public void deactivate() {
		this.importer.removeCallbackForService(IBundleManagerAsync.class);
		this.importer.deactivate();
		super.deactivate();
	}
	
	public IContainer getContainerForID(ID id) {
		return this.importer.getContainerConnectedToID(id);
	}
	
	void bindContainerManager(IContainerManager c) {
		this.importer.bindContainerManager(c);
	}

	void unbindContainerManager(IContainerManager c) {
		this.importer.unbindContainerManager(c);
	}

	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.bindRemoteServiceAdmin(rsa);
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.unbindRemoteServiceAdmin(rsa);
	}

	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		addServiceHolder(IBundleManagerAsync.class, bm);
	}

	void unbindBundleManagerAsync(IBundleManagerAsync bm) {
		removeServiceHolder(IBundleManagerAsync.class, bm);
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		this.importer.remoteAdminEvent(event);
	}
}
