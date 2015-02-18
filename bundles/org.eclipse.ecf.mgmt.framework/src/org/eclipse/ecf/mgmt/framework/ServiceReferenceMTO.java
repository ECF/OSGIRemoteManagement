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
import java.util.Arrays;
import java.util.Map;

import org.osgi.framework.dto.ServiceReferenceDTO;

public class ServiceReferenceMTO implements Serializable {

	private static final long serialVersionUID = -4088391130982105496L;

	private final long id;
	private final long bundle;
	private final Map<String, Object> properties;
	private final long[] usingBundles;

	public ServiceReferenceMTO(ServiceReferenceDTO srDTO) {
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

	@Override
	public String toString() {
		return "ServiceReferenceMTO [id=" + id + ", bundle=" + bundle
				+ ", properties=" + properties + ", usingBundles="
				+ Arrays.toString(usingBundles) + "]";
	}

}
