/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.install;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.VersionedId;

/**
 * Service interface for managing feature-based installs.
 * <p>
 * This service interface is suitable for usage as a remote service, as the
 * method parameters and return values are serializable.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 */
interface IFeatureInstallManagerAsync {

	CompletableFuture<IStatus> installFeatureAsync(VersionedId featureId,
			URI[] repoLocations, String profileId);

	CompletableFuture<IStatus> installFeatureAsync(VersionedId featureId,
			URI[] repoLocations);

	CompletableFuture<IStatus> installFeatureAsync(VersionedId featureId,
			String profileId);

	CompletableFuture<IStatus> installFeatureAsync(VersionedId featureId);

	CompletableFuture<IStatus> updateFeatureAsync(VersionedId featureId,
			URI[] repoLocations, String profileId);

	CompletableFuture<IStatus> updateFeatureAsync(VersionedId featureId,
			URI[] repoLocations);

	CompletableFuture<IStatus> updateFeatureAsync(VersionedId featureId,
			String profileId);

	CompletableFuture<IStatus> updateFeatureAsync(VersionedId featureId);

	CompletableFuture<IStatus> uninstallFeatureAsync(VersionedId featureId,
			URI[] repoLocations, String profileId);

	CompletableFuture<IStatus> uninstallFeatureAsync(VersionedId featureId,
			URI[] repoLocations);

	CompletableFuture<IStatus> uninstallFeatureAsync(VersionedId featureId,
			String profileId);

	CompletableFuture<IStatus> uninstallFeatureAsync(VersionedId featureId);

	CompletableFuture<IStatus> applyConfigurationAsync();

	CompletableFuture<VersionedId[]> getInstalledFeaturesAsync(String profileId);

	CompletableFuture<VersionedId[]> getInstalledFeaturesAsync();

	CompletableFuture<VersionedId[]> getInstallableFeaturesAsync(URI location);

	CompletableFuture<VersionedId[]> getInstallableFeaturesAsync();
}
