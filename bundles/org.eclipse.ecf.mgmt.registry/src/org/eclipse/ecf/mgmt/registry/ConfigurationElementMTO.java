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
import java.util.Map;

public class ConfigurationElementMTO implements Serializable {

	private static final long serialVersionUID = -7322749309516518506L;
	private final String name;
	private final String value;
	private final String namespaceIdentifier;
	private final String extensionId;
	private final long contributorId;
	private final boolean valid;
	private final Map<String, String> attributes;
	private ConfigurationElementMTO parent;
	private final ConfigurationElementMTO children[];

	public ConfigurationElementMTO(String name, String value,
			String namespaceIdentifier, String extensionId, long contributorId,
			boolean valid, Map<String, String> attributes,
			ConfigurationElementMTO[] children) {
		this.name = name;
		this.value = value;
		this.namespaceIdentifier = namespaceIdentifier;
		this.extensionId = extensionId;
		this.contributorId = contributorId;
		this.valid = valid;
		this.attributes = attributes;
		this.children = children;
		if (this.children != null)
			for (int i = 0; i < this.children.length; i++)
				this.children[i].parent = this;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getNamespaceIdentifier() {
		return namespaceIdentifier;
	}

	public String getExtensionId() {
		return extensionId;
	}

	public long getContributorId() {
		return contributorId;
	}

	public boolean isValid() {
		return valid;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public ConfigurationElementMTO getParent() {
		return parent;
	}

	public ConfigurationElementMTO[] getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "ConfigurationElementMTO [name=" + name + ", value=" + value
				+ ", namespaceIdentifier=" + namespaceIdentifier
				+ ", extensionId=" + extensionId + ", contributorId="
				+ contributorId + ", valid=" + valid + ", attributes="
				+ attributes + ", parent=" + parent + ", children="
				+ Arrays.toString(children) + "]";
	}

}
