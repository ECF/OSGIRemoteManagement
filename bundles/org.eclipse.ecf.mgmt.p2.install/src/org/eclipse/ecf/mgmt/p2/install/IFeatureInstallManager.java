package org.eclipse.ecf.mgmt.p2.install;

import java.net.URI;

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
public interface IFeatureInstallManager {

	/**
	 * Install a given feature, via the given p2 repository locations, into the
	 * given profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @param profileId
	 *            the id of the profile to use to install the feature into. If
	 *            <code>null</code>, the feature will be installed into the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus installFeature(VersionedId featureId, URI[] repoLocations,
			String profileId);

	/**
	 * Install a given feature, via the given p2 repository locations, into the
	 * SELF profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus installFeature(VersionedId featureId, URI[] repoLocations);

	/**
	 * Install a given feature into the given profile. The feature specified
	 * must be present in a repository already known to the target system being
	 * managed, otherwise the install will fail.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param profileId
	 *            the id of the profile to use to install the feature into. If
	 *            <code>null</code>, the feature will be installed into the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus installFeature(VersionedId featureId, String profileId);

	/**
	 * Install a given feature into the SELF profile. The feature specified must
	 * be present in a repository already known to the target system being
	 * managed, otherwise the install will fail.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus installFeature(VersionedId featureId);

	/**
	 * Update a given feature, via the given p2 repository locations, into the
	 * given profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            <b>either</b> have a specific version (e.g.
	 *            1.0.0.201006281200), <b>OR</b> the version may be
	 *            <code>null</code>. If the version is <code>null</code>, then
	 *            the relevant repositories are consulted for available versions
	 *            of the given feature, and if more than one version is
	 *            available, the <b>highest</b> version available is selected.
	 *            The current version is then updated to the selected version.
	 *            Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @param profileId
	 *            the id of the profile to use for the feature update. If
	 *            <code>null</code>, the feature will be updated into the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus updateFeature(VersionedId featureId, URI[] repoLocations,
			String profileId);

	/**
	 * Update a given feature, via the given p2 repository locations, into the
	 * SELF profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            <b>either</b> have a specific version (e.g.
	 *            1.0.0.201006281200), <b>OR</b> the version may be
	 *            <code>null</code>. If the version is <code>null</code>, then
	 *            the relevant repositories are consulted for available versions
	 *            of the given feature, and if more than one version is
	 *            available, the <b>highest</b> version available is selected.
	 *            The current version is then updated to the selected version.
	 *            Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus updateFeature(VersionedId featureId, URI[] repoLocations);

	/**
	 * Update a given feature into the given profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            <b>either</b> have a specific version (e.g.
	 *            1.0.0.201006281200), <b>OR</b> the version may be
	 *            <code>null</code>. If the version is <code>null</code>, then
	 *            the relevant repositories are consulted for available versions
	 *            of the given feature, and if more than one version is
	 *            available, the <b>highest</b> version available is selected.
	 *            The current version is then updated to the selected version.
	 *            Must not be <code>null</code>.
	 * 
	 * @param profileId
	 *            the id of the profile to use for the feature update. If
	 *            <code>null</code>, the feature will be updated into the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus updateFeature(VersionedId featureId, String profileId);

	/**
	 * Update a given feature, via the given p2 repository locations, into the
	 * SELF profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to install. The featureId must
	 *            <b>either</b> have a specific version (e.g.
	 *            1.0.0.201006281200), <b>OR</b> the version may be
	 *            <code>null</code>. If the version is <code>null</code>, then
	 *            the relevant repositories are consulted for available versions
	 *            of the given feature, and if more than one version is
	 *            available, the <b>highest</b> version available is selected.
	 *            The current version is then updated to the selected version.
	 *            Must not be <code>null</code>.
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus updateFeature(VersionedId featureId);

	/**
	 * Uninstall/remove a given feature, via the given p2 repository locations,
	 * from the given profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to uninstall. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @param profileId
	 *            the id of the profile to use to uninstall the feature from. If
	 *            <code>null</code>, the feature will be uninstalled from the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus uninstallFeature(VersionedId featureId, URI[] repoLocations,
			String profileId);

	/**
	 * Uninstall/remove a given feature, via the given p2 repository locations,
	 * from the SELF profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to uninstall. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param repoLocations
	 *            repository locations to use for the given featureId. If
	 *            <code>null</code>, then any <b>existing</b> repositories will
	 *            be searched for the given feature (and version).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus uninstallFeature(VersionedId featureId, URI[] repoLocations);

	/**
	 * Uninstall/remove a given feature from the given profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to uninstall. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @param profileId
	 *            the id of the profile to use to uninstall the feature from. If
	 *            <code>null</code>, the feature will be uninstalled from the p2
	 *            <b>SELF</b> profile (i.e. the running runtime).
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus uninstallFeature(VersionedId featureId, String profileId);

	/**
	 * Uninstall/remove a given feature from the SELF profile.
	 * <p>
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @param featureId
	 *            the featureId of the feature to uninstall. The featureId must
	 *            have a specific version (e.g. 1.0.0.201006281200), rather than
	 *            an empty version (e.g. 0.0.0). Must not be <code>null</code>.
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus uninstallFeature(VersionedId featureId);

	/**
	 * Apply the SELF profile configuration to the running runtime. This will
	 * result in bundles that are uninstalled being stopped, and uninstalled, as
	 * well as new bundles being installed.
	 * 
	 * The implementation of this method will typically be relatively long
	 * running. Callers should expect to be blocked (if invoked via this service
	 * interface). See also the {@link IFeatureInstallManagerAsync} for
	 * asynchronous invocation.
	 * 
	 * @return IStatus indicating the status (and error information if not
	 *         completed successfully). Should not be <code>null</code>
	 */
	IStatus applyConfiguration();

	/**
	 * Get the feature ids (and versions) of the features that are currently
	 * installed for the given profile.
	 * 
	 * @param profileId
	 *            the id of the profile to get the installed features for. If
	 *            <code>null</code> the installed features for the p2 SELF
	 *            profile are consulted.
	 * 
	 * @return VersionedId[] the installed features. May be <code>null</code> if
	 *         some unexpected error occurs (e.g. the repository manager service
	 *         is not running on the target system being managed).
	 */
	VersionedId[] getInstalledFeatures(String profileId);

	/**
	 * Get the feature ids (and versions) of the features that are currently
	 * installed for the SELF profile.
	 * 
	 * @return VersionedId[] the installed features. May be <code>null</code> if
	 *         some unexpected error occurs (e.g. the repository manager service
	 *         is not running on the target system being managed).
	 */
	VersionedId[] getInstalledFeatures();

	/**
	 * Get the feature ids (and versions) of the features that are currently
	 * installable from the given metadata repository location.
	 * 
	 * @param location
	 *            the location of a repository to consult for installable
	 *            features.
	 * 
	 * @return VersionedId[] the installable features exposed at the repository
	 *         located at the given location. May be <code>null</code> if some
	 *         unexpected error occurs (e.g. the repository manager service is
	 *         not running on the target system being managed).
	 */
	VersionedId[] getInstallableFeatures(URI location);

	/**
	 * Get the feature ids (and versions) of the features that are currently
	 * installable from the metadata repositories currently known/loaded.
	 * 
	 * @return VersionedId[] the installable features exposed at the repository
	 *         located at the given location. May be <code>null</code> if some
	 *         unexpected error occurs (e.g. the repository manager service is
	 *         not running on the target system being managed).
	 */
	VersionedId[] getInstallableFeatures();
}
