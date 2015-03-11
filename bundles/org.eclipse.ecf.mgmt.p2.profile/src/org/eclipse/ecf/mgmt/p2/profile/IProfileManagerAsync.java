/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.profile;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;

/**
 * Profile manager service interface.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IProfileManagerAsync {

	CompletableFuture<IStatus> addProfileAsync(String profileId,
			Map<String, String> properties);

	CompletableFuture<IStatus> removeProfileAsync(String profileId);

	CompletableFuture<String[]> getProfileIdsAsync();

	CompletableFuture<ProfileMTO> getProfileAsync(String profileId);

	CompletableFuture<ProfileMTO[]> getProfilesAsync();

	CompletableFuture<InstallableUnitMTO[]> getInstalledFeaturesAsync(
			String profileId);
}
