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

import org.osgi.resource.dto.WireDTO;

public class WireMTO implements Serializable {

	private static final long serialVersionUID = -6107675731355682032L;
	private final CapabilityRefMTO capability;
	private final RequirementRefMTO requirement;
	private final int provider;
	private final int requirer;

	public WireMTO(WireDTO dto) {
		this.capability = new CapabilityRefMTO(dto.capability);
		this.requirement = new RequirementRefMTO(dto.requirement);
		this.provider = dto.provider;
		this.requirer = dto.requirer;
	}

	public CapabilityRefMTO getCapability() {
		return capability;
	}

	public RequirementRefMTO getRequirement() {
		return requirement;
	}

	public int getProvider() {
		return provider;
	}

	public int getRequirer() {
		return requirer;
	}

}
