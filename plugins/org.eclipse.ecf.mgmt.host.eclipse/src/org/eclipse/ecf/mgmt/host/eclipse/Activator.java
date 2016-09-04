/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.host.eclipse;

import java.util.Dictionary;

import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.host.BundleManager;
import org.eclipse.ecf.mgmt.framework.host.ServiceManager;
import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManager;
import org.eclipse.ecf.mgmt.rsa.host.RemoteServiceAdminManager;
import org.eclipse.ecf.osgi.services.distribution.IDistributionConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.DebugRemoteServiceAdminListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public class Activator extends AbstractUIPlugin {

	public static final boolean DEBUG = new Boolean(
			System.getProperty("org.eclipse.ecf.mgmt.host.eclipse.debug", "true")).booleanValue();

	public static final String HOSTNAME_PREF = "mgmt.hostname";
	public static final String PORT_PREF = "mgmt.port";
	public static final String HOSTNAME_DEFAULT = "localhost";
	public static final Integer PORT_DEFAULT = new Integer(3791);
	public static final String GENERIC_CONFIG = "ecf.generic.server";
	public static final String GENERIC_CONFIG_HOSTNAME = GENERIC_CONFIG + ".hostname";
	public static final String GENERIC_CONFIG_PORT = GENERIC_CONFIG + ".port";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.mgmt.host.eclipse"; //$NON-NLS-1$
	public static final String ECF_ASYNC_INTERFACES = "ecf.exported.async.interfaces";

	// The shared instance
	private static Activator plugin;
	private BundleContext context;

	private ServiceRegistration<IServiceManager> smReg;
	private ServiceRegistration<IRemoteServiceAdminManager> rsamReg;

	private ServiceRegistration<IBundleManager> bmReg;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	static class EclipseServiceManager extends ServiceManager {
		public EclipseServiceManager(BundleContext context) throws Exception {
			activate(context);
		}
	}

	static class EclipseBundleManager extends BundleManager {
		public EclipseBundleManager(BundleContext context) throws Exception {
			activate(context);
		}
	}
	
	static class EclipseRSAManager extends RemoteServiceAdminManager {
		public EclipseRSAManager(BundleContext context) throws Exception {
			bindRemoteServiceAdmin(RSAComponent.getRemoteServiceAdmin());
			activate(context);
		}
	}

	private RemoteServiceAdminEvent rsaEvent;

	public synchronized void registerRSAManager(Dictionary<String, Object> props) throws Throwable {
		rsamReg = this.context.registerService(IRemoteServiceAdminManager.class, new EclipseRSAManager(this.context),
				props);
		if (this.rsaEvent == null) {
			unregisterRSAManager();
			throw new Exception("No RSA registered rsaEvent for RSA manager registration");
		} else {
			Throwable t = this.rsaEvent.getException();
			if (t != null) {
				unregisterRSAManager();
				rsaEvent = null;
				throw t;
			} else if (this.rsaEvent.getType() != RemoteServiceAdminEvent.EXPORT_REGISTRATION) {
				unregisterRSAManager();
				rsaEvent = null;
				throw new Exception("Invalid RSA rsaEvent for RSA manager registration");
			}
			rsaEvent = null;
		}
	}

	public synchronized void registerServiceManager(Dictionary<String, Object> props) throws Throwable {
		smReg = this.context.registerService(IServiceManager.class, new EclipseServiceManager(this.context), props);
		if (this.rsaEvent == null) {
			unregisterRSAManager();
			throw new Exception("No registered rsaEvent for service manager registration");
		} else {
			Throwable t = this.rsaEvent.getException();
			if (t != null) {
				unregisterServiceManager();
				rsaEvent = null;
				throw t;
			} else if (this.rsaEvent.getType() != RemoteServiceAdminEvent.EXPORT_REGISTRATION) {
				unregisterServiceManager();
				rsaEvent = null;
				throw new Exception("Invalid rsaEvent type for service manager registration");
			}
			rsaEvent = null;
		}
	}

	public synchronized void registerBundleManager(Dictionary<String, Object> props) throws Throwable {
		bmReg = this.context.registerService(IBundleManager.class, new EclipseBundleManager(this.context), props);
		if (this.rsaEvent == null) {
			unregisterRSAManager();
			throw new Exception("No registered rsaEvent for service manager registration");
		} else {
			Throwable t = this.rsaEvent.getException();
			if (t != null) {
				unregisterBundleManager();
				rsaEvent = null;
				throw t;
			} else if (this.rsaEvent.getType() != RemoteServiceAdminEvent.EXPORT_REGISTRATION) {
				unregisterServiceManager();
				rsaEvent = null;
				throw new Exception("Invalid rsaEvent type for service manager registration");
			}
			rsaEvent = null;
		}
	}

	public boolean isServiceManagerRegistered() {
		return smReg != null;
	}

	public boolean isBundleManagerRegistered() {
		return bmReg != null;
	}
	
	public boolean isRSAManagerRegistered() {
		return rsamReg != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;
		try {
			// Verify that org.eclipse.ecf.osgi.services.distribution bundle
			// (BasicTopologyManager)
			// is present and active
			@SuppressWarnings({ "unused" })
			String constant = IDistributionConstants.REMOTE_CONFIGS_SUPPORTED;
		} catch (Exception | NoClassDefFoundError e) {
			e.printStackTrace();
		}
		// Set up defaults
		IPreferenceStore store = getPreferenceStore();
		if (store != null) {
			store.setDefault(HOSTNAME_PREF, HOSTNAME_DEFAULT);
			store.setDefault(PORT_PREF, PORT_DEFAULT);
		}
		// debugging
		if (DEBUG)
			this.context.registerService(RemoteServiceAdminListener.class, new DebugRemoteServiceAdminListener(), null);
	
	}

	public void stopCompositeDiscoveryBundle() {
		if (context == null) return;
		Bundle discoveryProvider = null;
		for(Bundle b: this.context.getBundles()) 
			if (b.getSymbolicName().equals("org.eclipse.ecf.provider.discovery"))
				discoveryProvider = b;
		if (discoveryProvider != null) 
			try {
				discoveryProvider.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		unregisterServiceManager();
		unregisterBundleManager();
		unregisterRSAManager();
		plugin = null;
		this.context = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public void unregisterServiceManager() {
		if (smReg != null) {
			smReg.unregister();
			smReg = null;
			rsaEvent = null;
		}
	}

	public void unregisterBundleManager() {
		if (bmReg != null) {
			bmReg.unregister();
			bmReg = null;
			rsaEvent = null;
		}
	}

	public void unregisterRSAManager() {
		if (rsamReg != null) {
			rsamReg.unregister();
			rsamReg = null;
			rsaEvent = null;
		}
	}

	public synchronized void remoteAdminEvent(RemoteServiceAdminEvent event) {
		this.rsaEvent = event;
	}
}
