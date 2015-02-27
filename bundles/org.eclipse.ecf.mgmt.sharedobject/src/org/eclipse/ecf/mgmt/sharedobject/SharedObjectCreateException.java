/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.sharedobject;

import org.eclipse.ecf.mgmt.PropertiesUtil;

public class SharedObjectCreateException extends Exception {

	private static final long serialVersionUID = 3118008197172201124L;

	public SharedObjectCreateException(String message) {
		super(message);
	}

	public SharedObjectCreateException(Throwable cause) {
		super(cause == null ? null : PropertiesUtil.isSerializable(cause) ? cause : new Throwable(cause.getMessage()));
	}

	public SharedObjectCreateException(String message, Throwable cause) {
		super(message, cause == null ? null : PropertiesUtil.isSerializable(cause) ? cause : new Throwable(
				cause.getMessage()));
	}

	public SharedObjectCreateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause == null ? null : PropertiesUtil.isSerializable(cause) ? cause : new Throwable(
				cause.getMessage()), enableSuppression, writableStackTrace);
	}

}
