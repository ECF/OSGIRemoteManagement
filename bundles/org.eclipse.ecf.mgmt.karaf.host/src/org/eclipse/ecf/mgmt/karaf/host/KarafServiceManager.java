/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.host;

import org.eclipse.ecf.mgmt.framework.IServiceEventHandlerAsync;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class KarafServiceManager extends ServiceManager implements IServiceManager, RemoteServiceAdminListener  {

	@Activate
	protected void activate(BundleContext ctxt) throws Exception {
		super.activate(ctxt);
	}
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,policy=ReferencePolicy.DYNAMIC)
	void bindServiceEventHandler(IServiceEventHandlerAsync seh) {
		super.addServiceEventHandler(seh);
	}

	void unbindServiceEventHandler(IServiceEventHandlerAsync beh) {
		super.removeServiceEventHandler(beh);
	}

	@Reference
	protected void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.bindRemoteServiceAdmin(rsa);
	}
	
	protected void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.unbindRemoteServiceAdmin(rsa);
	}
}
