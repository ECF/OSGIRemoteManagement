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

public class RemoteServiceAdminEventMTO implements Serializable {

	private static final long serialVersionUID = 6351054561253577383L;
	private final int type;
	private final long source;
	private final ImportReferenceMTO importReference;
	private final ExportReferenceMTO exportReference;
	private final Throwable exception;

	private RemoteServiceAdminEventMTO(int type, long bundleId, ImportReferenceMTO importReference,
			ExportReferenceMTO exportReference, Throwable exception) {
		this.type = type;
		this.source = bundleId;
		this.importReference = importReference;
		this.exportReference = exportReference;
		this.exception = exception;
	}

	public RemoteServiceAdminEventMTO(int type, long bundleId, ImportReferenceMTO importReference, Throwable exception) {
		this(type, bundleId, importReference, null, exception);
	}

	public RemoteServiceAdminEventMTO(int type, long bundleId, ExportReferenceMTO exportReference, Throwable exception) {
		this(type, bundleId, null, exportReference, exception);
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
