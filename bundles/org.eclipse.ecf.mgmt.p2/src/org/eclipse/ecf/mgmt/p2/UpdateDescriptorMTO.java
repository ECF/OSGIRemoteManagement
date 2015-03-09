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
import java.net.URI;
import java.util.Collection;

public class UpdateDescriptorMTO implements Serializable {

	private static final long serialVersionUID = -3830182096461703763L;
	private final URI location;
	private final int severity;
	private final String description;
	private final Collection<InstallableUnitMTO> iusBeingUpdated;

	public UpdateDescriptorMTO(URI location, int severity, String description,
			Collection<InstallableUnitMTO> iusBeingUpdated) {
		this.location = location;
		this.severity = severity;
		this.description = description;
		this.iusBeingUpdated = iusBeingUpdated;
	}

	public URI getLocation() {
		return location;
	}

	public Collection<InstallableUnitMTO> getIUSBeingUpdated() {
		return iusBeingUpdated;
	}

	public int getSeverity() {
		return severity;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "UpdateDescriptorMTO [location=" + location + ", severity="
				+ severity + ", description=" + description
				+ ", iusBeingUpdated=" + iusBeingUpdated + "]";
	}

}
