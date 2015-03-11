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

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.SerializationUtil;

public class ImportRegistrationMTO {

	private final ImportReferenceMTO importReference;
	private final Throwable exception;

	public ImportRegistrationMTO(ID containerID, long remoteServiceId,
			long exportedServiceId, Map<String, ?> endpointProperties) {
		this.importReference = new ImportReferenceMTO(containerID,
				remoteServiceId, exportedServiceId, endpointProperties);
		this.exception = null;
	}

	public ImportRegistrationMTO(Throwable exception) {
		this.exception = SerializationUtil.isSerializable(exception) ? exception
				: new Throwable(exception.toString());
		this.importReference = null;
	}

	public ImportReferenceMTO getImportReference() {
		return importReference;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "ImportRegistrationMTO [importReference=" + importReference
				+ ", exception=" + exception + "]";
	}

}
