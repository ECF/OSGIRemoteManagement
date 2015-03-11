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
import java.util.Map;

import org.osgi.resource.dto.RequirementDTO;

public class RequirementMTO implements Serializable {

	private static final long serialVersionUID = -3901027051049605998L;
	private final int id;
	private final String namespace;
	private final Map<String, String> directives;
	private final Map<String, Object> attributes;
	private final int resource;

	public static RequirementMTO[] createMTOs(List<RequirementDTO> dtos) {
		List<RequirementMTO> results = new ArrayList<RequirementMTO>(
				dtos.size());
		for (RequirementDTO dto : dtos)
			results.add(new RequirementMTO(dto));
		return results.toArray(new RequirementMTO[results.size()]);
	}

	RequirementMTO(RequirementDTO dto) {
		this.id = dto.id;
		this.namespace = dto.namespace;
		this.directives = dto.directives;
		this.attributes = dto.attributes;
		this.resource = dto.resource;
	}

	public int getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	public Map<String, String> getDirectives() {
		return directives;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public int getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return "RequirementMTO [id=" + id + ", namespace=" + namespace
				+ ", directives=" + directives + ", attributes=" + attributes
				+ ", resource=" + resource + "]";
	}

}
