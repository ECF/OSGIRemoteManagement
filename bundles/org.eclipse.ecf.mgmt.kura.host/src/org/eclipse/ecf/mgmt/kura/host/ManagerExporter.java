package org.eclipse.ecf.mgmt.kura.host;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.IServiceEventHandler;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.karaf.features.FeaturesInstaller;
import org.eclipse.ecf.mgmt.karaf.features.FeaturesListener;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ServiceExporterCallbackImporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

@Component(immediate = true)
public class ManagerExporter extends ServiceExporterCallbackImporter {

	private ServiceReference<IBundleManager> bmRef;
	private ServiceReference<IServiceManager> smRef;
	private ServiceReference<FeaturesInstaller> fiRef;
	
	@Reference
	public void bindRemoteServiceAdmin(RemoteServiceAdmin a) {
		super.bindRemoteServiceAdmin(a);
	}

	public void unbindRemoteServiceAdmin(RemoteServiceAdmin a) {
		super.unbindRemoteServiceAdmin(a);
	}

	@Reference
	public void bindContainerManager(IContainerManager cm) {
		super.bindContainerManager(cm);
	}

	public void unbindContainerManager(IContainerManager cm) {
		super.unbindContainerManager(cm);
	}

	@Reference
	void bindBundleManager(ServiceReference<IBundleManager> r) {
		this.bmRef = r;
	}

	void unbindBundleManager(ServiceReference<IBundleManager> r) {
		this.bmRef = null;
	}

	@Reference
	void bindServiceManager(ServiceReference<IServiceManager> r) {
		this.smRef = r;
	}

	void unbindServiceManager(ServiceReference<IServiceManager> r) {
		this.smRef = null;
	}

	@Reference
	void bindKarafFeaturesInstallerManager(ServiceReference<FeaturesInstaller> r) {
		this.fiRef = r;
	}
	
	void unbindKarafFeaturesInstallerManager(ServiceReference<FeaturesInstaller> r) {
		this.fiRef = null;
	}
	
	private ExportRegistration bmReg;
	private ExportRegistration smReg;
	private ExportRegistration fiReg;
	
	@Activate
	public void activate(BundleContext c) throws Exception {
		super.activate(c);
		addExportedServiceCallback(bmRef, IBundleEventHandler.class);
		addExportedServiceCallback(smRef, IServiceEventHandler.class);
		addExportedServiceCallback(fiRef, FeaturesListener.class);
		Map<String,Object> props = createRemoteServiceProperties();
		Collection<ExportRegistration> regs = getRSA().exportService(bmRef, props);
		bmReg = regs.iterator().next();
		Throwable t = bmReg.getException();
		if (t != null) {
			bmReg = null;
			throw new RuntimeException("Could not export BundleManager service");
		}
		regs = getRSA().exportService(smRef, props);
		smReg = regs.iterator().next();
		t = smReg.getException();
		if (t != null) {
			this.bmReg.close();
			this.bmReg = null;
			this.smReg = null;
			throw new RuntimeException("Could not export ServiceManager service");
		}
		regs = getRSA().exportService(fiRef, props);
		fiReg = regs.iterator().next();
		t = fiReg.getException();
		if (t != null) {
			this.bmReg.close();
			this.bmReg = null;
			this.smReg.close();
			this.smReg = null;
			this.fiReg.close();
			this.fiReg = null;
			throw new RuntimeException("Could not export ServiceManager service");
		}
	}

	@Deactivate
	public void deactivate() {
		if (bmReg != null) {
			bmReg.close();
			bmReg = null;
		}
		if (smReg != null) {
			smReg.close();
			smReg = null;
		}
		if (fiReg != null) {
			fiReg.close();
			fiReg = null;
		}
		super.deactivate();
	}

	private static final String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";
	private static final String EXPORT_CONFIG = "ecf.jms.mqtt.manager";
	private static final String EXPORT_CONFIG_ID = "tcp://iot.eclipse.org:1883/kura/remoteservices";

	private Map<String, Object> createRemoteServiceProperties() {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		result.put("service.exported.interfaces", "*");
		result.put("ecf.exported.async.interfaces", "*");
		// get system properties
		Properties props = System.getProperties();
		String config = props.getProperty(SERVICE_EXPORTED_CONFIGS);
		if (config == null)
			config = EXPORT_CONFIG;
		result.put(SERVICE_EXPORTED_CONFIGS, config);
		String configid = props.getProperty(config+".id");
		if (configid == null)
			configid = EXPORT_CONFIG_ID;
		result.put(config+".id", configid);
		// add any config properties. config properties start with
		// the config name '.' property
		for (Object k : props.keySet()) {
			if (k instanceof String) {
				String key = (String) k;
				if (key.startsWith(config))
					result.put(key, props.get(key));
			}
		}
		return result;
	}

}
