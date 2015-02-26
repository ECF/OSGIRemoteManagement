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
import java.util.Arrays;

public class ContainerTypeDescriptionMTO implements Serializable {

	private static final long serialVersionUID = -1052125446263792157L;
	private final String name;
	private final String description;
	private final boolean hidden;
	private final boolean server;
	private final String[] supportedAdapterTypes;
	private final String[][] supportedParameterTypes;
	private final String[] supportedIntents;
	private final String[] supportedConfigs;

	public ContainerTypeDescriptionMTO(String name, String description,
			boolean hidden, boolean server, String[] supportedAdapterTypes,
			String[][] supportedParameterTypes, String[] supportedIntents,
			String[] supportedConfigs) {
		this.name = name;
		this.description = description;
		this.hidden = hidden;
		this.server = server;
		this.supportedAdapterTypes = supportedAdapterTypes;
		this.supportedParameterTypes = supportedParameterTypes;
		this.supportedIntents = supportedIntents;
		this.supportedConfigs = supportedConfigs;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isServer() {
		return server;
	}

	public String[] getSupportedAdapterTypes() {
		return supportedAdapterTypes;
	}

	public String[][] getSupportedParameterTypes() {
		return supportedParameterTypes;
	}

	public String[] getSupportedIntents() {
		return supportedIntents;
	}

	public String[] getSupportedConfigs() {
		return supportedConfigs;
	}

	@Override
	public String toString() {
		return "ContainerTypeDescriptionMTO [name=" + name + ", description="
				+ description + ", hidden=" + hidden + ", server=" + server
				+ ", supportedAdapterTypes="
				+ Arrays.toString(supportedAdapterTypes)
				+ ", supportedParameterTypes="
				+ Arrays.toString(supportedParameterTypes)
				+ ", supportedIntents=" + Arrays.toString(supportedIntents)
				+ ", supportedConfigs=" + Arrays.toString(supportedConfigs)
				+ "]";
	}

}
