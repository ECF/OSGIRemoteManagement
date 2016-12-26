/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;

public class RemoteServiceManagerNode extends AbstractServicesNode {

	private final IRemoteServiceReference managerRef;
	private final IServiceManagerAsync rsaManager;

	public RemoteServiceManagerNode(IRemoteServiceReference managerRef, IServiceManagerAsync rsaManager) {
		this.managerRef = managerRef;
		this.rsaManager = rsaManager;
	}

	public IServiceManagerAsync getRemoteServiceManager() {
		return this.rsaManager;
	}

	public IRemoteServiceReference getRemoteServiceReference() {
		return this.managerRef;
	}

	public String getManagerContainer() {
		return this.managerRef.getID().getContainerID().getName();
	}

	public String getName() {
		return getManagerContainer() + ":" + this.managerRef.getID().getContainerRelativeID();
	}

	public ServiceNode getServiceNode(long id) {
		for(AbstractServicesNode sn: getChildren()) {
			if (sn instanceof ServiceNode) {
				ServiceNode serviceNode = (ServiceNode) sn;
				if (serviceNode.getServiceId() == id)
					return serviceNode;
			}
		}
		return null;
	}
}
