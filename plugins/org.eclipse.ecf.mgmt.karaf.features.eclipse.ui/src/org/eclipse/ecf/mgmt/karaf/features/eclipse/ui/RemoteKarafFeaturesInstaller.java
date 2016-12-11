package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.karaf.features.KarafFeaturesInstallerAsync;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true)
public class RemoteKarafFeaturesInstaller extends RemoteServiceComponent {

	private IContainerManager containerManager;

	public RemoteKarafFeaturesInstaller() {
		super();
	}

	private static RemoteKarafFeaturesInstaller instance;

	public static RemoteKarafFeaturesInstaller getInstance() {
		return instance;
	}

	@Activate
	public void activate() throws Exception {
		super.activate();
		instance = this;
	}

	@Deactivate
	public void deactivate() {
		super.deactivate();
		instance = null;
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	void bindKarafFeaturesInstaller(KarafFeaturesInstallerAsync fi) {
		addServiceHolder(KarafFeaturesInstallerAsync.class, fi);
	}

	void unbindKarafFeaturesInstaller(KarafFeaturesInstallerAsync fi) {
		removeServiceHolder(KarafFeaturesInstallerAsync.class, fi);
	}

	@Reference
	void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}

	void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
	}

	public IContainer getContainerForID(ID containerID) {
		IContainerManager cm = this.containerManager;
		if (cm != null)
			for (IContainer c : this.containerManager.getAllContainers()) {
				ID targetID = c.getConnectedID();
				if (targetID != null && targetID.equals(containerID))
					return c;
			}
		return null;
	}
}
