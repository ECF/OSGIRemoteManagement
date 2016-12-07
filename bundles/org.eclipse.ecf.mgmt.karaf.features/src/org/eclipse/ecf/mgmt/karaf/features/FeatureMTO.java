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

public class FeatureMTO implements Serializable {

	private static final long serialVersionUID = 7521766099987289587L;

	private String id;
	private String name;
	private String description;
	private String details;
	private boolean hasVersion;
	private boolean hidden;
	public FeatureMTO(String id, String name, String description, String details, boolean hasVersion, boolean hidden) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.details = details;
		this.hasVersion = hasVersion;
		this.hidden = hidden;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getDetails() {
		return details;
	}
	public boolean hasVersion() {
		return hasVersion;
	}
	public boolean isHidden() {
		return hidden;
	}
	@Override
	public String toString() {
		return "FeatureMTO [id=" + id + ", name=" + name + ", description=" + description + ", details=" + details
				+ ", hasVersion=" + hasVersion + ", hidden=" + hidden + "]";
	}
	
	
}
