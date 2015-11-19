/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSAContentProvider;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportedServicesRootNode;
import org.eclipse.ui.IViewSite;

/**
 * @since 3.3
 */
public class RSAManagerContentProvider extends AbstractRSAContentProvider {

	private RemoteRSAManagersRootNode root;

	public RSAManagerContentProvider(IViewSite viewSite) {
		super(viewSite);
		ExportedServicesRootNode invisibleRoot = getInvisibleRoot();
		this.root = new RemoteRSAManagersRootNode("Remote RSA Managers");
		invisibleRoot.addChild(root);
	}

	public RemoteRSAManagersRootNode getRoot() {
		return root;
	}
}
