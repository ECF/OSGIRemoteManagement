/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.dto.ServiceReferenceDTO;

public class ServiceReferenceMTO implements Serializable {

	private static final long serialVersionUID = -4088391130982105496L;

	private final long id;
	private final long bundle;
	private final Map<String, Object> properties;
	private final long[] usingBundles;

	public static ServiceReferenceMTO createMTO(ServiceReferenceDTO dto) {
		return new ServiceReferenceMTO(dto);
	}

	public static ServiceReferenceMTO[] createMTOs(ServiceReferenceDTO[] dtos) {
		List<ServiceReferenceMTO> results = new ArrayList<ServiceReferenceMTO>();
		for (ServiceReferenceDTO dto : dtos)
			results.add(createMTO(dto));
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	ServiceReferenceMTO(ServiceReferenceDTO srDTO) {
		this.id = srDTO.id;
		this.bundle = srDTO.bundle;
		this.properties = srDTO.properties;
		this.usingBundles = srDTO.usingBundles;
	}

	public long getId() {
		return id;
	}

	public long getBundle() {
		return bundle;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public long[] getUsingBundles() {
		return usingBundles;
	}

	public String[] getServices() {
		return (String[]) properties.get(Constants.OBJECTCLASS);
	}
	
	public int getRanking() {
		Integer ranking = (Integer) properties.get(Constants.SERVICE_RANKING);
		if (ranking == null) return 0;
		else return ranking.intValue();
	}
	
	@Override
	public String toString() {
		return "ServiceReferenceMTO [id=" + id + ", bundle=" + bundle + ", properties=" + properties
				+ ", usingBundles=" + Arrays.toString(usingBundles) + "]";
	}

}
