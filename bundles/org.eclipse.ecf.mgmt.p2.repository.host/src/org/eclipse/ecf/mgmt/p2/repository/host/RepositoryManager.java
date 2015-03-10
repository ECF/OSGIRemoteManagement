package org.eclipse.ecf.mgmt.p2.repository.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableMultiStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.p2.CopyrightMTO;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;
import org.eclipse.ecf.mgmt.p2.LicenseMTO;
import org.eclipse.ecf.mgmt.p2.VersionedId;
import org.eclipse.ecf.mgmt.p2.repository.IRepositoryManager;
import org.eclipse.ecf.mgmt.p2.repository.RepositoryMTO;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.metadata.ICopyright;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

public class RepositoryManager extends AbstractManager implements
		IRepositoryManager {

	private IProvisioningAgent agent;

	protected void bindProvisioningAgent(IProvisioningAgent agent) {
		this.agent = agent;
	}

	protected void unbindProvisioningAgent(IProvisioningAgent agent) {
		this.agent = null;
	}

	protected IMetadataRepositoryManager getMetadataRepositoryManager() {
		return (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
	}

	protected IArtifactRepositoryManager getArtifactRepositoryManager() {
		return (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
	}

	@Override
	public URI[] getKnownMetadataRepositories(int flags) {
		IMetadataRepositoryManager mrm = getMetadataRepositoryManager();
		return (mrm != null) ? mrm.getKnownRepositories(flags) : null;
	}

	@Override
	public URI[] getKnownMetadataRepositories() {
		return getKnownMetadataRepositories(0);
	}

	@Override
	public URI[] getKnownArtifactRepositories(int flags) {
		IArtifactRepositoryManager arm = getArtifactRepositoryManager();
		return (arm != null) ? arm.getKnownRepositories(flags) : null;
	}

	@Override
	public URI[] getKnownArtifactRepositories() {
		return getKnownArtifactRepositories(0);
	}

	@Override
	public IStatus addArtifactRepository(URI location, int flags) {
		IArtifactRepositoryManager manager = getArtifactRepositoryManager();
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			manager.loadRepository(location, flags, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}

		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			manager.createRepository(location, repositoryName,
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
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return createErrorStatus("No metadata repository manager found");
		try {
			manager.loadRepository(location, flags, null);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			// fall through and create a new repository
		}

		// for convenience create and add a repository here
		String repositoryName = location + " - metadata";
		try {
			manager.createRepository(location, repositoryName,
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
		IArtifactRepositoryManager manager = getArtifactRepositoryManager();
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		manager.removeRepository(location);
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public IStatus removeMetadataRepository(URI location) {
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return createErrorStatus("No metadata repository manager found");
		manager.removeRepository(location);
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
		IArtifactRepositoryManager manager = getArtifactRepositoryManager();
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			manager.refreshRepository(location, null);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public IStatus refreshMetadataRepository(URI location) {
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return createErrorStatus("No artifact repository manager found");
		try {
			manager.refreshRepository(location, null);
		} catch (ProvisionException e) {
			return createErrorStatus("error refreshing repository location="
					+ location, e);
		}
		return new SerializableStatus(Status.OK_STATUS);
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
		IMetadataRepositoryManager manager = getMetadataRepositoryManager();
		if (manager == null)
			return null;
		@SuppressWarnings("rawtypes")
		IQueryable queryable = null;
		if (location == null) {
			queryable = manager;
		} else {
			try {
				queryable = manager.loadRepository(location, null);
			} catch (Exception e) {
				return null;
			}
		}
		if (queryable == null)
			return null;
		@SuppressWarnings("unchecked")
		IInstallableUnit[] units = (IInstallableUnit[]) queryable.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class);
		if (units == null)
			return null;
		List<InstallableUnitMTO> results = selectAndMap(Arrays.asList(units),
				null, un -> {
					return createInstallableUnitMTO(un);
				});
		return results.toArray(new InstallableUnitMTO[results.size()]);
	}

	private InstallableUnitMTO createInstallableUnitMTO(IInstallableUnit iu) {
		Version v = iu.getVersion();
		ICopyright copyRight = iu.getCopyright();
		Collection<ILicense> licenses = iu.getLicenses();
		return new InstallableUnitMTO(new VersionedId(iu.getId(),
				(v == null) ? null : v.toString()), iu.getProperties(),
				iu.isSingleton(), iu.isResolved(),
			    licenses==null?null:createLicenses(iu.getLicenses()),
				copyRight==null?null:new CopyrightMTO(copyRight.getLocation(), copyRight.getBody()));
	}

	private LicenseMTO[] createLicenses(Collection<ILicense> ls) {
		List<LicenseMTO> results = selectAndMap(
				new ArrayList<ILicense>(ls),
				null,
				l -> {
					return new LicenseMTO(l.getLocation(), l.getBody(), l
							.getUUID());
				});
		return results.toArray(new LicenseMTO[results.size()]);
	}

}
