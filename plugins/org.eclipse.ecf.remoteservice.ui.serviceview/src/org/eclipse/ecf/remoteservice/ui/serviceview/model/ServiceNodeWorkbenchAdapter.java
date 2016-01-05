/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview.model;

import org.eclipse.ecf.remoteservices.ui.RSAImageRegistry;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 */
public class ServiceNodeWorkbenchAdapter extends AbstractServicesWorkbenchAdapter {

	@Override
	public String getLabel(Object object) {
		ServiceNode sn = (ServiceNode) object;
		return PropertyUtils.convertStringArrayToString(sn.getServiceInterfaces()); // $NON-NLS-1$
																					// //$NON-NLS-2$
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		int eiState = ((ServiceNode) object).getExportedImportedState();
		if (eiState == 2)
			return RSAImageRegistry.RSPROXY_OBJ;
		else if (eiState == 1)
			return RSAImageRegistry.RS_OBJ;
		else
			return RSAImageRegistry.INTERFACE_OBJ;
	}

}
