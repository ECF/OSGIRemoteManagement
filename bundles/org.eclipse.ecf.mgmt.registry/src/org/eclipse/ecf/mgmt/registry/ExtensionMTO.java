/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.registry;

import java.io.Serializable;
import java.util.Arrays;

public class ExtensionMTO implements Serializable {

	private static final long serialVersionUID = -6840724444591107433L;
	private String label;
	private String identifier;
	private String namespaceIdentifier;
	private String simpleIdentifier;
	private String uniqueIdentifier;
	private boolean valid;
	private long contributorId;
	private ConfigurationElementMTO configurationElements[];

	public ExtensionMTO(String label, String identifier, String namespaceIdentifier, String simpleIdentifier,
			String uniqueIdentifier, boolean valid, long contributorId,
			ConfigurationElementMTO[] configurationElementInfos) {
		this.label = label;
		this.identifier = identifier;
		this.namespaceIdentifier = namespaceIdentifier;
		this.simpleIdentifier = simpleIdentifier;
		this.uniqueIdentifier = uniqueIdentifier;
		this.valid = valid;
		this.contributorId = contributorId;
		this.configurationElements = configurationElementInfos;
	}

	public String getLabel() {
		return label;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getNamespaceIdentifier() {
		return namespaceIdentifier;
	}

	public String getSimpleIdentifier() {
		return simpleIdentifier;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public boolean isValid() {
		return valid;
	}

	public long getContributorId() {
		return contributorId;
	}

	public ConfigurationElementMTO[] getConfigurationElements() {
		return configurationElements;
	}

	@Override
	public String toString() {
		return "ExtensionMTO [label=" + label + ", identifier=" + identifier + ", namespaceIdentifier="
				+ namespaceIdentifier + ", simpleIdentifier=" + simpleIdentifier + ", uniqueIdentifier="
				+ uniqueIdentifier + ", valid=" + valid + ", contributorId=" + contributorId
				+ ", configurationElements=" + Arrays.toString(configurationElements) + "]";
	}

}
