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
import java.util.ArrayList;
import java.util.List;

import org.osgi.resource.dto.CapabilityRefDTO;

public class CapabilityRefMTO implements Serializable {

	private static final long serialVersionUID = -3574647331744805983L;
	private int capability;
	private int resource;

	public static CapabilityRefMTO[] createMTOs(List<CapabilityRefDTO> dtos) {
		List<CapabilityRefMTO> results = new ArrayList<CapabilityRefMTO>(
				dtos.size());
		for (CapabilityRefDTO dto : dtos)
			results.add(new CapabilityRefMTO(dto));
		return results.toArray(new CapabilityRefMTO[results.size()]);
	}

	public CapabilityRefMTO(CapabilityRefDTO dto) {
		this.capability = dto.capability;
		this.resource = dto.resource;
	}

	public int getCapability() {
		return capability;
	}

	public int getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "CapabilityRefMTO [capability=" + capability + ", resource="
				+ resource + "]";
	}

}
