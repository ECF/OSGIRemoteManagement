/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.discovery.ui.model;

import org.osgi.service.remoteserviceadmin.RemoteConstants;

public class EndpointServiceIDNode extends EndpointPropertyNode {

	public EndpointServiceIDNode() {
		super(RemoteConstants.ENDPOINT_SERVICE_ID);
		setPropertyAlias("Service Id");
	}

	@Override
	public Object getPropertyValue() {
		return getEndpointDescription().getServiceId();
	}
}
