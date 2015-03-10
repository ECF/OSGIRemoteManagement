package org.eclipse.ecf.mgmt.p2.profile.host;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;
import org.eclipse.ecf.mgmt.p2.profile.IProfileManager;
import org.eclipse.ecf.mgmt.p2.profile.ProfileMTO;
import org.eclipse.ecf.p2.host.AbstractP2Manager;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.osgi.service.environment.EnvironmentInfo;

public class ProfileManager extends AbstractP2Manager implements IProfileManager {

	private static final String P2_ENVIRONMENTS = "org.eclipse.equinox.p2.environments";
	protected EnvironmentInfo environmentInfo;

	protected void bindEnvironmentInfo(EnvironmentInfo ei) {
		this.environmentInfo = ei;
	}

	protected void unbindEnvironmentInfo(EnvironmentInfo ei) {
		this.environmentInfo = null;
	}

	protected IProfileRegistry getProfileRegistry() {
		return (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
	}

	@Override
	public IStatus addProfile(String profileId, Map<String, String> properties) {
		if (profileId == null)
			profileId = IProfileRegistry.SELF;
		if (properties == null)
			properties = new HashMap<String, String>();
		if (properties.get(P2_ENVIRONMENTS) == null)
			properties.put(P2_ENVIRONMENTS,
					"osgi.os=" + environmentInfo.getOS() + ",osgi.ws="
							+ environmentInfo.getWS() + ",osgi.arch="
							+ environmentInfo.getOSArch());
		try {
			getProfileRegistry().addProfile(profileId, properties);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (ProvisionException e) {
			return createErrorStatus("Could not add profile id=" + profileId, e);
		}
	}

	@Override
	public IStatus removeProfile(String profileId) {
		if (profileId == null)
			return createErrorStatus("Cannot remove self profile");
		getProfileRegistry().removeProfile(profileId);
		return new SerializableStatus(Status.OK_STATUS);
	}

	@Override
	public String[] getProfileIds() {
		List<String> results = selectAndMap(
				Arrays.asList(getProfileRegistry().getProfiles()), null, p -> {
					return p.getProfileId();
				});
		return results.toArray(new String[results.size()]);
	}

	protected ProfileMTO createProfileInfo(IProfile profile) {
		return new ProfileMTO(profile.getProfileId(), profile.getProperties(),
				profile.getTimestamp());
	}

	@Override
	public ProfileMTO getProfile(String profileId) {
		if (profileId == null)
			profileId = IProfileRegistry.SELF;
		IProfile profile = getProfileRegistry().getProfile(profileId);
		return profile == null ? null : createProfileInfo(profile);
	}

	@Override
	public ProfileMTO[] getProfiles() {
		List<ProfileMTO> results = selectAndMap(
				Arrays.asList(getProfileRegistry().getProfiles()), null, p -> {
					return createProfileInfo(p);
				});
		return results.toArray(new ProfileMTO[results.size()]);
	}

	@Override
	public InstallableUnitMTO[] getInstalledFeatures(String profileId) {
		if (profileId == null)
			profileId = IProfileRegistry.SELF;
		IProfile profile = getProfileRegistry().getProfile(profileId);
		if (profile == null)
			return null;
		return getInstallableUnitsMTO(profile.query(
				QueryUtil.createIUGroupQuery(), null).toArray(
				IInstallableUnit.class));
	}

}
