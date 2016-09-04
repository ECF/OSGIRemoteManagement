/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview.model;

import org.eclipse.ui.IViewSite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

/**
 * @since 3.3
 */
public class AbstractBundlesContentProvider extends BaseWorkbenchContentProvider {

	private IViewSite viewSite;
	private final BundlesRootNode invisibleRoot;

	public AbstractBundlesContentProvider(IViewSite viewSite) {
		this.viewSite = viewSite;
		this.invisibleRoot = new BundlesRootNode(""); //$NON-NLS-1$
	}

	protected IViewSite getViewSite() {
		return this.viewSite;
	}

	protected BundlesRootNode getInvisibleRoot() {
		return this.invisibleRoot;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(viewSite)) {
			return getChildren(getInvisibleRoot());
		}
		return getChildren(parent);
	}

	public BundlesRootNode getBundlesRoot() {
		return getInvisibleRoot();
	}

}
