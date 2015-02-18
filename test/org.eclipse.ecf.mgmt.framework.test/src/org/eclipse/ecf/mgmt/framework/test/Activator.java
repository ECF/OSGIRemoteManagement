package org.eclipse.ecf.mgmt.framework.test;

import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.IFrameworkManager;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static BundleContext context;

	@Override
	public void start(BundleContext ctxt) throws Exception {
		context = ctxt;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		context = null;
	}

	public static IBundleManager getBundleManager() {
		ServiceTracker<IBundleManager, IBundleManager> st = new ServiceTracker<IBundleManager, IBundleManager>(
				context, IBundleManager.class, null);
		st.open();
		IBundleManager bm = st.getService();
		st.close();
		return bm;
	}

	public static IServiceManager getServiceManager() {
		ServiceTracker<IServiceManager, IServiceManager> st = new ServiceTracker<IServiceManager, IServiceManager>(
				context, IServiceManager.class, null);
		st.open();
		IServiceManager sm = st.getService();
		st.close();
		return sm;
	}

	public static IFrameworkManager getFrameworkManager() {
		ServiceTracker<IFrameworkManager, IFrameworkManager> st = new ServiceTracker<IFrameworkManager, IFrameworkManager>(
				context, IFrameworkManager.class, null);
		st.open();
		IFrameworkManager sm = st.getService();
		st.close();
		return sm;
	}

}
