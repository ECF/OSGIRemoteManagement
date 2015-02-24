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

import org.eclipse.ecf.mgmt.PropertiesUtil;

public class ExportRegistrationMTO {

	private final ExportReferenceMTO exportReference;
	private final Throwable exception;

	public ExportRegistrationMTO(ExportReferenceMTO exportReference) {
		this.exportReference = exportReference;
		this.exception = null;
	}

	public ExportRegistrationMTO(Throwable exception) {
		this.exception = PropertiesUtil.isSerializable(exception) ? exception : new Throwable(exception.toString());
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
		return "ExportRegistrationMTO [exportReference=" + exportReference + ", exception=" + exception + "]";
	}

}
