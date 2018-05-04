/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceNotifier;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallEventHandler;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManagerAsync;
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

@Component(immediate = true)
public class RemoteKarafFeaturesInstaller {

	private static RemoteKarafFeaturesInstaller instance;

	public static RemoteKarafFeaturesInstaller getInstance() {
		return instance;
	}

	public RemoteKarafFeaturesInstaller() {
		instance = this;
	}

	private IContainerManager containerManager;
	
	@Reference
	void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}
	
	void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
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

	private ImportCallbackAssociation association;
	
	@Activate
	public void activate(BundleContext context) throws Exception {
		association = this.importer.associateCallbackRegistrar(FeatureInstallManagerAsync.class, new CallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(FeatureInstallEventHandler.class,
						new KarafFeaturesListener(importReference), null);
			}
		});
	}

	@Deactivate
	public void deactivate() {
		if (association != null) {
			association.disassociate();
			association = null;
		}
		this.importer = null;
		this.notifier = null;
		instance = null;
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	void bindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		this.notifier.addServiceHolder(FeatureInstallManagerAsync.class, fi);
	}

	void unbindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		this.notifier.removeServiceHolder(FeatureInstallManagerAsync.class, fi);
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

}
