package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.mgmt.karaf.features.FeatureMTO;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryMTO;
import org.eclipse.ui.views.properties.IPropertySource;

public class RepositoryNode extends AbstractFeaturesNode {

	private RepositoryMTO repositoryMTO;
	
	public URI getUri() {
		return repositoryMTO.getUri();
	}

	public String getName() {
		return repositoryMTO.getName();
	}
	
	public RepositoryNode(RepositoryMTO mto) {
		this.repositoryMTO = mto;
		FeatureMTO[] features = mto.getFeatures();
		for(int i=0; i < features.length; i++)
			addChild(new FeatureNode(features[i]));
	}

	private Map<String,String> getRepositoryProperties() {
		Map<String,String> result = new HashMap<String,String>();
		result.put("URI", getUri().toString());
		result.put("Name", getName());
		result.put("Repositories", Arrays.asList(repositoryMTO.getRepositories()).toString());
		result.put("Resource Repositories", Arrays.asList(repositoryMTO.getResourceRepositories()).toString());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class)
			return new FeaturePropertySource(getRepositoryProperties());
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
