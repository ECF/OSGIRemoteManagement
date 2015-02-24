/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa;

import java.util.Map;

import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;

public interface IRemoteServiceAdminManager {

	RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents(int[] typeFilter);

	ExportReferenceMTO[] getExportedServices();

	ImportReferenceMTO[] getImportedEndpoints();

	ExportRegistrationMTO registerService(ServiceReferenceMTO serviceReference, Map<String, ?> overridingProperties);

	EndpointDescriptionMTO update(ExportRegistrationMTO exportRegistration, Map<String, ?> properties);

	void close(ExportRegistrationMTO exportRegistration);

	ImportRegistrationMTO importService(EndpointDescriptionMTO endpointDescription);

	boolean updateImport(EndpointDescriptionMTO endpoint);

	void close(ImportRegistrationMTO importRegistration);

}
