/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.internal.serviceview;

import org.eclipse.ecf.remoteservice.ui.serviceview.ServicesView;
import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class DiscoveryComponent implements RemoteServiceAdminListener {

	private RemoteServiceAdmin rsa;

	private static DiscoveryComponent instance;

	private ServicesView servicesView;

	public void setServicesView(ServicesView servicesView) {
		this.servicesView = servicesView;
	}

	public DiscoveryComponent() {
		instance = this;
	}

	public static DiscoveryComponent getDefault() {
		return instance;
	}

	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = rsa;
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = null;
	}

	private BundleContext context;

	public void activate(BundleContext ctxt) {
		this.context = ctxt;
	}

	public void deactivate() {
		this.context = null;
	}

	public BundleContext getContext() {
		return context;
	}

	public RemoteServiceAdmin getRSA() {
		return this.rsa;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		if (servicesView != null)
			servicesView.handleRSAEvent(event);
	}
}
