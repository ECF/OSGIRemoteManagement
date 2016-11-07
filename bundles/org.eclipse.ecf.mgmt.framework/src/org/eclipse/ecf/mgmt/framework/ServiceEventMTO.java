/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;

public class ServiceEventMTO implements Serializable {

	private static final long serialVersionUID = 6847277274662855469L;
	private final int type;
	private final ServiceReferenceMTO serviceReferenceMTO;
	
	public ServiceEventMTO(int type, ServiceReferenceMTO sr) {
		this.type = type;
		this.serviceReferenceMTO = sr;
	}
	
	public int getType() {
		return this.type;
	}

	public ServiceReferenceMTO getServiceReferenceMTO() {
		return this.serviceReferenceMTO;
	}

	@Override
	public String toString() {
		return "ServiceEventMTO [type=" + type + ", serviceReferenceMTO=" + serviceReferenceMTO + "]";
	}
	
}
