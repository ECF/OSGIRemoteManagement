/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

public interface IExportableServiceCallbackAssociator {

	Class<?> associateCallback(ServiceReference<?> exportableServiceReference, Class<?> callbackClass);
	Class<?> getAssociatedCallback(ServiceReference<?> exportableServiceReference);
	Class<?> unassociateCallback(ServiceReference<?> exportedServiceReference);

	RemoteServiceAdmin getRSA();
}
