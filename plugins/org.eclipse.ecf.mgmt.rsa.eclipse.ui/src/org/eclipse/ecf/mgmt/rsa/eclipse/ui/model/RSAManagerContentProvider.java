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
import org.eclipse.ui.IViewSite;

/**
 * @since 3.3
 */
public class RSAManagerContentProvider extends AbstractRSAContentProvider {

	private final RemoteRSAManagersRootNode invisibleRoot;

	public RSAManagerContentProvider(IViewSite viewSite) {
		super(viewSite);
		this.invisibleRoot = new RemoteRSAManagersRootNode(""); //$NON-NLS-1$
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(getViewSite())) {
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public RemoteRSAManagersRootNode getRoot() {
		return invisibleRoot;
	}
}
