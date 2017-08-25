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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.SatisfiedReferenceDTO;
import org.osgi.service.component.runtime.dto.UnsatisfiedReferenceDTO;

public class ComponentConfigurationMTO implements Serializable {

	private static final long serialVersionUID = -305034570832361805L;
	private final ComponentDescriptionMTO description;
	private final long id;
	private final Map<String, Object> properties;
	private final SatisfiedReferenceMTO[] satisfiedReferences;
	private final int state;
	private final UnsatisfiedReferenceMTO[] unsatisfiedReferences;

	public ComponentConfigurationMTO(ComponentConfigurationDTO ccdto) {
		this.description = new ComponentDescriptionMTO(ccdto.description);
		this.id = ccdto.id;
		this.properties = ccdto.properties;
		List<SatisfiedReferenceMTO> srefs = new ArrayList<SatisfiedReferenceMTO>();
		for (SatisfiedReferenceDTO sdto : ccdto.satisfiedReferences)
			srefs.add(new SatisfiedReferenceMTO(sdto));
		this.satisfiedReferences = srefs.toArray(new SatisfiedReferenceMTO[srefs.size()]);
		this.state = ccdto.state;
		List<UnsatisfiedReferenceMTO> urefs = new ArrayList<UnsatisfiedReferenceMTO>();
		for (UnsatisfiedReferenceDTO udto : ccdto.unsatisfiedReferences)
			urefs.add(new UnsatisfiedReferenceMTO(udto));
		this.unsatisfiedReferences = srefs.toArray(new UnsatisfiedReferenceMTO[urefs.size()]);
	}

	public ComponentDescriptionMTO getDescription() {
		return description;
	}

	public long getId() {
		return id;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public SatisfiedReferenceMTO[] getSatisfiedReferences() {
		return satisfiedReferences;
	}

	public int getState() {
		return state;
	}

	public UnsatisfiedReferenceMTO[] getUnsatisfiedReferences() {
		return unsatisfiedReferences;
	}

	@Override
	public String toString() {
		return "ComponentConfigurationMTO [description=" + description + ", id=" + id + ", properties=" + properties
				+ ", satisfiedReferences=" + Arrays.toString(satisfiedReferences) + ", state=" + state
				+ ", unsatisfiedReferences=" + Arrays.toString(unsatisfiedReferences) + "]";
	}
}
