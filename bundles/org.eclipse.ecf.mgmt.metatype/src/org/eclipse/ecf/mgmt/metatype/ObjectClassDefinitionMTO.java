/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.metatype;

import java.io.Serializable;
import java.util.Arrays;

public class ObjectClassDefinitionMTO implements Serializable {

	private static final long serialVersionUID = 661437832885503024L;

	private final String name;
	private final String id;
	private final String description;
	private final AttributeDefinitionMTO[] attributeDefinitions;
	
	public ObjectClassDefinitionMTO(String name, String id, String description, AttributeDefinitionMTO[] attributeDefinitions) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.attributeDefinitions = attributeDefinitions;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public AttributeDefinitionMTO[] getAttributeDefinitions() {
		return attributeDefinitions;
	}

	@Override
	public String toString() {
		return "ObjectClassDefinitionMTO [name=" + name + ", id=" + id + ", description=" + description
				+ ", attributeDefinitions=" + Arrays.toString(attributeDefinitions) + "]";
	}

}
