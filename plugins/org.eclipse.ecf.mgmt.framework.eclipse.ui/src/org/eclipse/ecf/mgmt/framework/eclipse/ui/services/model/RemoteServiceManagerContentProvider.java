/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model;

import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesContentProvider;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesRootNode;
import org.eclipse.ui.IViewSite;

public class RemoteServiceManagerContentProvider extends ServicesContentProvider {

	private RemoteServiceManagerRootNode root;

	public RemoteServiceManagerContentProvider(IViewSite viewSite) {
		super(viewSite);
		this.root = new RemoteServiceManagerRootNode("");
	}
	
	@Override
	protected ServicesRootNode getInvisibleRoot() {
		return getServicesRoot();
	}
	
	public RemoteServiceManagerRootNode getServicesRoot() {
		return root;
	}

}
