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
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.mgmt.PropertiesUtil;
import org.osgi.framework.Bundle;

public class BundleMTO implements Serializable {

	private static final long serialVersionUID = -8261289274590963132L;

	public static BundleMTO createMTO(Bundle bundle) {
		if (bundle == null)
			return null;
		return new BundleMTO(bundle);
	}

	public static BundleMTO[] createMTOs(Bundle[] bundles) {
		List<BundleMTO> results = new ArrayList<BundleMTO>();
		for (Bundle b : bundles)
			results.add(createMTO(b));
		return results.toArray(new BundleMTO[results.size()]);
	}

	private final long id;
	private final long lastModified;
	private final int state;
	private final String symbolicName;
	private final String version;
	private final Map<String, String> manifest;
	private final String location;

	BundleMTO(Bundle bundle) {
		this.id = bundle.getBundleId();
		this.lastModified = bundle.getLastModified();
		this.state = bundle.getState();
		this.symbolicName = bundle.getSymbolicName();
		this.version = bundle.getVersion().toString();
		this.manifest = PropertiesUtil.convertHeadersToMap(bundle.getHeaders());
		this.location = bundle.getLocation();
	}
	public long getId() {
		return id;
	}

	public long getLastModified() {
		return lastModified;
	}

	public int getState() {
		return state;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public String getVersion() {
		return version;
	}

	public boolean isFragment() {
		if (manifest == null)
			return false;
		Object result = manifest.get("Fragment-Host"); //$NON-NLS-1$
		if (result instanceof String)
			return true;
		return false;
	}

	public Map<String, String> getManifest() {
		return manifest;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "BundleMTO [id=" + id + ", lastModified=" + lastModified
				+ ", state=" + state + ", symbolicName=" + symbolicName
				+ ", version=" + version + ", manifest=" + manifest
				+ ", location=" + location + "]";
	}

}
