package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.framework.BundleEventMTO;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;

public class BundleEventHandler implements IBundleEventHandler {

	@Override
	public void handleBundleEvent(BundleEventMTO bundleEvent) {
		System.out.println("RECEIVED bundleEvent=" + bundleEvent);
	}

}
