package org.eclipse.ecf.mgmt.kura.host;

import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate=true)
public class KuraServiceManager extends ServiceManager implements IServiceManager {

	@Activate
	protected void activate(BundleContext ctxt) throws Exception {
		super.activate(ctxt);
	}
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
	
}
