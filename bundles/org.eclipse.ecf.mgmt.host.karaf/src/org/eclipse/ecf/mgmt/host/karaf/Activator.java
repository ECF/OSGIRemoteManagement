package org.eclipse.ecf.mgmt.host.karaf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManager;
import org.eclipse.ecf.mgmt.rsa.host.RemoteServiceAdminManager;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.DebugRemoteServiceAdminListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static final String DEFAULT_HOSTNAME = System.getProperty("org.eclipse.ecf.mgmt.host.karaf.hostname");
	private static final Integer DEFAULT_PORT = Integer
			.valueOf(System.getProperty("org.eclipse.ecf.mgmt.host.karaf.port", "3791"));

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private String hostname;

	public Activator() {
		if (DEFAULT_HOSTNAME == null) {
			try {
				hostname = InetAddress.getLocalHost().getCanonicalHostName();
			} catch (UnknownHostException e) {
				hostname = "localhost"; //$NON-NLS-1$
			}
		} else
			hostname = DEFAULT_HOSTNAME;
	}

	static class KarafServiceManager extends ServiceManager {
		public KarafServiceManager(BundleContext context) throws Exception {
			activate(context);
		}
	}

	static class KarafRSAManager extends RemoteServiceAdminManager {
		public KarafRSAManager(RemoteServiceAdmin rsa, BundleContext context) throws Exception {
			bindRemoteServiceAdmin(rsa);
			activate(context);
		}
	}

	RemoteServiceAdmin getRemoteServiceAdmin() {
		RemoteServiceAdmin result = null;
		ServiceTracker<RemoteServiceAdmin, RemoteServiceAdmin> st = new ServiceTracker<RemoteServiceAdmin, RemoteServiceAdmin>(
				Activator.context, RemoteServiceAdmin.class, null);
		st.open();
		result = st.getService();
		st.close();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		context.registerService(RemoteServiceAdminListener.class, new DebugRemoteServiceAdminListener(), null);
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("service.exported.interfaces", "*");
		props.put("ecf.exported.async.interfaces", "*");
		props.put(Constants.SERVICE_EXPORTED_CONFIGS, "ecf.generic.server");
		props.put("ecf.generic.server.hostname", hostname);
		props.put("ecf.generic.server.port", DEFAULT_PORT.toString());
		// service manager
		context.registerService(IServiceManager.class, new KarafServiceManager(context), props);
		// rsa manager
		RemoteServiceAdmin rsa = getRemoteServiceAdmin();
		if (rsa == null) {
			System.out.println("Cannot get RemoteServiceAdmin so not registering RSAManager remote service");
		} else
			context.registerService(IRemoteServiceAdminManager.class, new KarafRSAManager(rsa, context), props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
