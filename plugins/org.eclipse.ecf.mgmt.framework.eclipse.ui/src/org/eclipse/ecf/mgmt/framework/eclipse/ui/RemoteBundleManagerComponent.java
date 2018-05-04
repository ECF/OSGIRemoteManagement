/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceNotifier;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
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
public class RemoteBundleManagerComponent {

	private static RemoteBundleManagerComponent instance;

	public static RemoteBundleManagerComponent getInstance() {
		return instance;
	}

	public RemoteBundleManagerComponent() {
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
	public void activate(final BundleContext context) throws Exception {
		ica = this.importer.associateCallbackRegistrar(IBundleManagerAsync.class, new CallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(IBundleEventHandler.class, new BundleEventHandler(importReference), null);
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
	
	private IContainerManager containerManager;
	
	@Reference
	void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}
	
	void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
	}
	
	public IContainer[] getContainersForConnectedID(ID connectedID) {
		List<IContainer> result = new ArrayList<IContainer>();
		if (containerManager != null) {
			for(IContainer c: containerManager.getAllContainers()) {
				ID cID = c.getConnectedID();
				if (cID != null && cID.equals(connectedID))
					result.add(c);
			}
		}
		return result.toArray(new IContainer[result.size()]);
	}

	@Reference(policy=ReferencePolicy.DYNAMIC,cardinality=ReferenceCardinality.MULTIPLE)
	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		this.notifier.addServiceHolder(IBundleManagerAsync.class, bm);
	}

	void unbindBundleManagerAsync(IBundleManagerAsync bm) {
		this.notifier.removeServiceHolder(IBundleManagerAsync.class, bm);
	}

}
