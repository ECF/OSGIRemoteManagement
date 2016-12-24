package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.framework.ServiceEventMTO;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public interface IServiceEventHandlerDelegate {

	public void handleServiceEvent(IRemoteServiceID rsID, ServiceEventMTO serviceEvent);
}
