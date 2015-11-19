/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.internal.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManagerAsync;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.RemoteRSAView;

public class RSAManagerComponent {

	private List<IRemoteServiceAdminManagerAsync> rsaManagers = new ArrayList<IRemoteServiceAdminManagerAsync>();
	private RemoteRSAView rsaView;

	private static RSAManagerComponent instance;

	public RSAManagerComponent() {
		instance = this;
	}

	public static RSAManagerComponent getDefault() {
		return instance;
	}

	public synchronized void setRemoteRSAView(RemoteRSAView rsaView) {
		this.rsaView = rsaView;
		if (this.rsaView != null) {
			for (IRemoteServiceAdminManagerAsync rsaManager : rsaManagers)
				this.rsaView.addRSAManagerAsync(rsaManager);
		}
	}

	void bindRemoteServiceAdminManagerAsync(IRemoteServiceAdminManagerAsync rsaManagerAsync) {
		synchronized (this) {
			if (this.rsaView == null)
				rsaManagers.add(rsaManagerAsync);
			else
				this.rsaView.addRSAManagerAsync(rsaManagerAsync);
		}
	}

	void unbindRemoteServiceAdminManagerAsync(IRemoteServiceAdminManagerAsync rsaManagerAsync) {
		synchronized (this) {
			if (this.rsaView == null)
				rsaManagers.remove(rsaManagerAsync);
			else {
				rsaManagers.remove(rsaManagerAsync);
				this.rsaView.removeRSAManagerAsync(rsaManagerAsync);
			}
		}
	}
}
