package org.eclipse.ecf.mgmt.kura.host;

import org.eclipse.ecf.mgmt.framework.IServiceEventHandlerAsync;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class KuraServiceManager extends ServiceManager implements IServiceManager, RemoteServiceAdminListener  {

	@Activate
	protected void activate(BundleContext ctxt) throws Exception {
		super.activate(ctxt);
	}
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,policy=ReferencePolicy.DYNAMIC)
	void bindServiceEventHandler(IServiceEventHandlerAsync seh) {
		super.addServiceEventHandler(seh);
	}

	void unbindServiceEventHandler(IServiceEventHandlerAsync beh) {
		super.removeServiceEventHandler(beh);
	}

	@Reference
	protected void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.bindRemoteServiceAdmin(rsa);
	}
	
	protected void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.unbindRemoteServiceAdmin(rsa);
	}
}
