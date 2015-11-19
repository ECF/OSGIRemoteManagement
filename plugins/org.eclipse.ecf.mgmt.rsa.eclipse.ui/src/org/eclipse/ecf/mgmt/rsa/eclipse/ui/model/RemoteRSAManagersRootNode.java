/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportedServicesRootNode;

public class RemoteRSAManagersRootNode extends ExportedServicesRootNode {

	private Map<IRemoteServiceReference, RSAManagerNode> managers = Collections
			.synchronizedMap(new HashMap<IRemoteServiceReference, RSAManagerNode>());

	public RemoteRSAManagersRootNode(String groupName) {
		super(groupName);
	}

	public RSAManagerNode getRSAManagerNode(IRemoteServiceReference rsRef, IRemoteServiceAdminManagerAsync rsaManager) {
		synchronized (managers) {
			RSAManagerNode managerNode = managers.get(rsRef);
			if (managerNode == null) {
				managerNode = new RSAManagerNode(rsRef, rsaManager);
				managers.put(rsRef, managerNode);
				addChild(managerNode);
			}
			return managerNode;
		}
	}

	public void removeRSAManagerNode(IRemoteServiceReference rsRef) {
		synchronized (managers) {
			RSAManagerNode managerNode = managers.remove(rsRef);
			if (managerNode != null)
				removeChild(managerNode);
		}
	}

}
