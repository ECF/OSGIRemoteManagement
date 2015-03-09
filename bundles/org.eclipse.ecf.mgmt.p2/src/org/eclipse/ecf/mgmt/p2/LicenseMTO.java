/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2;

import java.io.Serializable;
import java.net.URI;

public class LicenseMTO implements Serializable {

	private static final long serialVersionUID = -1446772208695740265L;
	private final URI location;
	private final String body;
	private final String uuid;

	public LicenseMTO(URI location, String body, String uuid) {
		this.location = location;
		this.body = body;
		this.uuid = uuid;
	}

	public URI getLocation() {
		return location;
	}

	public String getBody() {
		return body;
	}

	public String getUUID() {
		return uuid;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("LicenseMTO[location=");
		buffer.append(location);
		buffer.append(", body=");
		buffer.append(body);
		buffer.append(", uuid=");
		buffer.append(uuid);
		buffer.append("]");
		return buffer.toString();
	}

}
