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
import java.util.concurrent.CompletableFuture;

import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;

public interface IRemoteServiceAdminManagerAsync {

	CompletableFuture<RemoteServiceAdminEventMTO[]> getRemoteServiceAdminEventsAsync();

	CompletableFuture<RemoteServiceAdminEventMTO[]> getRemoteServiceAdminEventsAsync(int[] typeFilter);

	CompletableFuture<ExportReferenceMTO[]> getExportedServicesAsync();

	CompletableFuture<ImportReferenceMTO[]> getImportedEndpointsAsync();

	CompletableFuture<ExportRegistrationMTO[]> registerServiceAsync(ServiceReferenceMTO serviceReference,
			Map<String, ?> overridingProperties);

	CompletableFuture<EndpointDescriptionMTO> updateAsync(ExportReferenceMTO exportReference,
			Map<String, ?> properties);

	CompletableFuture<Void> closeAsync(ExportReferenceMTO exportReference);

	CompletableFuture<ImportRegistrationMTO> importServiceAsync(EndpointDescriptionMTO endpointDescription);

	CompletableFuture<Boolean> updateImportAsync(EndpointDescriptionMTO endpoint);

	CompletableFuture<Void> closeAsync(ImportReferenceMTO importReference);

}
