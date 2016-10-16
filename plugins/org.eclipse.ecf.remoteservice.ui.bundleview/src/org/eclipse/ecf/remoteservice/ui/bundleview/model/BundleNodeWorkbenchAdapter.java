/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview.model;

import org.eclipse.ecf.remoteservice.ui.internal.bundleview.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

/**
 * @since 3.3
 */
public class BundleNodeWorkbenchAdapter extends AbstractBundlesWorkbenchAdapter {

	private static final String ICON_ACTIVE = "/icons/bundle_active.png";
	private static final String ICON_RESOLVED = "/icons/bundle_resolved.png";
	private static final String ICON_INSTALLED = "/icons/bundle_installed.png";
	
	@Override
	public String getLabel(Object object) {
		BundleNode bn = (BundleNode) object;
		StringBuffer buf = new StringBuffer(String.valueOf(bn.getId()));
		buf.append(" - ").append(bn.getSymbolicName());
		buf.append(" - ").append(bn.getVersion());
		return buf.toString(); // //$NON-NLS-2$
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		BundleNode bn = (BundleNode) object;
		int bstate = bn.getState();
		String icon = null;
		if (bstate == Bundle.INSTALLED)
			icon = ICON_INSTALLED;
		else {
			boolean active = (bn.isFragment())?(bstate == Bundle.ACTIVE || bstate == Bundle.RESOLVED):(bstate == Bundle.ACTIVE);
			icon = active?ICON_ACTIVE:ICON_RESOLVED;
		}
		if (icon == null)
			return null;
		return Activator.getImageDescriptor(icon);
	}

}
