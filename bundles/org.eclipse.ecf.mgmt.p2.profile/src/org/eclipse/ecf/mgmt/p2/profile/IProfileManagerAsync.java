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
