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

import org.osgi.resource.dto.CapabilityDTO;

public class CapabilityMTO implements Serializable {

	private static final long serialVersionUID = -1589820636299570683L;

	private int id;
	private String namespace;
	private Map<String, String> directives;
	private Map<String, Object> attributes;
	private int resource;

	public static CapabilityMTO[] createMTOs(List<CapabilityDTO> dtos) {
		List<CapabilityMTO> results = new ArrayList<CapabilityMTO>(dtos.size());
		for (CapabilityDTO dto : dtos)
			results.add(new CapabilityMTO(dto));
		return results.toArray(new CapabilityMTO[results.size()]);
	}

	CapabilityMTO(CapabilityDTO dto) {
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
		return "CapabilityMTO [id=" + id + ", namespace=" + namespace
				+ ", directives=" + directives + ", attributes=" + attributes
				+ ", resource=" + resource + "]";
	}

}
