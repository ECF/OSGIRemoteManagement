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

public class ImportReferenceMTO implements Serializable {

	private static final long serialVersionUID = -3517044534420251616L;
	private final ID containerID;
	private final long remoteServiceId;
	private final long importedService;
	private final EndpointDescriptionMTO endpoint;

	public ImportReferenceMTO(ID containerID, long remoteServiceId, long exportedServiceId,
			Map<String, ?> endpointProperties) {
		super();
		this.containerID = containerID;
		this.remoteServiceId = remoteServiceId;
		this.importedService = exportedServiceId;
		this.endpoint = new EndpointDescriptionMTO(endpointProperties);
	}

	public ID getContainerID() {
		return containerID;
	}

	public long getRemoteServiceId() {
		return remoteServiceId;
	}

	public long getImportedService() {
		return importedService;
	}

	public EndpointDescriptionMTO getImportedEndpoint() {
		return endpoint;
	}

	@Override
	public String toString() {
		return "ImportReferenceMTO [containerID=" + containerID + ", remoteServiceId=" + remoteServiceId
				+ ", importedService=" + importedService + ", endpoint=" + endpoint + "]";
	}

}
