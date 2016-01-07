package org.eclipse.ecf.mgmt.host.eclipse;

import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class RSAComponent implements RemoteServiceAdminListener {

	private static RemoteServiceAdmin remoteServiceAdmin;

	void activate() {
		listener = Activator.getDefault();
	}

	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		remoteServiceAdmin = rsa;
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		remoteServiceAdmin = null;
	}

	public static RemoteServiceAdmin getRemoteServiceAdmin() {
		return remoteServiceAdmin;
	}

	private Activator listener;

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		Activator l = this.listener;
		if (l != null)
			l.remoteAdminEvent(event);
	}
}
