package org.eclipse.ecf.mgmt.kura.host;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.DebugRemoteServiceAdminListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component
public class RSADebug extends DebugRemoteServiceAdminListener implements RemoteServiceAdminListener {

}
