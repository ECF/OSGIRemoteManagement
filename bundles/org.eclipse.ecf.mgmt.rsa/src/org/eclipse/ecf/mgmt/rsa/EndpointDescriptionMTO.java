/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.ecf.mgmt.PropertiesUtil;

public class EndpointDescriptionMTO implements Serializable {

	private static final long serialVersionUID = -7058001347405515626L;
	private final Map<String, Object> properties;

	@SuppressWarnings("unchecked")
	public EndpointDescriptionMTO(Map<String, ?> properties) {
		this.properties = PropertiesUtil
				.convertMapToSerializableMap(properties);
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	@Override
	public String toString() {
		return "EndpointDescriptionMTO [properties=" + properties + "]";
	}

}
