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

import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.component.runtime.dto.SatisfiedReferenceDTO;

public class SatisfiedReferenceMTO implements Serializable {

	private static final long serialVersionUID = 9024511950073452553L;

	private final ServiceReferenceMTO[] boundServices;
	private final String name;
	private final String target;

	public SatisfiedReferenceMTO(SatisfiedReferenceDTO srdto) {
		List<ServiceReferenceMTO> srmtos = new ArrayList<ServiceReferenceMTO>();
		for (ServiceReferenceDTO dto : srdto.boundServices)
			srmtos.add(ServiceReferenceMTO.createMTO(dto));
		this.boundServices = srmtos.toArray(new ServiceReferenceMTO[srmtos.size()]);
		this.name = srdto.name;
		this.target = srdto.target;
	}

	public ServiceReferenceMTO[] getBoundServices() {
		return boundServices;
	}

	public String getName() {
		return name;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "SatisfiedReferenceMTO [boundServices=" + Arrays.toString(boundServices) + ", name=" + name + ", target="
				+ target + "]";
	}

}
