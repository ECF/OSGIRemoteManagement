/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.discovery.ui.model;

import org.eclipse.ecf.internal.mgmt.rsa.discovery.ui.RSAImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;

public class EndpointInterfacesNodeWorkbenchAdapter extends
		AbstractEndpointNodeWorkbenchAdapter {

	private ImageDescriptor interfacesDesc;

	public EndpointInterfacesNodeWorkbenchAdapter() {
		interfacesDesc = RSAImageRegistry.DESC_SERVICE_OBJ;
	}

	@Override
	public String getLabel(Object object) {
		return ((EndpointInterfacesNode) object).getPropertyValue().toString();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return interfacesDesc;
	}
}
