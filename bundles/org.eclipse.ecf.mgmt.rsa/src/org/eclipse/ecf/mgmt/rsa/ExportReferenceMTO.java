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

import org.eclipse.ecf.core.identity.ID;

public class ExportReferenceMTO implements Serializable {

	private static final long serialVersionUID = -4593358591603858549L;

	private final ID containerID;
	private final long remoteServiceId;
	private final long exportedService;
	private final EndpointDescriptionMTO endpoint;

	public ExportReferenceMTO(ID containerID, long remoteServiceId,
			long exportedServiceId, Map<String, ?> endpointProperties) {
		super();
		this.containerID = containerID;
		this.remoteServiceId = remoteServiceId;
		this.exportedService = exportedServiceId;
		this.endpoint = new EndpointDescriptionMTO(endpointProperties);
	}

	public ID getContainerID() {
		return containerID;
	}

	public long getRemoteServiceId() {
		return remoteServiceId;
	}

	public long getExportedService() {
		return exportedService;
	}

	public EndpointDescriptionMTO getExportedEndpoint() {
		return endpoint;
	}

	@Override
	public String toString() {
		return "ExportReferenceMTO [containerID=" + containerID
				+ ", remoteServiceId=" + remoteServiceId + ", exportedService="
				+ exportedService + ", endpoint=" + endpoint + "]";
	}

}
