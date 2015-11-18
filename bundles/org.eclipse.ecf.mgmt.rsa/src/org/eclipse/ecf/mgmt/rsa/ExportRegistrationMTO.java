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
import org.eclipse.ecf.mgmt.SerializationUtil;

public class ExportRegistrationMTO implements Serializable {

	private static final long serialVersionUID = -3083425723194071464L;
	private final ExportReferenceMTO exportReference;
	private final Throwable exception;

	public ExportRegistrationMTO(ID containerID, long remoteServiceId,
			long exportedServiceId, Map<String, ?> endpointProperties) {
		this.exportReference = new ExportReferenceMTO(containerID,
				remoteServiceId, exportedServiceId, endpointProperties);
		this.exception = null;
	}

	public ExportRegistrationMTO(Throwable exception) {
		this.exception = SerializationUtil.isSerializable(exception) ? exception
				: new Throwable(exception.toString());
		this.exportReference = null;
	}

	public ExportReferenceMTO getExportReference() {
		return exportReference;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "ExportRegistrationMTO [exportReference=" + exportReference
				+ ", exception=" + exception + "]";
	}

}
