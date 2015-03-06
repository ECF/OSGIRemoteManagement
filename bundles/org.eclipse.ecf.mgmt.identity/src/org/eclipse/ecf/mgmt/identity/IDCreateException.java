/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.identity;

import org.eclipse.ecf.mgmt.SerializationUtil;

public class IDCreateException extends RuntimeException {

	private static final long serialVersionUID = 2861221573666918411L;

	public IDCreateException(String message) {
		super(message);
	}

	public IDCreateException(Throwable cause) {
		super(SerializationUtil.isSerializable(cause) ? cause : new Throwable(cause.getMessage()));
	}

	public IDCreateException(String message, Throwable cause) {
		super(message, SerializationUtil.isSerializable(cause) ? cause : new Throwable(cause.getMessage()));
	}

}
