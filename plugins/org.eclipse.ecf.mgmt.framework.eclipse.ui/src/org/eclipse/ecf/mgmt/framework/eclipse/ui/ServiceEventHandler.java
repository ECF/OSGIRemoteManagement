package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.framework.IServiceEventHandler;
import org.eclipse.ecf.mgmt.framework.ServiceEventMTO;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public class ServiceEventHandler implements IServiceEventHandler {

	private static Map<IRemoteServiceID, IServiceEventHandlerDelegate> sehs = new HashMap<IRemoteServiceID, IServiceEventHandlerDelegate>();
	private ID containerID;
	private long rsid;

	public ServiceEventHandler(ImportReference importReference) {
		containerID = importReference.getContainerID();
		rsid = importReference.getRemoteServiceId();
	}

	public static void addDelegate(IRemoteServiceID rsID, IServiceEventHandlerDelegate delegate) {
		synchronized (sehs) {
			sehs.put(rsID, delegate);
		}
	}

	public static void removeDelegate(IRemoteServiceID rsID) {
		synchronized (sehs) {
			sehs.remove(rsID);
		}
	}

	public static void removeDelegates() {
		synchronized (sehs) {
			sehs.clear();
		}
	}

	@Override
	public void handleServiceEvent(ServiceEventMTO serviceEvent) {
		IRemoteServiceID rsID = null;
		IServiceEventHandlerDelegate seh = null;
		synchronized (sehs) {
			for (IRemoteServiceID rsid : sehs.keySet())
				if (rsid.getContainerID().equals(this.containerID) && rsid.getContainerRelativeID() == this.rsid) {
					rsID = rsid;
					seh = sehs.get(rsID);
					break;
				}
		}
		if (seh != null)
			seh.handleServiceEvent(rsID, serviceEvent);
		else
			System.out.println("RECEIVED serviceEvent=" + serviceEvent);
	}

}
