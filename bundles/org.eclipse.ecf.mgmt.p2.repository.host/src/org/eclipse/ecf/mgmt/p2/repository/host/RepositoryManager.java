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
	public URI[] getKnownMetadataRepositories(int flags) {
		return getMetadataRepositoryManager().getKnownRepositories(flags);
	}

	@Override
	public URI[] getKnownMetadataRepositories() {
		return getKnownMetadataRepositories(0);
	}

	@Override
	public URI[] getKnownArtifactRepositories(int flags) {
		return getArtifactRepositoryManager().getKnownRepositories(flags);
	}

	@Override
	public URI[] getKnownArtifactRepositories() {
		return getKnownArtifactRepositories(0);
	}

	@Override
	public IStatus addArtifactRepository(URI location, int flags) {
		try {
			getArtifactRepositoryManager()
					.loadRepository(location, flags, null);
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
	public IStatus addArtifactRepository(URI location) {
		return addArtifactRepository(location, 0);
	}

	@Override
	public IStatus addMetadataRepository(URI location, int flags) {
		try {
			getArtifactRepositoryManager()
					.loadRepository(location, flags, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}
		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			getArtifactRepositoryManager().createRepository(location,
					repositoryName,
					IMetadataRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Cannot add metadata repository", e);
		}
	}

	@Override
	public IStatus addMetadataRepository(URI location) {
		return addMetadataRepository(location, 0);
	}

	@Override
	public IStatus removeArtifactRepository(URI location) {
		getArtifactRepositoryManager().removeRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public IStatus removeMetadataRepository(URI location) {
		getArtifactRepositoryManager().removeRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public IStatus addRepository(URI location, int flags) {
		// add metadata repository
		IStatus metadataStatus = addMetadataRepository(location, flags);
		// If it failed, we're done
		if (!metadataStatus.isOK())
			return metadataStatus;
		// If everything's ok with metadata repo
		IStatus artifactStatus = addArtifactRepository(location, flags);
		if (artifactStatus.isOK())
			return new SerializableStatus(Status.OK_STATUS);
		return new SerializableStatus(new SerializableMultiStatus(getContext()
				.getBundle().getSymbolicName(), IStatus.ERROR, new IStatus[] {
				metadataStatus, artifactStatus }, "addRepository for location="
				+ location + " failed", null));
	}

	@Override
	public IStatus addRepository(URI location) {
		return addRepository(location, 0);
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

	protected RepositoryMTO[] getArtifactRepositories(URI location,
			int loadFlags) {
		IArtifactRepositoryManager manager = getArtifactRepositoryManager();
		if (manager == null)
			return null;
		List<URI> locations = Arrays
				.asList((location != null) ? new URI[] { location }
						: getKnownArtifactRepositories(0));
		List<RepositoryMTO> results = new ArrayList<RepositoryMTO>();
		for (URI l : locations) {
			IArtifactRepository repo;
			try {
				repo = manager.loadRepository(l, loadFlags, null);
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
	public RepositoryMTO[] getArtifactRepositories(int flags) {
		return getArtifactRepositories(null, flags);
	}

	@Override
	public RepositoryMTO[] getArtifactRepositories() {
		return getArtifactRepositories(0);
	}

	@Override
	public RepositoryMTO getArtifactRepository(URI location, int loadFlags) {
		RepositoryMTO[] results = getArtifactRepositories(location, loadFlags);
		return (results.length == 0) ? null : results[0];
	}

	@Override
	public RepositoryMTO getArtifactRepository(URI location) {
		return getArtifactRepository(location, 0);
	}

	protected RepositoryMTO[] getMetadataRepositories(URI location,
			int loadFlags) {
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return null;
		List<URI> locations = Arrays
				.asList((location != null) ? new URI[] { location }
						: getKnownMetadataRepositories(0));
		List<RepositoryMTO> results = new ArrayList<RepositoryMTO>();
		for (URI l : locations) {
			IMetadataRepository repo;
			try {
				repo = manager.loadRepository(l, loadFlags, null);
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
	public RepositoryMTO[] getMetadataRepositories(int flags) {
		return getMetadataRepositories(null, 0);
	}

	@Override
	public RepositoryMTO[] getMetadataRepositories() {
		return getMetadataRepositories(null, 0);
	}

	@Override
	public RepositoryMTO getMetadataRepository(URI location, int flags) {
		RepositoryMTO[] repos = getMetadataRepositories(location, flags);
		return repos.length == 0 ? null : repos[0];
	}

	@Override
	public RepositoryMTO getMetadataRepository(URI location) {
		return getMetadataRepository(location, 0);
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
