/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import java.util.Collection;
import java.util.Hashtable;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxy;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

public class RemoteBundleManagerComponent extends RemoteServiceComponent {

	private static RemoteBundleManagerComponent instance;

	public static RemoteBundleManagerComponent getInstance() {
		return instance;
	}

	public RemoteBundleManagerComponent() {
		instance = this;
	}

	private RemoteServiceAdmin localRSA;
	
	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.localRSA = rsa;
	}
	
	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.localRSA = null;
	}
	
	public RemoteServiceAdmin getRSA() {
		return this.localRSA;
	}
	
	private ServiceReference<IBundleEventHandler> behRef;
	
	void bindBundleEventHandler(ServiceReference<IBundleEventHandler> ref) {
		this.behRef = ref;
	}
	
	void unbindBundleEventHandler(ServiceReference<IBundleEventHandler> ref) {
		this.behRef = null;
	}
	
	private IContainerManager cm;
	
	void bindContainerManager(IContainerManager c) {
		this.cm = c;
	}
	
	void unbindContainerManager(IContainerManager c) {
		this.cm = null;
	}
	
	IContainer getContainerForID(ID connectedID) {
		for(IContainer c: this.cm.getAllContainers()) {
			ID targetID = c.getConnectedID();
			if (targetID != null && targetID.equals(connectedID))
				return c;
		}
		return null;
	}

	/*
	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		IRemoteServiceProxy rsProxy = (IRemoteServiceProxy) bm;
		ID cID = rsProxy.getRemoteServiceReference().getContainerID();
		// First export to local RSA
		Hashtable<String,Object> props = new Hashtable<String,Object>();
		props.put("service.exported.interfaces","*");
		props.put("ecf.exported.async.interfaces", "*");
		props.put("service.exported.configs", "ecf.generic.client");
		props.put("ecf.endpoint.connecttarget.id", cID.getName());
		IContainer c = getContainerForID(cID);
		if (c != null) 
			props.put("ecf.endpoint.idfilter.ids", new String[] { c.getID().getName() } );
		
		// Export IBundleManagerAsync service
		Collection<ExportRegistration> regs = this.localRSA.exportService(behRef, props);
		ExportRegistration reg = regs.iterator().next();
		// If exported without exception...
		if (reg.getException() == null) {
			addServiceHolder(IBundleManagerAsync.class, bm);
		}
	}
	*/
	
	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		addServiceHolder(IBundleManagerAsync.class, bm);
	}
	
	void unbindBundleManagerAsync(IBundleManagerAsync bm) {
		removeServiceHolder(IBundleManagerAsync.class, bm);
	}
}
