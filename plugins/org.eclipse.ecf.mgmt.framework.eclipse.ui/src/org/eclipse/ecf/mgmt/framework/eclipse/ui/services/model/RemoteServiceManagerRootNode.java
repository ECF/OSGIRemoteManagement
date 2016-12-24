/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesRootNode;

public class RemoteServiceManagerRootNode extends ServicesRootNode {

	private Map<IRemoteServiceReference, RemoteServiceManagerNode> managers = Collections
			.synchronizedMap(new HashMap<IRemoteServiceReference, RemoteServiceManagerNode>());

	public RemoteServiceManagerRootNode(String groupName) {
		super(groupName);
	}

	public RemoteServiceManagerNode getServiceManagerNode(IRemoteServiceReference rsRef,
			IServiceManagerAsync rsaManager) {
		synchronized (managers) {
			RemoteServiceManagerNode managerNode = managers.get(rsRef);
			if (managerNode == null) {
				managerNode = new RemoteServiceManagerNode(rsRef, rsaManager);
				managers.put(rsRef, managerNode);
				addChild(managerNode);
			}
			return managerNode;
		}
	}

	public RemoteServiceManagerNode getServiceManagerNode(IRemoteServiceID rsID) {
		synchronized (managers) {
			for(IRemoteServiceReference rsRef: managers.keySet()) {
				if (rsID.equals(rsRef.getID()))
					return managers.get(rsRef);
			}
			return null;
		}
	}
	

	public void removeServiceManagerNode(IRemoteServiceReference rsRef) {
		synchronized (managers) {
			RemoteServiceManagerNode managerNode = managers.remove(rsRef);
			if (managerNode != null)
				removeChild(managerNode);
		}
	}

}
