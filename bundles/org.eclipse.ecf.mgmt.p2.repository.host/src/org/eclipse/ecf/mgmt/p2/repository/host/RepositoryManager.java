/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableMultiStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;
import org.eclipse.ecf.mgmt.p2.host.AbstractP2Manager;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryManager;
import org.eclipse.ecf.mgmt.p2.repository.RepositoryMTO;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

public class RepositoryManager extends AbstractP2Manager implements
		IRepositoryManager {

	@Override
	public URI[] getKnownMetadataRepositories() {
		return getMetadataRepositoryManager().getKnownRepositories(
				IMetadataRepositoryManager.REPOSITORIES_ALL);
	}

	@Override
	public URI[] getKnownArtifactRepositories() {
		return getArtifactRepositoryManager().getKnownRepositories(
				IMetadataRepositoryManager.REPOSITORIES_ALL);
	}

	@Override
	public IStatus addArtifactRepository(URI location) {
		try {
			getArtifactRepositoryManager().loadRepository(location, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}

		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			getArtifactRepositoryManager().createRepository(location,
					repositoryName,
					IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Cannot add artifact repository", e);
		}
	}

	@Override
	public IStatus addMetadataRepository(URI location) {
		try {
			getMetadataRepositoryManager().loadRepository(location, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}
		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			getMetadataRepositoryManager().createRepository(location,
					repositoryName,
					IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Cannot add metadata repository", e);
		}
	}

	@Override
	public IStatus removeArtifactRepository(URI location) {
		boolean result = getArtifactRepositoryManager().removeRepository(
				location);
		return (result) ? SerializableStatus.OK_STATUS
				: createErrorStatus("Could not remove artifact repository location="
						+ location);
	}

	@Override
	public IStatus removeMetadataRepository(URI location) {
		boolean result = getArtifactRepositoryManager().removeRepository(
				location);
		return (result) ? SerializableStatus.OK_STATUS
				: createErrorStatus("Could not remove metadata repository location="
						+ location);
	}

	@Override
	public IStatus addRepository(URI location) {
		// add metadata repository
		IStatus metadataStatus = addMetadataRepository(location);
		// If it failed, we're done
		if (!metadataStatus.isOK())
			return metadataStatus;
		// If everything's ok with metadata repo
		IStatus artifactStatus = addArtifactRepository(location);
		if (artifactStatus.isOK())
			return new SerializableStatus(Status.OK_STATUS);
		return new SerializableStatus(new SerializableMultiStatus(getContext()
				.getBundle().getSymbolicName(), IStatus.ERROR, new IStatus[] {
				metadataStatus, artifactStatus }, "addRepository for location="
				+ location + " failed", null));
	}

	@Override
	public IStatus removeRepository(URI location) {
		// remove metadata repository
		removeMetadataRepository(location);
		// remove artifact repository
		removeArtifactRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public IStatus refreshArtifactRepository(URI location) {
		try {
			getArtifactRepositoryManager().refreshRepository(location, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
	}

	@Override
	public IStatus refreshMetadataRepository(URI location) {
		try {
			getMetadataRepositoryManager().refreshRepository(location, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
	}

	@Override
	public IStatus refreshRepository(URI location) {
		// refresh metadata repository
		IStatus metadataStatus = refreshMetadataRepository(location);
		// If it failed, we're done
		if (!metadataStatus.isOK())
			return metadataStatus;
		// If everything's ok with metadata repo
		IStatus artifactStatus = refreshArtifactRepository(location);
		if (artifactStatus.isOK())
			return new SerializableStatus(Status.OK_STATUS);
		return new SerializableStatus(new SerializableMultiStatus(getContext()
				.getBundle().getSymbolicName(), IStatus.ERROR, new IStatus[] {
				metadataStatus, artifactStatus },
				"refresh failed for location=" + location, null));
	}

	protected RepositoryMTO[] getArtifactRepositories(URI location) {
		IArtifactRepositoryManager manager = getArtifactRepositoryManager();
		if (manager == null)
			return null;
		List<URI> locations = Arrays
				.asList((location != null) ? new URI[] { location }
						: getKnownArtifactRepositories());
		List<RepositoryMTO> results = new ArrayList<RepositoryMTO>();
		for (URI l : locations) {
			IArtifactRepository repo;
			try {
				repo = manager.loadRepository(l, null);
				results.add(new RepositoryMTO(repo.getLocation(), repo
						.getName(), repo.getDescription(), repo.getType(), repo
						.getProvider(), repo.getVersion().toString(), repo
						.getProperties(), repo.isModifiable()));

			} catch (ProvisionException e) {
				logError("Could not load artifact repository=" + location
						+ "...skipping", e);
			}
		}
		return results.toArray(new RepositoryMTO[results.size()]);
	}

	@Override
	public RepositoryMTO[] getArtifactRepositories() {
		return getArtifactRepositories(null);
	}

	@Override
	public RepositoryMTO getArtifactRepository(URI location) {
		RepositoryMTO[] results = getArtifactRepositories(location);
		return (results.length == 0) ? null : results[0];
	}

	protected RepositoryMTO[] getMetadataRepositories(URI location) {
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return null;
		List<URI> locations = Arrays
				.asList((location != null) ? new URI[] { location }
						: getKnownMetadataRepositories());
		List<RepositoryMTO> results = new ArrayList<RepositoryMTO>();
		for (URI l : locations) {
			IMetadataRepository repo;
			try {
				repo = manager.loadRepository(l, null);
				results.add(new RepositoryMTO(repo.getLocation(), repo
						.getName(), repo.getDescription(), repo.getType(), repo
						.getProvider(), repo.getVersion().toString(), repo
						.getProperties(), repo.isModifiable()));

			} catch (ProvisionException e) {
				logError("Could not load metadata repository=" + location
						+ "...skipping", e);
			}
		}
		return results.toArray(new RepositoryMTO[results.size()]);
	}

	@Override
	public RepositoryMTO[] getMetadataRepositories() {
		return getMetadataRepositories(null);
	}

	@Override
	public RepositoryMTO getMetadataRepository(URI location) {
		RepositoryMTO[] repos = getMetadataRepositories(location);
		return repos.length == 0 ? null : repos[0];
	}

	@Override
	public InstallableUnitMTO[] getInstallableFeatures(URI location) {
		IQueryable<IInstallableUnit> queryable = null;
		if (location == null) {
			queryable = getMetadataRepositoryManager();
		} else
			try {
				queryable = getMetadataRepositoryManager().loadRepository(
						location, null);
			} catch (Exception e) {
				return null;
			}
		if (queryable == null)
			return null;
		return getInstallableUnitsMTO((IInstallableUnit[]) queryable.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class));
	}

}
