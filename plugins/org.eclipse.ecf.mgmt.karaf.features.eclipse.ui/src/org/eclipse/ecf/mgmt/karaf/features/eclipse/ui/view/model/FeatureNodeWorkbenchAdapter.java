/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.jface.resource.ImageDescriptor;

public class FeatureNodeWorkbenchAdapter extends AbstractFeaturesWorkbenchAdapter {

	@Override
	public String getLabel(Object object) {
		FeatureNode kfn = (FeatureNode) object;
		return kfn.getName()+"/"+kfn.getVersion();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

}
