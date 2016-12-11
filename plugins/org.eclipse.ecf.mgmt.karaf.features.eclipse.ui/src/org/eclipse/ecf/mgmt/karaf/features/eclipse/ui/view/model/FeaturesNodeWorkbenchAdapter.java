/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.ecf.remoteservices.ui.RSAImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 */
public class FeaturesNodeWorkbenchAdapter extends AbstractFeaturesWorkbenchAdapter {

	@Override
	public String getLabel(Object object) {
		return ((FeaturesNode) object).getName();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return RSAImageRegistry.ENDPOINTDESCRIPTION_OBJ;
	}
}
