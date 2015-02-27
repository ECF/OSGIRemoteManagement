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

import java.io.Serializable;

import org.eclipse.ecf.mgmt.identity.IDMTO;

public class SharedObjectMTO implements Serializable {

	private static final long serialVersionUID = -4363277618074727072L;
	private final IDMTO id;
	private final String className;
	
	public SharedObjectMTO(IDMTO id, String className) {
		this.id = id;
		this.className = className;
	}

	public IDMTO getId() {
		return id;
	}

	public String getClassname() {
		return className;
	}

	@Override
	public String toString() {
		return "SharedObjectMTO [id=" + id + ", className=" + className + "]";
	}
	
}
