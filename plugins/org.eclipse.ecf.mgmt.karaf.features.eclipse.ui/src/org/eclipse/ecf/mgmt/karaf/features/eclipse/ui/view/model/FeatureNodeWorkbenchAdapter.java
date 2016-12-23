/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.Activator;
import org.eclipse.jface.resource.ImageDescriptor;

public class FeatureNodeWorkbenchAdapter extends AbstractFeaturesWorkbenchAdapter {

	@Override
	public String getLabel(Object object) {
		FeatureNode kfn = (FeatureNode) object;
		return kfn.getName() + "/" + kfn.getVersion();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		FeatureNode kfn = (FeatureNode) object;
		Activator a = Activator.getDefault();
		return kfn.isInstalled() ? a.getIconDescriptor("/icons/feature_inst.png")
				: a.getIconDescriptor("/icons/feature_noinst.png");
	}

}
