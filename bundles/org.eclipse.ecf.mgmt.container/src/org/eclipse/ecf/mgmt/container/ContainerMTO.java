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

import java.io.Serializable;

import org.eclipse.ecf.mgmt.identity.IDMTO;
import org.eclipse.ecf.mgmt.identity.NamespaceMTO;

public class ContainerMTO implements Serializable {

	private static final long serialVersionUID = -1565412302911016694L;
	private final IDMTO id;
	private final IDMTO connectedID;
	private final NamespaceMTO connectNamespace;
	private final ContainerTypeDescriptionMTO containerTypeDescription;
	private final String className;

	public ContainerMTO(IDMTO id, IDMTO connectedID, NamespaceMTO namespace,
			ContainerTypeDescriptionMTO containerTypeDescription,
			String className) {
		this.id = id;
		this.connectedID = connectedID;
		this.connectNamespace = namespace;
		this.containerTypeDescription = containerTypeDescription;
		this.className = className;
	}

	public IDMTO getID() {
		return id;
	}

	public IDMTO getConnectedID() {
		return connectedID;
	}

	public NamespaceMTO getNamespace() {
		return connectNamespace;
	}

	public ContainerTypeDescriptionMTO getContainerTypeDescription() {
		return containerTypeDescription;
	}

	public String getClassname() {
		return className;
	}

	@Override
	public String toString() {
		return "ContainerMTO[id=" + id + ", connectedID=" + connectedID
				+ ", connectNamespace=" + connectNamespace
				+ ", containerTypeDescription=" + containerTypeDescription
				+ ", className=" + className + "]";
	}

}
