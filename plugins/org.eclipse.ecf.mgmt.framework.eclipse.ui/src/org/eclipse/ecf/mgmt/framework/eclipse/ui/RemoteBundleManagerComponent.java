/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

public class RemoteBundleManagerComponent extends RemoteServiceComponent {

	private static RemoteBundleManagerComponent instance;

	public static RemoteBundleManagerComponent getInstance() {
		return instance;
	}

	public RemoteBundleManagerComponent() {
		instance = this;
	}

	void bindBundleManagerAsync(IBundleManagerAsync bm) {
		addServiceHolder(IBundleManagerAsync.class, bm);
	}

	void unbindBundleManagerAsync(IBundleManagerAsync bm) {
		removeServiceHolder(IBundleManagerAsync.class, bm);
	}
}
