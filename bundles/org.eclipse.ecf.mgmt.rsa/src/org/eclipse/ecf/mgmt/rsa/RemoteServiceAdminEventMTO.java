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

import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ImportReference;

public class RemoteServiceAdminEventMTO implements Serializable {

	private static final long serialVersionUID = 6351054561253577383L;
	private final int type;
	private final long source;
	private final ImportReferenceMTO importReference;
	private final ExportReferenceMTO exportReference;
	private final Throwable exception;

	public RemoteServiceAdminEventMTO(RemoteServiceAdmin.RemoteServiceAdminEvent event) {
		this.type = event.getType();
		this.source = event.getSource().getBundleId();
		ImportReference ir = event.getImportReference();
		this.importReference = ir == null ? null : new ImportReferenceMTO(
				(org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) ir.getImportedEndpoint());
		ExportReference er = event.getExportReference();
		this.exportReference = er == null ? null : new ExportReferenceMTO(
				(org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) er.getExportedEndpoint());
		this.exception = event.getException();
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
		return "RemoteServiceAdminEventMTO [type=" + type + ", source=" + source + ", importReference="
				+ importReference + ", exportReference=" + exportReference + ", exception=" + exception + "]";
	}
}
