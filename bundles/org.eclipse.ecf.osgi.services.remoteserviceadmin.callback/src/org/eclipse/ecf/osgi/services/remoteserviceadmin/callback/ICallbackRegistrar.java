package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.osgi.framework.ServiceRegistration;

public interface ICallbackRegistrar {

	ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception;
	
}
