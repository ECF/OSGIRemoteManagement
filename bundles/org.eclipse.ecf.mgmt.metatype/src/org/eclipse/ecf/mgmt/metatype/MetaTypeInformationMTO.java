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

public class MetaTypeInformationMTO implements Serializable {

	private static final long serialVersionUID = 7700548231132976358L;

	private final String[] pids;
	private final String[] factoryPids;
	private final long bundleId;
	
	public MetaTypeInformationMTO(String[] pids, String[] factoryPids, long bundleId) {
		this.pids = pids;
		this.factoryPids = factoryPids;
		this.bundleId = bundleId;
	}
	public String[] getFactoryPids() {
		return factoryPids;
	}
	public String[] getPids() {
		return pids;
	}
	@Override
	public String toString() {
		return "MetaTypeInformationMTO [pids=" + Arrays.toString(pids) + ", factoryPids=" + Arrays.toString(factoryPids)
				+ ", bundleId=" + bundleId + "]";
	}
}
