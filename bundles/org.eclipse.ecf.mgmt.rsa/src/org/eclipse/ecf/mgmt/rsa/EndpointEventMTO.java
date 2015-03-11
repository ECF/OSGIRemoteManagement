/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa;

import java.io.Serializable;
import java.util.Map;

public class EndpointEventMTO implements Serializable {

	private static final long serialVersionUID = -2329552850335950554L;
	private final int type;
	private final EndpointDescriptionMTO endpoint;

	public EndpointEventMTO(int type, Map<String, ?> endpointProps) {
		this.type = type;
		this.endpoint = new EndpointDescriptionMTO(endpointProps);
	}

	public int getType() {
		return type;
	}

	public EndpointDescriptionMTO getEndpoint() {
		return endpoint;
	}

	@Override
	public String toString() {
		return "EndpointEventMTO [type=" + type + ", endpoint=" + endpoint
				+ "]";
	}
}
