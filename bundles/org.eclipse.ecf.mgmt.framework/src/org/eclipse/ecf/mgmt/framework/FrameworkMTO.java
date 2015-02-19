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

public class FrameworkMTO implements Serializable {

	private static final long serialVersionUID = 5986069380441758470L;

	private final BundleMTO[] bundles;
	private final Map<String, Object> properties;
	private final ServiceReferenceMTO[] services;

	public FrameworkMTO(BundleMTO[] bundles, Map<String, Object> properties, ServiceReferenceMTO[] services) {
		this.bundles = bundles;
		this.properties = properties;
		this.services = services;
	}

	public BundleMTO[] getBundles() {
		return bundles;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public ServiceReferenceMTO[] getServiceReferences() {
		return services;
	}

	@Override
	public String toString() {
		return "FrameworkMTO [bundles=" + Arrays.toString(bundles) + ", properties=" + properties + ", services="
				+ Arrays.toString(services) + "]";
	}

}
