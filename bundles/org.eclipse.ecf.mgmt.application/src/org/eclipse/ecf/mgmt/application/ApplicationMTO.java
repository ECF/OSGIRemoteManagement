/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.application;

import java.io.Serializable;
import java.util.Map;

public class ApplicationMTO implements Serializable {

	private static final long serialVersionUID = 1056529815986643544L;
	private final String id;
	private final Map<String, ?> properties;

	public ApplicationMTO(String id, Map<String, ?> properties) {
		this.id = id;
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return (String) properties.get("application.name");
	}

	public Map<String, ?> getProperties() {
		return properties;
	}

	private boolean getBooleanProperty(String name) {
		Object propValue = properties.get(name); //$NON-NLS-1$
		boolean isProp = false;
		if (propValue instanceof Boolean)
			isProp = ((Boolean) propValue).booleanValue();
		else if (propValue instanceof String)
			isProp = Boolean.getBoolean((String) propValue);
		return isProp;
	}

	public boolean isLocked() {
		return getBooleanProperty("application.locked");
	}

	public boolean isLaunchable() {
		return getBooleanProperty("application.launchable"); //$NON-NLS-1$
	}

	public boolean isVisible() {
		return getBooleanProperty("application.visible"); //$NON-NLS-1$
	}

	@Override
	public String toString() {
		return "ApplicationMTO [getId()=" + getId() + ", getName()="
				+ getName() + ", isLocked()=" + isLocked()
				+ ", isLaunchable()=" + isLaunchable() + ", isVisible()="
				+ isVisible() + ", getProperties()=" + getProperties() + "]";
	}

}
