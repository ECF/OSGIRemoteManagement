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

public class AttributeDefinitionMTO implements Serializable {

	private static final long serialVersionUID = -1851433239067403070L;

	private final String name;
	private final String id;
	private final int cardinality;
	private final int type;
	private final String[] optionValues;
	private final String[] optionLabels;
	private final String[] defaultValues;
	
	public AttributeDefinitionMTO(String name, String id, int cardinality, int type, String[] optionValues,
			String[] optionLabels, String[] defaultValues) {
		super();
		this.name = name;
		this.id = id;
		this.cardinality = cardinality;
		this.type = type;
		this.optionValues = optionValues;
		this.optionLabels = optionLabels;
		this.defaultValues = defaultValues;
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

	public int getCardinality() {
		return cardinality;
	}

	public int getType() {
		return type;
	}

	public String[] getOptionValues() {
		return optionValues;
	}

	public String[] getOptionLabels() {
		return optionLabels;
	}

	public String[] getDefaultValues() {
		return defaultValues;
	}

	@Override
	public String toString() {
		return "AttributeDefinitionMTO [name=" + name + ", id=" + id + ", cardinality=" + cardinality + ", type=" + type
				+ ", optionValues=" + Arrays.toString(optionValues) + ", optionLabels=" + Arrays.toString(optionLabels)
				+ ", defaultValues=" + Arrays.toString(defaultValues) + "]";
	}
	
}
