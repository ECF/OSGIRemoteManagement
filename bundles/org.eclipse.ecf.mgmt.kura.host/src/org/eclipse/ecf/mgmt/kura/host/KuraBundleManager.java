package org.eclipse.ecf.mgmt.kura.host;

import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.host.BundleManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(service=org.eclipse.ecf.mgmt.framework.IBundleManager.class,properties="mqtt.rs.properties")
public class KuraBundleManager extends BundleManager implements IBundleManager {

	@Activate
	protected void activate(BundleContext context) throws Exception {
		super.activate(context);
	}
	
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
}
