package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.karaf.features.FeatureEventMTO;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallEventHandler;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryEventMTO;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public class KarafFeaturesListener implements FeatureInstallEventHandler {

	private static Map<IRemoteServiceID, FeaturesInstallerHandler> behs = new HashMap<IRemoteServiceID, FeaturesInstallerHandler>();
	private ID containerID;
	private long rsid;

	public KarafFeaturesListener(ImportReference importReference) {
		containerID = importReference.getContainerID();
		rsid = importReference.getRemoteServiceId();
	}

	public static void addDelegate(IRemoteServiceID rsID, FeaturesInstallerHandler delegate) {
		synchronized (behs) {
			behs.put(rsID, delegate);
		}
	}

	public static void removeDelegate(IRemoteServiceID rsID) {
		synchronized (behs) {
			behs.remove(rsID);
		}
	}

	public static void removeDelegates() {
		synchronized (behs) {
			behs.clear();
		}
	}

	@Override
	public void handleFeatureEvent(FeatureEventMTO event) {
		IRemoteServiceID rsID = null;
		FeaturesInstallerHandler beh = null;
		synchronized (behs) {
			for (IRemoteServiceID rsid : behs.keySet())
				if (rsid.getContainerID().equals(this.containerID) && rsid.getContainerRelativeID() == this.rsid) {
					rsID = rsid;
					beh = behs.get(rsID);
					break;
				}
		}
		if (beh != null)
			beh.handleFeatureEvent(rsID, event);
		else
			System.out.println("RECEIVED featureEvent=" + event);
	}

	@Override
	public void handleRepoEvent(RepositoryEventMTO event) {
		IRemoteServiceID rsID = null;
		FeaturesInstallerHandler beh = null;
		synchronized (behs) {
			for (IRemoteServiceID rsid : behs.keySet())
				if (rsid.getContainerID().equals(this.containerID) && rsid.getContainerRelativeID() == this.rsid) {
					rsID = rsid;
					beh = behs.get(rsID);
					break;
				}
		}
		if (beh != null)
			beh.handleRepoEvent(rsID, event);
		else
			System.out.println("RECEIVED repoEvent=" + event);
	}

}
