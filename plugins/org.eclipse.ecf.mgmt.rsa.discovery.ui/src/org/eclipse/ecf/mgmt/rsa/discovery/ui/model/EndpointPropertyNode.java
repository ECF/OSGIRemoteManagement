package org.eclipse.ecf.mgmt.rsa.discovery.ui.model;

/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
public class EndpointPropertyNode extends AbstractEndpointNode {

	private final String propertyName;
	private String propertyAlias;
	private String nameValueSeparator = ": ";

	public EndpointPropertyNode(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyAlias() {
		return this.propertyAlias;
	}

	public void setPropertyAlias(String propertyAlias) {
		this.propertyAlias = propertyAlias;
	}

	public String getNameValueSeparator() {
		return nameValueSeparator;
	}

	public void setNameValueSeparator(String nameValueSeparator) {
		this.nameValueSeparator = nameValueSeparator;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		return getEndpointDescriptionProperties().get(propertyName);
	}

}
