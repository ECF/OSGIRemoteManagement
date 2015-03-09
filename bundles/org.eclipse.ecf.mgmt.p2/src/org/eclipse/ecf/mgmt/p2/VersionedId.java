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

public class VersionedId implements Serializable {

	private static final long serialVersionUID = -4602875522331954194L;
	private final String id;
	private final String version;

	private int hashCode;

	public VersionedId(String id) {
		this(id, null);
	}

	public VersionedId(String id, String version) {
		this.id = id;
		this.version = version;
		int hc = 37 + id.hashCode();
		this.hashCode = hc
				+ ((this.version == null) ? 0 : this.version.hashCode());
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public int hashCode() {
		return hashCode;
	}

	private boolean versionsEqual(String otherVersion) {
		if (version == null) {
			return (otherVersion == null);
		} else {
			return version.equals(otherVersion);
		}
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof VersionedId))
			return false;
		VersionedId other = (VersionedId) o;
		if (other.id.equals(id))
			return versionsEqual(other.version);
		else
			return false;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("VersionedId[id="); //$NON-NLS-1$
		buffer.append(id);
		buffer.append(", version="); //$NON-NLS-1$
		buffer.append(version);
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}

}
