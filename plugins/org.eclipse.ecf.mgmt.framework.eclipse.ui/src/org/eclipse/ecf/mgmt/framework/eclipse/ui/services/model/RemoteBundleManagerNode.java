/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.AbstractBundlesNode;

public class RemoteBundleManagerNode extends AbstractBundlesNode {

	private final IRemoteServiceReference managerRef;
	private final IBundleManagerAsync bundleManager;

	public RemoteBundleManagerNode(IRemoteServiceReference managerRef, IBundleManagerAsync bundleManager) {
		this.managerRef = managerRef;
		this.bundleManager = bundleManager;
	}

	public IBundleManagerAsync getBundleManager() {
		return this.bundleManager;
	}

	public IRemoteServiceReference getBundleManagerRef() {
		return this.managerRef;
	}

	public String getManagerContainer() {
		return this.managerRef.getID().getContainerID().getName();
	}

	public String getName() {
		return getManagerContainer() + ":" + this.managerRef.getID().getContainerRelativeID();
	}
}
