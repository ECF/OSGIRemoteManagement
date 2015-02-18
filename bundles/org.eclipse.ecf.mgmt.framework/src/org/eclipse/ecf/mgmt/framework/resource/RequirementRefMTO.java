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

import org.osgi.resource.dto.RequirementRefDTO;

public class RequirementRefMTO implements Serializable {

	private static final long serialVersionUID = -2330456167907867360L;
	private int requirement;
	private int resource;

	public static RequirementRefMTO[] createMTOs(List<RequirementRefDTO> dtos) {
		List<RequirementRefMTO> results = new ArrayList<RequirementRefMTO>(
				dtos.size());
		for (RequirementRefDTO dto : dtos)
			results.add(new RequirementRefMTO(dto));
		return results.toArray(new RequirementRefMTO[results.size()]);
	}

	public RequirementRefMTO(RequirementRefDTO dto) {
		this.requirement = dto.requirement;
		this.resource = dto.resource;
	}

	public int getRequirement() {
		return requirement;
	}

	public int getResource() {
		return resource;
	}

}
