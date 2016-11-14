package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.framework.BundleEventMTO;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public class BundleEventHandler implements IBundleEventHandler {

	private static Map<IRemoteServiceID, IBundleEventHandlerDelegate> behs = new HashMap<IRemoteServiceID, IBundleEventHandlerDelegate>();
	private ID containerID;
	private long rsid;

	public BundleEventHandler(ImportReference importReference) {
		containerID = importReference.getContainerID();
		rsid = importReference.getRemoteServiceId();
	}

	public static void addDelegate(IRemoteServiceID rsID, IBundleEventHandlerDelegate delegate) {
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
	public void handleBundleEvent(BundleEventMTO bundleEvent) {
		IRemoteServiceID rsID = null;
		IBundleEventHandlerDelegate beh = null;
		synchronized (behs) {
			for (IRemoteServiceID rsid : behs.keySet())
				if (rsid.getContainerID().equals(this.containerID) && rsid.getContainerRelativeID() == this.rsid) {
					rsID = rsid;
					beh = behs.get(rsID);
					break;
				}
		}
		if (beh != null)
			beh.handleBundleEvent(rsID, bundleEvent);
		else
			System.out.println("RECEIVED bundleEvent=" + bundleEvent);
	}

}
