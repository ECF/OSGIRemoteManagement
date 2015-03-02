/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.app;

import java.io.Serializable;
import java.util.Map;

public class AppMTO implements Serializable {

	private static final long serialVersionUID = 1056529815986643544L;
	private final String id;
	private final String name;
	private final Map<String,?> properties;
	private final boolean locked;
	private final boolean launchable;
	private final boolean visible;

	public AppMTO(String id, String name,
			Map<String,?> properties, boolean locked,
			boolean launchable, boolean visible) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.locked = locked;
		this.launchable = launchable;
		this.visible = visible;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<String,?> getProperties() {
		return properties;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isLaunchable() {
		return launchable;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public String toString() {
		return "AppMTO [id=" + id + ", name=" + name + ", properties=" + properties + ", locked=" + locked
				+ ", launchable=" + launchable + ", visible=" + visible + "]";
	}

}
