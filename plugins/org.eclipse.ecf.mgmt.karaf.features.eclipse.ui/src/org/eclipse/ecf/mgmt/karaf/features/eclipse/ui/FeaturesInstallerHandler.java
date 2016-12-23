package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.ecf.mgmt.karaf.features.FeatureEventMTO;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryEventMTO;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public interface FeaturesInstallerHandler {

	public void handleFeatureEvent(IRemoteServiceID rsID, FeatureEventMTO bundleEvent);
	
	public void handleRepoEvent(IRemoteServiceID rsID, RepositoryEventMTO repoEvent);
}
