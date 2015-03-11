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

public class RemoteServiceAdminEventMTO implements Serializable {

	private static final long serialVersionUID = 6351054561253577383L;
	private final int type;
	private final long source;
	private ImportReferenceMTO importReference;
	private ExportReferenceMTO exportReference;
	private final Throwable exception;

	public RemoteServiceAdminEventMTO(int eventType, long bundleId,
			ID containerID, long remoteServiceId, long exportedServiceId,
			Map<String, ?> endpointProperties, Throwable exception) {
		this.type = eventType;
		this.source = bundleId;
		if (type == 1 || type == 4 || type == 5 || type == 8 || type == 9) {
			this.importReference = new ImportReferenceMTO(containerID,
					remoteServiceId, exportedServiceId, endpointProperties);
			this.exportReference = null;
		} else if (type == 2 || type == 6 || type == 7 || type == 10) {
			this.exportReference = new ExportReferenceMTO(containerID,
					remoteServiceId, exportedServiceId, endpointProperties);
			this.importReference = null;
		}
		this.exception = exception;
	}

	public int getType() {
		return type;
	}

	public long getSource() {
		return source;
	}

	public ImportReferenceMTO getImportReference() {
		return importReference;
	}

	public ExportReferenceMTO getExportReference() {
		return exportReference;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "RemoteServiceAdminEventMTO [type=" + type + ", source="
				+ source + ", importReference=" + importReference
				+ ", exportReference=" + exportReference + ", exception="
				+ exception + "]";
	}
}
