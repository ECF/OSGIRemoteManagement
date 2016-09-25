package org.eclipse.ecf.mgmt.kura.host;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

@Component(immediate=true)
public class RSAExporter {

	private RemoteServiceAdmin rsa;
	private ServiceReference<IBundleManager> ref;
	private Collection<ExportRegistration> rsregs;
	
	@Reference
	void bindRemoteServiceAdmin(RemoteServiceAdmin a) {
		this.rsa = a;
	}
	
	void unbindRemoteServiceAdmin(RemoteServiceAdmin a) {
		this.rsa = null;
	}
	
	@Reference
	void bindBundleManager(ServiceReference<IBundleManager> r) {
		this.ref = r;
	}
	
	void unbindBundleManager(ServiceReference<IBundleManager> r) {
		this.ref = null;
	}
	
	@Activate
	protected void activate() throws Exception {
		// export with overriding properties
		rsregs = this.rsa.exportService(this.ref, createRemoteServiceProperties());
		// should only be one exportregistration for this provider
		Throwable t = rsregs.iterator().next().getException();
		if (t != null) 
			throw new ServiceException("Could not export service",t);
	}
	
	@Deactivate
	protected void deactivate() throws Exception {
		if (rsregs != null) {
			rsregs.forEach(reg -> reg.close());
			rsregs = null;
		}
	}

	private static final String SERVICE_EXPORTED_CONFIGS = "service.exported.configs";
	private static final String DEFAULT_CONFIG = "ecf.generic.server";
	private static final String DEFAULT_PORT = "3939";
	private static final String DEFAULT_HOST = "localhost";
	
	private Map<String, Object> createRemoteServiceProperties() {
		Hashtable<String, Object> result = new Hashtable<String, Object>();
		// This property is required by the Remote Services specification
		// (chapter 100 in enterprise specification), and when set results
		// in RSA impl exporting as a remote service
		result.put("service.exported.interfaces", "*");
		// async interfaces is an ECF Remote Services service property
		// that allows any declared asynchronous interfaces
		// to be used by consumers.
		// See https://wiki.eclipse.org/ECF/Asynchronous_Remote_Services
		result.put("ecf.exported.async.interfaces", "*");
		// get system properties
		Properties props = System.getProperties();
		// Get OSGi service.exported.configs property
		String config = props.getProperty(SERVICE_EXPORTED_CONFIGS);
		// If not present, then use default
		if (config == null) {
			config = DEFAULT_CONFIG;
			result.put(config + ".port", DEFAULT_PORT);
			result.put(config + ".hostname", DEFAULT_HOST);
		}
		result.put(SERVICE_EXPORTED_CONFIGS, config);
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
