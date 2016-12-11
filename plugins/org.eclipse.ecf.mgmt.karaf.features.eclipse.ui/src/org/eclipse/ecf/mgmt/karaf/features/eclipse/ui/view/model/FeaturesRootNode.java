package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.mgmt.karaf.features.KarafFeaturesInstallerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

public class FeaturesRootNode extends AbstractFeaturesNode {

	private final String groupName;

	public FeaturesRootNode(String groupName) {
		super();
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	private Map<IRemoteServiceReference, FeaturesNode> managers = Collections
			.synchronizedMap(new HashMap<IRemoteServiceReference, FeaturesNode>());

	public FeaturesNode getServiceManagerNode(IRemoteServiceReference rsRef,
			KarafFeaturesInstallerAsync rsaManager) {
		synchronized (managers) {
			FeaturesNode managerNode = managers.get(rsRef);
			if (managerNode == null) {
				managerNode = new FeaturesNode(rsRef, rsaManager);
				managers.put(rsRef, managerNode);
				addChild(managerNode);
			}
			return managerNode;
		}
	}

	public void removeServiceManagerNode(IRemoteServiceReference rsRef) {
		synchronized (managers) {
			FeaturesNode managerNode = managers.remove(rsRef);
			if (managerNode != null)
				removeChild(managerNode);
		}
	}


}
