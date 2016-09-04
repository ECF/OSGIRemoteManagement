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

import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesRootNode;

public class RemoteBundleManagerRootNode extends BundlesRootNode {

	private Map<IRemoteServiceReference, RemoteBundleManagerNode> managers = Collections
			.synchronizedMap(new HashMap<IRemoteServiceReference, RemoteBundleManagerNode>());

	public RemoteBundleManagerRootNode(String groupName) {
		super(groupName);
	}

	public RemoteBundleManagerNode getBundleManagerNode(IRemoteServiceReference rsRef, IBundleManagerAsync rsaManager) {
		synchronized (managers) {
			RemoteBundleManagerNode managerNode = managers.get(rsRef);
			if (managerNode == null) {
				managerNode = new RemoteBundleManagerNode(rsRef, rsaManager);
				managers.put(rsRef, managerNode);
				addChild(managerNode);
			}
			return managerNode;
		}
	}

	public void removeBundleManagerNode(IRemoteServiceReference rsRef) {
		synchronized (managers) {
			RemoteBundleManagerNode managerNode = managers.remove(rsRef);
			if (managerNode != null)
				removeChild(managerNode);
		}
	}

}
