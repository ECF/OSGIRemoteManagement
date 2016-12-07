/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features;

import java.io.Serializable;

public class DependencyMTO implements Serializable {

	private static final long serialVersionUID = -5370475742188165009L;
	private String name;
	private String version;
	private boolean prerequisite;
	private boolean dependency;
	
	public DependencyMTO(String name, String version, boolean prerequisite, boolean dependency) {
		super();
		this.name = name;
		this.version = version;
		this.prerequisite = prerequisite;
		this.dependency = dependency;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public boolean isPrerequisite() {
		return prerequisite;
	}

	public boolean isDependency() {
		return dependency;
	}

	@Override
	public String toString() {
		return "DependencyMTO [name=" + name + ", version=" + version + ", prerequisite=" + prerequisite
				+ ", dependency=" + dependency + "]";
	}
	
}
