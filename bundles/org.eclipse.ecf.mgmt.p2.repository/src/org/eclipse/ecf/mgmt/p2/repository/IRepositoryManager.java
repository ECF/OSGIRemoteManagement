package org.eclipse.ecf.mgmt.p2.repository;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;

/**
 * Repository manager service interface.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IRepositoryManager {

	URI[] getKnownMetadataRepositories(Integer flags);

	URI[] getKnownMetadataRepositories();

	URI[] getKnownArtifactRepositories(Integer flags);

	URI[] getKnownArtifactRepositories();

	IStatus addArtifactRepository(URI location, Integer flags);

	IStatus addArtifactRepository(URI location);

	IStatus addMetadataRepository(URI location, Integer flags);

	IStatus addMetadataRepository(URI location);

	IStatus removeArtifactRepository(URI location);

	IStatus removeMetadataRepository(URI location);

	IStatus addRepository(URI location, Integer flags);

	IStatus addRepository(URI location);

	IStatus removeRepository(URI location);

	IStatus refreshArtifactRepository(URI location);

	IStatus refreshMetadataRepository(URI location);

	IStatus refreshRepository(URI location);

	RepositoryMTO[] getArtifactRepository(Integer flags);

	RepositoryMTO[] getArtifactRepository();

	RepositoryMTO getArtifactRepository(URI location, Integer flags);

	RepositoryMTO getArtifactRepository(URI location);

	RepositoryMTO[] getMetadataRepository(Integer flags);

	RepositoryMTO[] getMetadataRepository();

	RepositoryMTO getMetadataRepository(URI location, Integer flags);

	RepositoryMTO getMetadataRepository(URI location);

	InstallableUnitMTO[] getInstallableFeatures(URI location);
}
