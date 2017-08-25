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

import org.osgi.service.component.runtime.dto.ReferenceDTO;

public class ReferenceMTO implements Serializable {

	private static final long serialVersionUID = 4083523150834730041L;

	private final String bind;
	private final String cardinality;
	private final String field;
	private final String fieldOption;
	private final String interfaceName;
	private final String name;
	private final String policy;
	private final String policyOption;
	private final String scope;
	private final String target;
	private final String unbind;
	private final String updated;

	public ReferenceMTO(ReferenceDTO dto) {
		super();
		this.bind = dto.bind;
		this.cardinality = dto.cardinality;
		this.field = dto.field;
		this.fieldOption = dto.fieldOption;
		this.interfaceName = dto.interfaceName;
		this.name = dto.name;
		this.policy = dto.policy;
		this.policyOption = dto.policyOption;
		this.scope = dto.scope;
		this.target = dto.target;
		this.unbind = dto.unbind;
		this.updated = dto.updated;

	}

	public String getBind() {
		return bind;
	}

	public String getCardinality() {
		return cardinality;
	}

	public String getField() {
		return field;
	}

	public String getFieldOption() {
		return fieldOption;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getName() {
		return name;
	}

	public String getPolicy() {
		return policy;
	}

	public String getPolicyOption() {
		return policyOption;
	}

	public String getScope() {
		return scope;
	}

	public String getTarget() {
		return target;
	}

	public String getUnbind() {
		return unbind;
	}

	public String getUpdated() {
		return updated;
	}

	@Override
	public String toString() {
		return "ReferenceMTO [bind=" + bind + ", cardinality=" + cardinality + ", field=" + field + ", fieldOption="
				+ fieldOption + ", interfaceName=" + interfaceName + ", name=" + name + ", policy=" + policy
				+ ", policyOption=" + policyOption + ", scope=" + scope + ", target=" + target + ", unbind=" + unbind
				+ ", updated=" + updated + "]";
	}

}
