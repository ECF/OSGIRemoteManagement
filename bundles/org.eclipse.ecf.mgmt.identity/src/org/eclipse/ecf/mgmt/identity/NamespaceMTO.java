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
import java.util.Arrays;

public class NamespaceMTO implements Serializable {

	private static final long serialVersionUID = -5340693867462768695L;
	private final String name;
	private final String description;
	private final String scheme;
	private final String[] supportedSchemes;
	private final String[][] supportedParameterTypes;

	public NamespaceMTO(String name, String description, String scheme, String[] supportedSchemes,
			String[][] supportedParameterTypes) {
		this.name = name;
		this.description = description;
		this.scheme = scheme;
		this.supportedSchemes = supportedSchemes;
		this.supportedParameterTypes = supportedParameterTypes;
	}

	public NamespaceMTO(String name, String description, String scheme) {
		this(name, description, scheme, null, null);
	}
	
	public NamespaceMTO(String name) {
		this(name, null, null);
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getScheme() {
		return scheme;
	}

	public String[] getSupportedSchemes() {
		return supportedSchemes;
	}

	public String[][] getSupportedParameterTypes() {
		return supportedParameterTypes;
	}

	@Override
	public String toString() {
		return "NamespaceMTO [name=" + name + ", description=" + description + ", scheme=" + scheme
				+ ", supportedSchemes=" + Arrays.toString(supportedSchemes) + ", supportedParameterTypes="
				+ Arrays.toString(supportedParameterTypes) + "]";
	}

}
