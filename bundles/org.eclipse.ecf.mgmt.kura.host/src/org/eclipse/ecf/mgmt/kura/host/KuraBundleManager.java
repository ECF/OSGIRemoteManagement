package org.eclipse.ecf.mgmt.kura.host;

import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.host.BundleManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class KuraBundleManager extends BundleManager implements IBundleManager {

	@Activate
	protected void activate(BundleContext ctxt) throws Exception {
		super.activate(ctxt);
	}
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
}
