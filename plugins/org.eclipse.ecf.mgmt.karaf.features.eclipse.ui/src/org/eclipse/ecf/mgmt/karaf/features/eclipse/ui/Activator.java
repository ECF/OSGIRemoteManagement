package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.ecf.mgmt.karaf.features.eclipse.ui";
	
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
		super.stop(context);
		instance = null;
	}
	
	public ImageDescriptor getIconDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
