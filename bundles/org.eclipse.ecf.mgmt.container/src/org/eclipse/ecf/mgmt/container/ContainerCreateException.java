/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.container;

import org.eclipse.ecf.mgmt.SerializationUtil;

public class ContainerCreateException extends Exception {

	private static final long serialVersionUID = -1553113325873646907L;

	public ContainerCreateException(String message) {
		super(message);
	}

	public ContainerCreateException(Throwable cause) {
		super(cause);
	}

	public ContainerCreateException(String message, Throwable cause) {
		super(message, (cause == null) ? null : SerializationUtil
				.isSerializable(cause) ? cause : new Throwable(
				cause.getMessage()));
	}

	public ContainerCreateException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, (cause == null) ? null : SerializationUtil
				.isSerializable(cause) ? cause : new Throwable(
				cause.getMessage()), enableSuppression, writableStackTrace);
	}

}
