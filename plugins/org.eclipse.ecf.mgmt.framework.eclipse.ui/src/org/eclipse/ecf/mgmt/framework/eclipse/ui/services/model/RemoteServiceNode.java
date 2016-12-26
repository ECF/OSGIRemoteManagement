/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import java.util.Map;

import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;

public class RemoteServiceNode extends ServiceNode {

	private final int exportImportState;
	
	public RemoteServiceNode(long bundleId, long[] usingBundles, Map<String, Object> props, int exportImportState) {
		super(bundleId, usingBundles, props);
		this.exportImportState = exportImportState;
	}

	@Override
	public int getExportedImportedState() {
		return exportImportState;
	}
}
