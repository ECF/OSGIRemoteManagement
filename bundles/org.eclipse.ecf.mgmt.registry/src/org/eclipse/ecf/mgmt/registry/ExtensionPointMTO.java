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

public class ExtensionPointMTO implements Serializable {

	private static final long serialVersionUID = -8689555393853719536L;
	private final String label;
	private final String namespaceIdentifier;
	private final String simpleIdentifier;
	private final String uniqueIdentifier;
	private final boolean valid;
	private final long contributorId;
	private final ExtensionMTO extensions[];

	public ExtensionPointMTO(String label, String namespaceIdentifier, String simpleIdentifier,
			String uniqueIdentifier, boolean valid, long contributorId, ExtensionMTO[] extensions) {
		this.label = label;
		this.namespaceIdentifier = namespaceIdentifier;
		this.simpleIdentifier = simpleIdentifier;
		this.uniqueIdentifier = uniqueIdentifier;
		this.valid = valid;
		this.contributorId = contributorId;
		this.extensions = extensions;
	}

	public String getLabel() {
		return label;
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

	public ExtensionMTO[] getExtensions() {
		return extensions;
	}

	@Override
	public String toString() {
		return "ExtensionPointMTO [label=" + label + ", namespaceIdentifier=" + namespaceIdentifier
				+ ", simpleIdentifier=" + simpleIdentifier + ", uniqueIdentifier=" + uniqueIdentifier + ", valid="
				+ valid + ", contributorId=" + contributorId + ", extensions=" + Arrays.toString(extensions) + "]";
	}

}
