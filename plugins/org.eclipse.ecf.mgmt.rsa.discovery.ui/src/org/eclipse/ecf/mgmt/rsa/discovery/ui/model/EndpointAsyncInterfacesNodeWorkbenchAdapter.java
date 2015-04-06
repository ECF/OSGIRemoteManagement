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

public class EndpointAsyncInterfacesNodeWorkbenchAdapter extends
		EndpointInterfacesNodeWorkbenchAdapter {

	private ImageDescriptor asyncInterfacesDesc;

	public EndpointAsyncInterfacesNodeWorkbenchAdapter() {
		asyncInterfacesDesc = RSAImageRegistry.DESC_ASYNC_SERVICE_OBJ;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return asyncInterfacesDesc;
	}
}
