/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr;

import java.io.Serializable;
import java.util.Objects;

public class ComponentId implements Serializable {

	private static final long serialVersionUID = -1205607852896925134L;

	private final long bundleId;
	private final String name;

	public ComponentId(long bundleId, String name) {
		this.bundleId = bundleId;
		this.name = name;
	}

	public long getBundleId() {
		return bundleId;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof ComponentId))
			return false;
		ComponentId oid = (ComponentId) other;
		return (this.bundleId == oid.bundleId && this.name.equals(oid.name));
	}

	public int hashCode() {
		return Objects.hash(this.bundleId, this.name);
	}

	@Override
	public String toString() {
		return "ComponentId [bundleId=" + bundleId + ", name=" + name + "]";
	}

}
