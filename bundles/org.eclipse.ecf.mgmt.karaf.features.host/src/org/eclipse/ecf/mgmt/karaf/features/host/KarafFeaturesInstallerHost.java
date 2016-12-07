package org.eclipse.ecf.mgmt.karaf.features.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeatureState;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.FeaturesService.Option;
import org.apache.karaf.features.Repository;
import org.eclipse.ecf.mgmt.SerializationUtil;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.karaf.features.FeatureMTO;
import org.eclipse.ecf.mgmt.karaf.features.KarafFeaturesInstaller;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryMTO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component()
public class KarafFeaturesInstallerHost extends AbstractManager implements KarafFeaturesInstaller {

	private FeaturesService featuresService;

	@Reference
	protected void bindFeaturesService(FeaturesService featuresService) {
		this.featuresService = featuresService;
	}

	protected void unbindFeaturesService(FeaturesService featuresService) {
		this.featuresService = null;
	}

	private Exception getSerializableException(Throwable t) {
		Throwable e = SerializationUtil.checkForSerializable(t);
		if (e instanceof Exception)
			return (Exception) e;
		else
			return new Exception(t);
	}

	@Override
	public void validateRepository(URI uri) throws Exception {
		try {
			this.featuresService.validateRepository(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void addRepository(URI uri) throws Exception {
		try {
			this.featuresService.addRepository(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void addRepository(URI uri, boolean install) throws Exception {
		try {
			this.featuresService.addRepository(uri, install);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void removeRepository(URI uri) throws Exception {
		try {
			this.featuresService.removeRepository(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void removeRepository(URI uri, boolean uninstall) throws Exception {
		try {
			this.featuresService.removeRepository(uri, uninstall);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void restoreRepository(URI uri) throws Exception {
		try {
			this.featuresService.restoreRepository(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	protected FeatureMTO[] createFeatures(Feature[] features) {
		List<FeatureMTO> result = selectAndMap(Arrays.asList(features), null, f -> {
			return new FeatureMTO(f.getId(), f.getName(), f.getDescription(), f.getDetails(), f.hasVersion(),
					f.isHidden());
		});
		return result.toArray(new FeatureMTO[result.size()]);
	}

	protected RepositoryMTO[] createRepositories(Repository[] repositories) {
		List<RepositoryMTO> result = new ArrayList<RepositoryMTO>();
		for (Repository r : repositories) {
			try {
				result.add(new RepositoryMTO(r.getName(), r.getURI(), r.getRepositories(), r.getResourceRepositories(),
						createFeatures(r.getFeatures())));
			} catch (Exception e) {
				logError("Exception creating RepositoryMTO for repository r=r", e);
			}
		}
		return result.toArray(new RepositoryMTO[result.size()]);
	}

	protected RepositoryMTO createRepository(Repository repo) {
		return createRepositories(new Repository[] { repo })[0];
	}

	@Override
	public RepositoryMTO[] listRequiredRepositories() throws Exception {
		try {
		return createRepositories(this.featuresService.listRequiredRepositories());
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public RepositoryMTO[] listRepositories() throws Exception {
		try {
		return createRepositories(this.featuresService.listRepositories());
		} catch (Exception e) {
			throw getSerializableException(e);
		}
}

	@Override
	public RepositoryMTO getRepository(String repoName) throws Exception {
		try {
		return createRepository(this.featuresService.getRepository(repoName));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public RepositoryMTO getRepository(URI uri) throws Exception {
		try {
		return createRepository(this.featuresService.getRepository(uri));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public String getRepositoryName(URI uri) throws Exception {
		try {
		return this.featuresService.getRepositoryName(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void setResolutionOutputFile(String outputFile) {
		this.featuresService.setResolutionOutputFile(outputFile);
	}

	@Override
	public void installFeature(String name) throws Exception {
		try {
		this.featuresService.installFeature(name);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	private EnumSet<Option> createOptions(Set<Integer> options) {
		Set<Option> result = new HashSet<Option>();
		for(Integer i: options) 
			switch (i) {
			case Option_NoAutoManageBundles:
				result.add(Option.NoAutoManageBundles);
				break;
			case Option_NoAutoRefreshBundles:
				result.add(Option.NoAutoRefreshBundles);
				break;
			case Option_NoAutoRefreshManagedBundles:
				result.add(Option.NoAutoRefreshManagedBundles);
				break;
			case Option_NoAutoRefreshUnmanagedBundles:
				result.add(Option.NoAutoRefreshUnmanagedBundles);
				break;
			case Option_NoAutoStartBundles:
				result.add(Option.NoAutoStartBundles);
				break;
			case Option_NoFailOnFeatureNotFound:
				result.add(Option.NoFailOnFeatureNotFound);
				break;
			}
		return EnumSet.copyOf(result);
	}
	
	@Override
	public void installFeature(String name, Set<Integer> options) throws Exception {
		try {
			this.featuresService.installFeature(name, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void installFeature(String name, String version) throws Exception {
		try {
			this.featuresService.installFeature(name, version);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void installFeature(String name, String version, Set<Integer> options) throws Exception {
		try {
			this.featuresService.installFeature(name, version, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void installFeatures(Set<String> features, Set<Integer> options) throws Exception {
		try {
			this.featuresService.installFeatures(features, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void installFeatures(Set<String> features, String region, Set<Integer> options) throws Exception {
		try {
			this.featuresService.installFeatures(features, region, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void addRequirements(Map<String, Set<String>> requirements, Set<Integer> options) throws Exception {
		try {
			this.featuresService.addRequirements(requirements, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeature(String name, Set<Integer> options) throws Exception {
		try {
			this.featuresService.uninstallFeature(name, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeature(String name) throws Exception {
		try {
			this.featuresService.uninstallFeature(name);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeature(String name, String version, Set<Integer> options) throws Exception {
		try {
			this.featuresService.uninstallFeature(name,version,createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeature(String name, String version) throws Exception {
		try {
			this.featuresService.uninstallFeature(name,version);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeatures(Set<String> features, Set<Integer> options) throws Exception {
		try {
			this.featuresService.uninstallFeatures(features,createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void uninstallFeatures(Set<String> features, String region, Set<Integer> options) throws Exception {
		try {
			this.featuresService.uninstallFeatures(features,region,createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void removeRequirements(Map<String, Set<String>> requirements, Set<Integer> options) throws Exception {
		try {
			this.featuresService.removeRequirements(requirements, createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	private FeatureState getFeatureState(Integer i) {
		switch (i) {
		case FeatureState_Installed:
			return FeatureState.Installed;
		case FeatureState_Resolved:
			return FeatureState.Resolved;
		case FeatureState_Started:
			return FeatureState.Started;
		case FeatureState_Uninstalled:
			return FeatureState.Uninstalled;
		default:
			return FeatureState.Installed;
		}
	}
	private Map<String, Map<String, FeatureState>> getStateChanges(Map<String, Map<String, Integer>> stateChanges) {
		Map<String, Map<String, FeatureState>> result = new HashMap<String,Map<String,FeatureState>>();
		for(String key: stateChanges.keySet()) {
			Map<String, Integer> fs = stateChanges.get(key);
			Map<String, FeatureState> newfs = new HashMap<String,FeatureState>();
			for(String fskey: fs.keySet()) 
				newfs.put(fskey, getFeatureState(fs.get(fskey)));
			result.put(key, newfs);
		}
		return result;
	}
	@Override
	public void updateFeaturesState(Map<String, Map<String, Integer>> stateChanges, Set<Integer> options)
			throws Exception {
		try {
			this.featuresService.updateFeaturesState(getStateChanges(stateChanges), createOptions(options));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO[] listFeatures() throws Exception {
		try {
			return createFeatures(this.featuresService.listFeatures());
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO[] listRequiredFeatures() throws Exception {
		try {
			return createFeatures(this.featuresService.listRequiredFeatures());
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO[] listInstalledFeatures() throws Exception {
		try {
			return createFeatures(this.featuresService.listInstalledFeatures());
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public Map<String, Set<String>> listRequirements() {
		return this.featuresService.listRequirements();
	}

	@Override
	public boolean isRequired(FeatureMTO f) {
		try {
			return this.featuresService.isRequired(this.featuresService.getFeature(f.getId()));
		} catch (Exception e) {
			logError("Exception getting isRequired feature for featureMTO="+f,e);
			return false;
		}
	}

	@Override
	public boolean isInstalled(FeatureMTO f) {
		try {
			return this.featuresService.isInstalled(this.featuresService.getFeature(f.getId()));
		} catch (Exception e) {
			logError("Exception getting isInstalled feature for featureMTO="+f,e);
			return false;
		}
	}

	@Override
	public FeatureMTO getFeature(String name, String version) throws Exception {
		try {
			return createFeatures(new Feature[] { this.featuresService.getFeature(name, version)})[0];
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO getFeature(String name) throws Exception {
		try {
			return createFeatures(new Feature[] { this.featuresService.getFeature(name)})[0];
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO[] getFeatures(String name, String version) throws Exception {
		try {
			return createFeatures(this.featuresService.getFeatures(name, version));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public FeatureMTO[] getFeatures(String name) throws Exception {
		try {
			return createFeatures(this.featuresService.getFeatures(name));
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public void refreshRepository(URI uri) throws Exception {
		try {
			this.featuresService.refreshRepository(uri);
		} catch (Exception e) {
			throw getSerializableException(e);
		}
	}

	@Override
	public URI getRepositoryUriFor(String name, String version) {
		return this.featuresService.getRepositoryUriFor(name, version);
	}

	@Override
	public String[] getRepositoryNames() {
		return this.featuresService.getRepositoryNames();
	}

	@Override
	public int getState(String featureId) {
		FeatureState fs = this.featuresService.getState(featureId);
		if (fs.equals(FeatureState.Installed))
			return FeatureState_Installed;
		else if (fs.equals(FeatureState.Resolved))
			return FeatureState_Resolved;
		else if (fs.equals(FeatureState.Started)) 
			return FeatureState_Started;
		else 
			return FeatureState_Uninstalled;
	}

}