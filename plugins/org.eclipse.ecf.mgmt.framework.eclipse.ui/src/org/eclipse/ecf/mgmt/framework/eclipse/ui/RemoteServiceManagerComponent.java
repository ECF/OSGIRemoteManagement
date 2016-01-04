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
import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;

public class RemoteServiceManagerComponent extends RemoteServiceComponent {

	private static RemoteServiceManagerComponent instance;
	
	public static RemoteServiceManagerComponent getInstance() {
		return instance;
	}
	
	public RemoteServiceManagerComponent() {
		instance = this;
	}
	void bindServicesManagerAsync(IServiceManagerAsync sm) {
		addServiceHolder(IServiceManagerAsync.class, sm);
	}
	
	void unbindServicesManagerAsync(IServiceManagerAsync sm) {
		removeServiceHolder(IServiceManagerAsync.class, sm);
	}
}
