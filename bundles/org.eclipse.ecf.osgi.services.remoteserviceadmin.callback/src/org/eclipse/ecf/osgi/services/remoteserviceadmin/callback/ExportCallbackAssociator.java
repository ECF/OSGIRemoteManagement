/*******************************************************************************
 * Copyright (c) 2018 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import org.osgi.framework.ServiceReference;

public interface ExportCallbackAssociator {

	ExportCallbackAssociation associateExportableWithCallback(ServiceReference<?> exportableServiceReference,
			Class<?> callbackServiceInterface);
}
