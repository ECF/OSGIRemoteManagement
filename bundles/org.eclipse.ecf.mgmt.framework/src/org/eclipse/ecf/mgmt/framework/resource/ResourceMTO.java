/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.resource;

import java.io.Serializable;

import org.osgi.resource.dto.ResourceDTO;

public class ResourceMTO implements Serializable {

	private static final long serialVersionUID = -7226725645623383998L;

	private final int id;
	private final CapabilityMTO[] capabilities;
	private final RequirementMTO[] requirements;

	public ResourceMTO(ResourceDTO dto) {
		this.id = dto.id;
		this.capabilities = CapabilityMTO.createMTOs(dto.capabilities);
		this.requirements = RequirementMTO.createMTOs(dto.requirements);
	}

	public int getId() {
		return id;
	}

	public CapabilityMTO[] getCapabilities() {
		return capabilities;
	}

	public RequirementMTO[] getRequirements() {
		return requirements;
	}

}
