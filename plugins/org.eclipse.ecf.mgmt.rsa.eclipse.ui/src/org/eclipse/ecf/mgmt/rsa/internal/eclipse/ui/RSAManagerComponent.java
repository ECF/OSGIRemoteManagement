/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.internal.eclipse.ui;

import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManagerAsync;
import org.eclipse.ecf.remote.mgmt.util.RemoteServicesComponent;

public class RSAManagerComponent extends RemoteServicesComponent {

	private static RSAManagerComponent instance;
	
	public RSAManagerComponent() {
		instance = this;
	}
	
	public static RSAManagerComponent getInstance() {
		return instance;
	}
	
	void bindRemoteServiceAdminManagerAsync(IRemoteServiceAdminManagerAsync rsaManagerAsync) {
		addServiceHolder(IRemoteServiceAdminManagerAsync.class, rsaManagerAsync);
	}

	void unbindRemoteServiceAdminManagerAsync(IRemoteServiceAdminManagerAsync rsaManagerAsync) {
		removeServiceHolder(IRemoteServiceAdminManagerAsync.class, rsaManagerAsync);
	}
}
