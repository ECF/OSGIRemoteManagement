package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.framework.BundleEventMTO;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public interface IBundleEventHandlerDelegate {

	public void handleBundleEvent(IRemoteServiceID rsID, BundleEventMTO bundleEvent);
}
