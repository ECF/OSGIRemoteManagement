/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview.model;

import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.framework.Bundle;

public class BundleNode extends AbstractBundlesNode {

	private final long id;
	private final long lastModified;
	private final int state;
	private final String symbolicName;
	private final String version;
	private final Map<String, String> manifest;
	private final String location;

	public BundleNode(long id, long lastModified, int state, String symbolicName, String version,
			Map<String, String> manifest, String location) {
		this.id = id;
		this.lastModified = lastModified;
		this.state = state;
		this.symbolicName = symbolicName;
		this.version = version;
		this.manifest = manifest;
		this.location = location;
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

	public String getStateLabel() {
		int s = getState();
		switch (s) {
		case Bundle.ACTIVE:
			return "Active";
		case Bundle.INSTALLED:
			return "Installed";
		case Bundle.RESOLVED:
			return "Resolved";
		case Bundle.STARTING:
			return "Starting";
		case Bundle.STOPPING:
			return "Stopping";
		case Bundle.UNINSTALLED:
			return "Uninstalled";
		default:
			return "Unknown";
		}
	}

	public boolean isFragment() {
		 return getManifest().containsKey("Fragment-Host");
	}
	
	public String getSymbolicName() {
		return symbolicName;
	}

	public String getVersion() {
		return version;
	}

	public Map<String, String> getManifest() {
		return manifest;
	}

	public String getLocation() {
		return location;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class)
			return new BundlePropertySource(getManifest());
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}


}
