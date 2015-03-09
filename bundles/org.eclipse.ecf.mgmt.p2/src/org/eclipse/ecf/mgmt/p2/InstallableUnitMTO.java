/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class InstallableUnitMTO implements Serializable {

	private static final long serialVersionUID = 7741324381427433225L;
	private final VersionedId id;
	private final Map<String,?> properties;
	private final boolean singleton;
	private final boolean resolved;
	private final Collection<LicenseMTO> licenses;
	private final CopyrightMTO copyright;
	private final UpdateDescriptorMTO updateDescriptor;

	public InstallableUnitMTO(VersionedId id, Map<String,?> properties,
			boolean singleton, boolean resolved,
			Collection<LicenseMTO> licenses, CopyrightMTO copyright,
			UpdateDescriptorMTO updateDescriptor) {
		this.id = id;
		this.properties = properties;
		this.singleton = singleton;
		this.resolved = resolved;
		this.licenses = licenses;
		this.copyright = copyright;
		this.updateDescriptor = updateDescriptor;
	}

	public VersionedId getId() {
		return id;
	}

	public Map<String,?> getProperties() {
		return properties;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public boolean isResolved() {
		return resolved;
	}

	public Collection getLicenses() {
		return licenses;
	}

	public CopyrightMTO getCopyright() {
		return copyright;
	}

	public UpdateDescriptorMTO getUpdateDescriptor() {
		return updateDescriptor;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("InstallableUnitMTO[id=");
		buffer.append(id);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", singleton=");
		buffer.append(singleton);
		buffer.append(", resolved=");
		buffer.append(resolved);
		buffer.append(", licenses=");
		buffer.append(licenses);
		buffer.append(", copyright=");
		buffer.append(copyright);
		buffer.append(", updateDescriptor=");
		buffer.append(updateDescriptor);
		buffer.append("]");
		return buffer.toString();
	}

}
