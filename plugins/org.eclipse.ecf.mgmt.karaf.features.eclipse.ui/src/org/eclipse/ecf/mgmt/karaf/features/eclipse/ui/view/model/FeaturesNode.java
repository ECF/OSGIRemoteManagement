package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import java.net.URI;

import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

public class FeaturesNode extends AbstractFeaturesNode {

	private final IRemoteServiceReference managerRef;
	private final FeatureInstallManagerAsync rsaManager;

	public FeaturesNode(IRemoteServiceReference managerRef, FeatureInstallManagerAsync rsaManager) {
		this.managerRef = managerRef;
		this.rsaManager = rsaManager;
	}

	public FeatureInstallManagerAsync getKarafFeaturesInstaller() {
		return this.rsaManager;
	}

	public IRemoteServiceReference getKarafFeaturesInstallerRef() {
		return this.managerRef;
	}

	public String getManagerContainer() {
		return this.managerRef.getID().getContainerID().getName();
	}

	public String getName() {
		return getManagerContainer() + ":" + this.managerRef.getID().getContainerRelativeID();
	}

	public RepositoryNode getRepositoryNode(URI repoURI) {
		for(AbstractFeaturesNode bn: getChildren()) {
			if (bn instanceof RepositoryNode) {
				RepositoryNode repoNode = (RepositoryNode) bn;
				if (repoNode.getUri().equals(repoURI))
					return repoNode;
			}
		}
		return null;
	}

	public FeatureNode getFeatureNode(String fId) {
		for(AbstractFeaturesNode bn: getChildren()) {
			if (bn instanceof RepositoryNode) {
				RepositoryNode repoNode = (RepositoryNode) bn;
				for(AbstractFeaturesNode rn: repoNode.getChildren()) {
					if (rn instanceof FeatureNode) {
						FeatureNode fn = (FeatureNode) rn;
						if (fn.getId().equals(fId))
							return fn;
					}
				}
			}
		}
		return null;
	}
}
