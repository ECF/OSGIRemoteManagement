/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;

public class BundleEventMTO implements Serializable {

	private static final long serialVersionUID = -8761167381182242979L;

	private long bundleId;
	private long originId;
	private int type;
	
	public BundleEventMTO(long bundleId, long originId, int type) {
		this.bundleId = bundleId;
		this.originId = originId;
		this.type = type;
	}

	public BundleEventMTO(long bundleId, int type) {
		this.bundleId = bundleId;
		this.originId = bundleId;
		this.type = type;
	}

	public long getBundleId() {
		return bundleId;
	}

	public long getOriginId() {
		return originId;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return "BundleEventMTO [bundleId=" + bundleId + ", originId=" + originId + ", type=" + type + "]";
	}

}
