/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesNode;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * @since 3.3
 */
public class AbstractServiceWorkbenchAdapter extends WorkbenchAdapter {

	@Override
	public Object getParent(Object object) {
		return ((AbstractServicesNode) object).getParent();
	}

	@Override
	public Object[] getChildren(Object object) {
		return ((AbstractServicesNode) object).getChildren();
	}

}
