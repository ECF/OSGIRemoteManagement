/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * @since 3.3
 */
public class AbstractFeaturesWorkbenchAdapter extends WorkbenchAdapter {

	@Override
	public Object getParent(Object object) {
		return ((AbstractFeaturesNode) object).getParent();
	}

	@Override
	public Object[] getChildren(Object object) {
		return ((AbstractFeaturesNode) object).getChildren();
	}

}
