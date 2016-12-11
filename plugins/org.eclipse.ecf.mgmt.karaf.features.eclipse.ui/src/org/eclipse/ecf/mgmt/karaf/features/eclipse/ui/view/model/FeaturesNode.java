package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.ecf.mgmt.karaf.features.KarafFeaturesInstallerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

public class FeaturesNode extends AbstractFeaturesNode {

	private final IRemoteServiceReference managerRef;
	private final KarafFeaturesInstallerAsync rsaManager;

	public FeaturesNode(IRemoteServiceReference managerRef, KarafFeaturesInstallerAsync rsaManager) {
		this.managerRef = managerRef;
		this.rsaManager = rsaManager;
	}

	public KarafFeaturesInstallerAsync getKarafFeaturesInstaller() {
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

}
