package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.ecf.mgmt.framework.BundleEventMTO;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.osgi.service.component.annotations.Component;

@Component(immediate=true)
public class BundleEventHandler implements IBundleEventHandler {

	@Override
	public void handleBundleEvent(BundleEventMTO bundleEvent) {
		System.out.println("RECEIVED bundleEvent=" + bundleEvent);
	}

}
