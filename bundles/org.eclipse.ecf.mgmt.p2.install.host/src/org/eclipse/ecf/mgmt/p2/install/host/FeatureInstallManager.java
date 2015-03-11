/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.install.host;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.p2.VersionedId;
import org.eclipse.ecf.mgmt.p2.host.AbstractP2Manager;
import org.eclipse.ecf.mgmt.p2.install.IFeatureInstallManager;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IPhaseSet;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.PhaseSetFactory;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.query.Collector;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

@SuppressWarnings("restriction")
public class FeatureInstallManager extends AbstractP2Manager implements
		IFeatureInstallManager {

	protected Configurator configurator;

	protected void bindConfigurator(Configurator configurator) {
		this.configurator = configurator;
	}

	protected void unbindConfigurator(Configurator configurator) {
		this.configurator = null;
	}

	protected IProgressMonitor getMonitorForInstall() {
		return new NullProgressMonitor();
	}

	protected IProgressMonitor getMonitorForUninstall() {
		return new NullProgressMonitor();
	}

	protected IProgressMonitor getMonitorForUpdate() {
		return new NullProgressMonitor();
	}

	@SuppressWarnings("unchecked")
	protected IQueryResult<IInstallableUnit>[] getInstallableUnits(
			IProvisioningAgent agent, URI[] locations,
			IQuery<IInstallableUnit> query, IProgressMonitor monitor)
			throws ProvisionException {
		IQueryable<IInstallableUnit>[] queryables = (IQueryable<IInstallableUnit>[]) ((locations == null) ? new IQueryable<?>[] { getMetadataRepositoryManager() }
				: getMetadataRepositories(agent, locations, monitor));
		if (queryables != null) {
			List<IQueryResult<IInstallableUnit>> queryResults = selectAndMap(
					Arrays.asList(queryables), null, qa -> {
						return qa.query(query, monitor);
					});
			return (IQueryResult<IInstallableUnit>[]) queryResults
					.toArray(new IQueryResult<?>[queryResults.size()]);
		}
		return (IQueryResult<IInstallableUnit>[]) new IQueryResult<?>[] { Collector
				.emptyCollector() };
	}

	protected IMetadataRepository getMetadataRepository(
			IProvisioningAgent agent, URI location, IProgressMonitor monitor)
			throws ProvisionException {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			throw new ProvisionException("No metadata repository manager found");
		return manager.loadRepository(location, monitor);
	}

	protected IMetadataRepository[] getMetadataRepositories(
			IProvisioningAgent agent, URI[] locations, IProgressMonitor monitor)
			throws ProvisionException {
		if (locations == null)
			return new IMetadataRepository[] { getMetadataRepository(agent,
					null, monitor) };
		else {
			List<IMetadataRepository> results = new ArrayList<IMetadataRepository>();
			for (int i = 0; i < locations.length; i++)
				results.add(getMetadataRepository(agent, locations[i], monitor));
			return results.toArray(new IMetadataRepository[] {});
		}
	}

	protected IStatus installOrUninstallIUs(
			Collection<IInstallableUnit> featuresToInstall,
			Collection<IInstallableUnit> featuresToUninstall, IProfile profile,
			IProgressMonitor monitor) {
		// Make sure we have planner
		IPlanner planner = (IPlanner) agent.getService(IPlanner.SERVICE_NAME);
		if (planner == null)
			return createErrorStatus("no planner available");
		// Make sure we have engine
		IEngine engine = (IEngine) agent.getService(IEngine.SERVICE_NAME);
		if (engine == null)
			return createErrorStatus("No engine available");
		// Create provisioning context
		ProvisioningContext provContext = createProvisioningContext(agent);
		// Create profile change request
		IProfileChangeRequest request = createProfileChangeRequest(planner,
				profile, featuresToInstall, featuresToUninstall);
		// Get provisioning plan
		IProvisioningPlan result = doPlan(planner, request, provContext,
				monitor);
		// check plan result
		IStatus planStatus = result.getStatus();
		if (!planStatus.isOK())
			return serializeStatus(planStatus);
		// Otherwise execute plan
		return serializeStatus(executePlan(result, engine, provContext, monitor));
	}

	protected IStatus executePlan(IProvisioningPlan plan, IEngine engine,
			ProvisioningContext context, IProgressMonitor progress) {
		return executePlan(plan, engine,
				PhaseSetFactory.createDefaultPhaseSet(), context, progress);
	}

	protected IStatus executePlan(IProvisioningPlan plan, IEngine engine,
			IPhaseSet phaseSet, ProvisioningContext context,
			IProgressMonitor progress) {
		IStatus planStatus = plan.getStatus();
		if (!planStatus.isOK())
			return planStatus;
		IProvisioningPlan newPlan = plan.getInstallerPlan();
		return engine.perform((newPlan != null) ? newPlan : plan, phaseSet,
				progress);
	}

	protected IProvisioningPlan doPlan(IPlanner planner,
			IProfileChangeRequest profileChangeRequest,
			ProvisioningContext provisioningContext, IProgressMonitor monitor) {
		return planner.getProvisioningPlan(profileChangeRequest,
				provisioningContext, monitor);
	}

	protected ProvisioningContext createProvisioningContext(
			IProvisioningAgent agent) {
		return new ProvisioningContext(agent);
	}

	protected IProfileChangeRequest createProfileChangeRequest(
			IPlanner planner, IProfile profile,
			Collection<IInstallableUnit> toInstall,
			Collection<IInstallableUnit> toUninstall) {
		IProfileChangeRequest request = planner.createChangeRequest(profile);
		if (toInstall != null)
			request.addAll(toInstall);
		if (toUninstall != null)
			request.removeAll(toUninstall);
		return request;
	}

	protected IInstallableUnit getFeatureForUpdate(
			Version currentFeatureVersion,
			Collection<IInstallableUnit> possibleFeaturesToInstall) {
		IInstallableUnit featureToUpdate = null;
		// Find iu to update
		for (Iterator<IInstallableUnit> i = possibleFeaturesToInstall
				.iterator(); i.hasNext();) {
			IInstallableUnit current = i.next();
			Version currentV = current.getVersion();
			if ((currentV.compareTo(currentFeatureVersion) > 0)
					&& (featureToUpdate == null || currentV
							.compareTo(featureToUpdate.getVersion()) > 0))
				featureToUpdate = current;
		}
		return featureToUpdate;
	}

	protected IQueryResult<IInstallableUnit> queryProfile(IProfile profile,
			IQuery<IInstallableUnit> query, IProgressMonitor monitor) {
		return profile.query(query, monitor);
	}

	@Override
	public IStatus installFeature(VersionedId featureId, URI[] repoLocations,
			String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("installFeature: featureid to install must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = getProfileRegistry().getProfile(profileId);
		if (profile == null)
			return createErrorStatus("installFeature: no profile matching profileId="
					+ profileId);
		IProgressMonitor monitor = getMonitorForInstall();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		// Query available/specified repository locations for given installable
		// unit id and unitVersion
		List<IInstallableUnit> featuresToInstall = new ArrayList<IInstallableUnit>();
		try {
			IQueryResult<IInstallableUnit>[] qresults = getInstallableUnits(
					agent, repoLocations, QueryUtil.createIUQuery(unitId,
							Version.create(unitVersion)), monitor);
			for (int i = 0; i < qresults.length; i++)
				for (Iterator<IInstallableUnit> it = qresults[i].iterator(); it
						.hasNext();)
					featuresToInstall.add(it.next());
		} catch (ProvisionException e) {
			// Could not load a metadata repository...report back in failed
			String message = "installFeature: could not load metadata repository.  FeatureId="
					+ featureId.getId() + ",v=" + featureId.getVersion();
			logError(message, e);
			return createErrorStatus(message, e);
		}

		if (featuresToInstall.isEmpty())
			return createErrorStatus("installFeature: feature="
					+ unitId
					+ ",v="
					+ unitVersion
					+ " not found in repositories.  Cannot continue with install.");

		return installOrUninstallIUs(featuresToInstall, null, profile, monitor);
	}

	@Override
	public IStatus installFeature(VersionedId featureId, URI[] repoLocations) {
		return installFeature(featureId, repoLocations, null);
	}

	@Override
	public IStatus installFeature(VersionedId featureId, String profileId) {
		return installFeature(featureId, null, profileId);
	}

	@Override
	public IStatus installFeature(VersionedId featureId) {
		return installFeature(featureId, null, null);
	}

	@Override
	public IStatus updateFeature(VersionedId featureId, URI[] repoLocations,
			String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("featureid to update must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get local profile registry...another sanity check
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no profile matching profileId="
					+ profileId);

		// For given profile, we should find some version to remove
		IProgressMonitor monitor = getMonitorForUpdate();
		IQueryResult<IInstallableUnit> installedFeatures = profile.query(
				QueryUtil.createIUQuery(featureId.getId()), monitor);
		// If no installed features for given featureId
		if (installedFeatures.isEmpty())
			return createErrorStatus("updateFeature: Feature="
					+ featureId.getId() + " not found installed in profile="
					+ profileId + " so it cannot be updated");

		// We have some installed features...so we select the one with the
		// highest version
		IInstallableUnit maxInstalledFeature = null;
		for (Iterator<IInstallableUnit> i = installedFeatures.iterator(); i
				.hasNext();) {
			IInstallableUnit current = i.next();
			if (maxInstalledFeature == null)
				maxInstalledFeature = current;
			else {
				Version currentV = current.getVersion();
				Version maxV = maxInstalledFeature.getVersion();
				int compareV = currentV.compareTo(maxV);
				if (compareV > 0)
					maxInstalledFeature = current;
			}
		}

		// Now have maxInstalledVersion
		org.eclipse.equinox.p2.metadata.VersionedId p2VersionedId = new org.eclipse.equinox.p2.metadata.VersionedId(
				featureId.getId(), featureId.getVersion());

		Version updateVersion = p2VersionedId.getVersion();

		Version maxInstalledFeatureVersion = maxInstalledFeature.getVersion();

		if (!updateVersion.equals(Version.emptyVersion)) {
			// If we're asking to update to a specific version, and we already
			// have a newer version installed, then
			// we have nothing to do
			if (maxInstalledFeatureVersion.compareTo(updateVersion) > 0) {
				return createErrorStatus("updateFeature: Nothing to update.  Feature="
						+ featureId.getId()
						+ ",v="
						+ featureId.getVersion()
						+ " is older than installed feature v="
						+ p2VersionedId.getVersion());
			}
		}

		// Now query metadata repositories for desired features
		List<IInstallableUnit> possibleFeaturesToInstall = new ArrayList<IInstallableUnit>();
		try {
			IQueryResult<IInstallableUnit>[] qresults = getInstallableUnits(
					agent,
					repoLocations,
					QueryUtil.createIUQuery(featureId.getId(),
							p2VersionedId.getVersion()), monitor);
			for (int i = 0; i < qresults.length; i++)
				for (Iterator<IInstallableUnit> it = qresults[i].iterator(); it
						.hasNext();)
					possibleFeaturesToInstall.add(it.next());
		} catch (ProvisionException e) {
			String message = "updateFeature: Could not load metadata repository.  FeatureId="
					+ featureId.getId() + ",v=" + featureId.getVersion();
			logError(message, e);
			return createErrorStatus(message, e);

		}
		IInstallableUnit featureToUpdate = getFeatureForUpdate(
				maxInstalledFeatureVersion, possibleFeaturesToInstall);
		if (featureToUpdate == null)
			return createErrorStatus("updateFeature: Nothing to update.  Could not find feature="
					+ featureId.getId()
					+ " with version="
					+ featureId.getVersion()
					+ " greater than in installed version in repositories");

		List<IInstallableUnit> fToUninstall = new ArrayList<IInstallableUnit>();
		fToUninstall.add(maxInstalledFeature);

		List<IInstallableUnit> fToInstall = new ArrayList<IInstallableUnit>();
		fToInstall.add(featureToUpdate);

		return installOrUninstallIUs(fToInstall, fToUninstall, profile, monitor);
	}

	@Override
	public IStatus updateFeature(VersionedId featureId, URI[] repoLocations) {
		return updateFeature(featureId, repoLocations, null);
	}

	@Override
	public IStatus updateFeature(VersionedId featureId, String profileId) {
		return updateFeature(featureId, null, profileId);
	}

	@Override
	public IStatus updateFeature(VersionedId featureId) {
		return updateFeature(featureId, null, null);
	}

	@Override
	public IStatus uninstallFeature(VersionedId featureId, URI[] repoLocations,
			String profileId) {
		// Parameter sanity checks
		if (featureId == null)
			return createErrorStatus("featureid to install must not be null");
		if (profileId == null || profileId.equals("this"))
			profileId = IProfileRegistry.SELF;
		// Must be able to get local profile registry...another sanity check
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return createErrorStatus("no profile registry available");
		// Must be able to get given profile, otherwise we're finished.
		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return createErrorStatus("no matching profileId=" + profileId);

		IProgressMonitor monitor = getMonitorForUninstall();
		String unitId = featureId.getId();
		String unitVersion = featureId.getVersion();
		// For given profile, we should find the specific version to remove
		IQueryResult<IInstallableUnit> units = queryProfile(profile,
				QueryUtil.createIUQuery(unitId, Version.create(unitVersion)),
				monitor);

		// If we didn't find it installed then we can't uninstall it
		if (units.isEmpty())
			return createErrorStatus("uninstallFeature: Feature=" + unitId
					+ " with v=" + unitVersion
					+ " not found installed, so cannot be uninstalled");

		return installOrUninstallIUs(null, units.toUnmodifiableSet(), profile,
				monitor);

	}

	@Override
	public IStatus uninstallFeature(VersionedId featureId, URI[] repoLocations) {
		return uninstallFeature(featureId, repoLocations, null);
	}

	@Override
	public IStatus uninstallFeature(VersionedId featureId, String profileId) {
		return uninstallFeature(featureId, null, profileId);
	}

	@Override
	public IStatus uninstallFeature(VersionedId featureId) {
		return uninstallFeature(featureId, null, null);
	}

	@Override
	public IStatus applyConfiguration() {
		try {
			configurator.applyConfiguration();
			return SerializableStatus.OK_STATUS;
		} catch (IOException e) {
			return createErrorStatus("Could not apply configuration", e);
		}
	}

	@Override
	public VersionedId[] getInstalledFeatures() {
		return getInstalledFeatures(null);
	}

	@Override
	public VersionedId[] getInstalledFeatures(String profileId) {
		IProfileRegistry profileRegistry = (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
		if (profileRegistry == null)
			return null;
		if (profileId == null)
			profileId = IProfileRegistry.SELF;

		IProfile profile = profileRegistry.getProfile(profileId);
		if (profile == null)
			return null;
		IInstallableUnit[] ius = profile.query(QueryUtil.createIUGroupQuery(),
				null).toArray(IInstallableUnit.class);
		return (ius == null) ? new VersionedId[0] : getVersionIds(ius);
	}

	protected VersionedId[] getVersionIds(IInstallableUnit[] ius) {
		List<VersionedId> results = selectAndMap(Arrays.asList(ius), null,
				iu -> {
					return new VersionedId(iu.getId(), iu.getVersion()
							.toString());
				});
		return results.toArray(new VersionedId[results.size()]);
	}

	@Override
	public VersionedId[] getInstallableFeatures(URI location) {
		IMetadataRepositoryManager manager = (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (manager == null)
			return null;
		IQueryable<IInstallableUnit> queryable = null;
		if (location == null)
			queryable = manager;
		else {
			try {
				queryable = manager.loadRepository(location, null);
			} catch (Exception e) {
				return null;
			}
		}
		if (queryable == null)
			return null;
		IInstallableUnit[] units = queryable.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class);
		return (units == null) ? new VersionedId[0] : getVersionIds(units);
	}

	@Override
	public VersionedId[] getInstallableFeatures() {
		return getInstallableFeatures(null);
	}

}
