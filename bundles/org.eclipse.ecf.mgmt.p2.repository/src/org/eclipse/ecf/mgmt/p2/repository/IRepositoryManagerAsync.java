package org.eclipse.ecf.mgmt.p2.repository;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;

/**
 * Repository manager service interface.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IRepositoryManagerAsync {

	CompletableFuture<URI[]> getKnownMetadataRepositoriesAsyncAsync(
			Integer flags);

	CompletableFuture<URI[]> getKnownMetadataRepositoriesAsync();

	CompletableFuture<URI[]> getKnownArtifactRepositoriesAsync(Integer flags);

	CompletableFuture<URI[]> getKnownArtifactRepositoriesAsync();

	CompletableFuture<IStatus> addArtifactRepositoryAsync(URI location,
			Integer flags);

	CompletableFuture<IStatus> addArtifactRepositoryAsync(URI location);

	CompletableFuture<IStatus> addMetadataRepositoryAsync(URI location,
			Integer flags);

	CompletableFuture<IStatus> addMetadataRepositoryAsync(URI location);

	CompletableFuture<IStatus> removeArtifactRepositoryAsync(URI location);

	CompletableFuture<IStatus> removeMetadataRepositoryAsync(URI location);

	CompletableFuture<IStatus> addRepositoryAsync(URI location, Integer flags);

	CompletableFuture<IStatus> addRepositoryAsync(URI location);

	CompletableFuture<IStatus> removeRepositoryAsync(URI location);

	CompletableFuture<IStatus> refreshArtifactRepositoryAsync(URI location);

	CompletableFuture<IStatus> refreshMetadataRepositoryAsync(URI location);

	CompletableFuture<IStatus> refreshRepositoryAsync(URI location);

	CompletableFuture<RepositoryMTO[]> getArtifactRepositoryAsync(Integer flags);

	CompletableFuture<RepositoryMTO[]> getArtifactRepositoryAsync();

	CompletableFuture<RepositoryMTO> getArtifactRepositoryAsync(URI location,
			Integer flags);

	CompletableFuture<RepositoryMTO> getArtifactRepositoryAsync(URI location);

	CompletableFuture<RepositoryMTO[]> getMetadataRepositoryAsync(Integer flags);

	CompletableFuture<RepositoryMTO[]> getMetadataRepositoryAsync();

	CompletableFuture<RepositoryMTO> getMetadataRepositoryAsync(URI location,
			Integer flags);

	CompletableFuture<RepositoryMTO> getMetadataRepositoryAsync(URI location);

	CompletableFuture<InstallableUnitMTO[]> getInstallableFeaturesAsync(
			URI location);
}
