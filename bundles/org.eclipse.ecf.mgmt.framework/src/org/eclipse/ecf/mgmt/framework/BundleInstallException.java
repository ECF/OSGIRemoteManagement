/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

public class BundleInstallException extends RuntimeException {

	private static final long serialVersionUID = -4906841930169620674L;

	public BundleInstallException(String message, Throwable cause) {
		super(message, cause);
	}

	public BundleInstallException(String message) {
		super(message);
	}

	public BundleInstallException(Throwable cause) {
		super(cause);
	}

}
