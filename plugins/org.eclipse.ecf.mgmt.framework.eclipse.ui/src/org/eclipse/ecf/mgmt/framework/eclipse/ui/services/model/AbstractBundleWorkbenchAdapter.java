/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.remoteservice.ui.bundleview.model.AbstractBundlesNode;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * @since 3.3
 */
public class AbstractBundleWorkbenchAdapter extends WorkbenchAdapter {

	@Override
	public Object getParent(Object object) {
		return ((AbstractBundlesNode) object).getParent();
	}

	@Override
	public Object[] getChildren(Object object) {
		return ((AbstractBundlesNode) object).getChildren();
	}

}
