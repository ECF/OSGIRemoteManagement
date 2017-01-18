/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.cm;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Map;

import org.eclipse.ecf.mgmt.PropertiesUtil;

public class ConfigurationMTO implements Serializable {

	private static final long serialVersionUID = -5191641448962102771L;

	private final String bundleLocation;
	private final long changeCount;
	private final String factoryPid;
	private final String pid;
	private final Map<String,Object> properties;
	
	@SuppressWarnings("unchecked")
	public ConfigurationMTO(String bundleLocation, long changeCount, String factoryPid, String pid, Dictionary<String,Object> properties) {
		this.bundleLocation = bundleLocation;
		this.changeCount = changeCount;
		this.factoryPid = factoryPid;
		this.pid = pid;
		this.properties = (Map<String,Object>) PropertiesUtil.convertDictionaryToMap(properties);
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getBundleLocation() {
		return bundleLocation;
	}

	public long getChangeCount() {
		return changeCount;
	}

	public String getFactoryPid() {
		return factoryPid;
	}

	public String getPid() {
		return pid;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	@Override
	public String toString() {
		return "ConfigurationMTO [bundleLocation=" + bundleLocation + ", changeCount=" + changeCount + ", factoryPid="
				+ factoryPid + ", pid=" + pid + ", properties=" + properties + "]";
	}

}
