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

	private long originId;
	private int type;
	private BundleMTO bundleMTO;
	
	public BundleEventMTO(long originId, int type, BundleMTO bundleMTO) {
		this.originId = originId;
		this.type = type;
		this.bundleMTO = bundleMTO;
	}

	public long getBundleId() {
		return bundleMTO.getId();
	}

	public long getOriginId() {
		return originId;
	}

	public int getType() {
		return type;
	}
	
	public BundleMTO getBundleMTO() {
		return this.bundleMTO;
	}

	@Override
	public String toString() {
		return "BundleEventMTO [originId=" + originId + ", type=" + type + ", bundleMTO=" + bundleMTO + "]";
	}
}
