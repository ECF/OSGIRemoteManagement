package org.eclipse.ecf.mgmt.p2.profile;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;

/**
 * Profile manager service interface.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IProfileManager {

	IStatus addProfile(String profileId, Map<String, String> properties);

	IStatus removeProfile(String profileId);

	String[] getProfileIds();

	ProfileMTO getProfile(String profileId);

	ProfileMTO[] getProfiles();

	InstallableUnitMTO[] getInstalledFeatures(String profileId);

}
