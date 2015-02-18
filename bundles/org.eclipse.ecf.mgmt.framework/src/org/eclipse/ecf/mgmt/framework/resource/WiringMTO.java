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
import java.util.List;

import org.osgi.resource.dto.WireDTO;
import org.osgi.resource.dto.WiringDTO;

public abstract class WiringMTO implements Serializable {

	private static final long serialVersionUID = 8837665580277852913L;
	private final int id;
	private final CapabilityRefMTO[] capabilities;
	private final RequirementRefMTO[] requirements;
	private final WireMTO[] providedWires;
	private final WireMTO[] requiredWires;
	private final int resource;

	public WiringMTO(WiringDTO dto) {
		this.id = dto.id;
		this.capabilities = CapabilityRefMTO.createMTOs(dto.capabilities);
		this.requirements = RequirementRefMTO.createMTOs(dto.requirements);
		this.providedWires = createMTOs(dto.providedWires);
		this.requiredWires = createMTOs(dto.requiredWires);
		this.resource = dto.resource;
	}

	protected abstract WireMTO[] createMTOs(List<WireDTO> dtos);

	public int getId() {
		return id;
	}

	public CapabilityRefMTO[] getCapabilities() {
		return capabilities;
	}

	public RequirementRefMTO[] getRequirements() {
		return requirements;
	}

	public WireMTO[] getProvidedWires() {
		return providedWires;
	}

	public WireMTO[] getRequiredWires() {
		return requiredWires;
	}

	public int getResource() {
		return resource;
	}

}
