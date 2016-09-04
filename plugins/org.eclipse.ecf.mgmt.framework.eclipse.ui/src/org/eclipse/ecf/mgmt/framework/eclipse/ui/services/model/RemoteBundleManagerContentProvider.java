/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesContentProvider;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesRootNode;
import org.eclipse.ui.IViewSite;

public class RemoteBundleManagerContentProvider extends BundlesContentProvider {

	private RemoteBundleManagerRootNode root;

	public RemoteBundleManagerContentProvider(IViewSite viewSite) {
		super(viewSite);
		this.root = new RemoteBundleManagerRootNode("");
	}

	@Override
	protected BundlesRootNode getInvisibleRoot() {
		return getBundlesRoot();
	}

	public RemoteBundleManagerRootNode getBundlesRoot() {
		return root;
	}

}
