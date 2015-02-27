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

import java.io.Serializable;

public class IDMTO implements Serializable {

	private static final long serialVersionUID = -629589443879094652L;

	private final String name;
	private final NamespaceMTO namespace;
	private final String externalForm;

	public IDMTO(NamespaceMTO namespace, String name, String externalForm) {
		this.namespace = namespace;
		this.name = name;
		this.externalForm = externalForm;
	}

	public IDMTO(NamespaceMTO namespace, String name) {
		this(namespace, name, name);
	}

	public IDMTO(String namespaceName, String name) {
		this(new NamespaceMTO(namespaceName), name);
	}

	public IDMTO(String stringIDName) {
		this(new NamespaceMTO("org.eclipse.ecf.core.identity.StringID"), stringIDName);
	}

	public String getName() {
		return name;
	}

	public NamespaceMTO getNamespace() {
		return namespace;
	}

	public String getExternalForm() {
		return externalForm;
	}

	@Override
	public String toString() {
		return "IDMTO [name=" + name + ", namespace=" + namespace + ", externalForm=" + externalForm + "]";
	}

}
