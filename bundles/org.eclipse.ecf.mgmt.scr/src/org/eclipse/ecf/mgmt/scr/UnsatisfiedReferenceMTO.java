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
import org.osgi.service.component.runtime.dto.UnsatisfiedReferenceDTO;

public class UnsatisfiedReferenceMTO implements Serializable {

	private static final long serialVersionUID = 9024511950073452553L;

	private final ServiceReferenceMTO[] targetServices;
	private final String name;
	private final String target;

	public UnsatisfiedReferenceMTO(UnsatisfiedReferenceDTO usrdto) {
		List<ServiceReferenceMTO> usrmtos = new ArrayList<ServiceReferenceMTO>();
		for (ServiceReferenceDTO dto : usrdto.targetServices)
			usrmtos.add(ServiceReferenceMTO.createMTO(dto));
		this.targetServices = usrmtos.toArray(new ServiceReferenceMTO[usrmtos.size()]);
		this.name = usrdto.name;
		this.target = usrdto.target;
	}

	public ServiceReferenceMTO[] getTargetServices() {
		return targetServices;
	}

	public String getName() {
		return name;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "UnsatisfiedReferenceMTO [targetServices=" + Arrays.toString(targetServices) + ", name=" + name
				+ ", target=" + target + "]";
	}

}
