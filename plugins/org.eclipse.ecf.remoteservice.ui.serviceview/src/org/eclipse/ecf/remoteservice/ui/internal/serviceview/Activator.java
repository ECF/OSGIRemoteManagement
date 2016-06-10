package org.eclipse.ecf.remoteservice.ui.internal.serviceview;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.ecf.remoteservice.ui.internal.serviceview";

	private static Activator instance;

	public static Activator getDefault() {
		return instance;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}
	
	public ServiceReference<?> getServiceReference(long serviceId) {
		BundleContext ctx = getBundle().getBundleContext();
		ServiceReference<?>[] sr = null;
		if (ctx != null) {
			try {
				sr = ctx.getAllServiceReferences(null, "("+Constants.SERVICE_ID+"="+serviceId+")");
			} catch (InvalidSyntaxException e) {
				// should never happen
			}
			if (sr != null && sr.length > 0)
				return sr[0];
		}
		return null;
	}
}
