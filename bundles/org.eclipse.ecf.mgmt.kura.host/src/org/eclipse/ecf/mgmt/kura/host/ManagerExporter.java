package org.eclipse.ecf.mgmt.kura.host;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceUnregisteredEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate = true)
public class ManagerExporter {

	private RemoteServiceAdmin rsa;
	private IContainerManager containerManager;
	// private ServiceReference<IRemoteServiceAdminManager> rsaRef;
	private ServiceReference<IBundleManager> bmRef;
	// private Collection<ExportRegistration> rsaRegs;
	private Collection<ExportRegistration> regs = new ArrayList<ExportRegistration>();

	private ServiceReference<IServiceManager> smRef;
	
	@Reference
	void bindRemoteServiceAdmin(RemoteServiceAdmin a) {
		this.rsa = a;
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin a) {
		this.rsa = null;
	}

	@Reference
	void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}

	void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
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

	void export(ServiceReference<?> ref, Map<String,Object> props) throws Exception {
		Collection<ExportRegistration> regs = this.rsa.exportService(ref, props);
		Iterator<ExportRegistration> i = regs.iterator();
		if (i.hasNext()) {
			Throwable t = i.next().getException();
			if (t != null) {
				regs.forEach(reg -> reg.close());
				throw new ServiceException("Could not export service", t);
			}
		}
		this.regs.addAll(regs);
	}
	@Activate
	protected void activate(BundleContext c) throws Exception {

		// c.registerService(RemoteServiceAdminListener.class, new
		// MyRSAListener(), null);
		Map<String, Object> rsProps = createRemoteServiceProperties();

		if (this.bmRef != null) 
			export(this.bmRef, rsProps);
		
		if (this.smRef != null)
			export(this.smRef, rsProps);
	}

	@Deactivate
	protected void deactivate() throws Exception {
		if (regs != null) {
			regs.forEach(reg -> reg.close());
			regs = null;
		}
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

	private Class<?> clazz = IBundleEventHandler.class;
	private ImportRegistration importRegistration;

	private void handleRemoteServiceRegisteredEvent(ID localID, IRemoteServiceReference rsRef) {
		synchronized (this) {
			if (importRegistration != null)
				return;
			Map<String, Object> importProps = createImportProps(localID, rsRef);
			try {
				ImportRegistration reg = (ImportRegistration) rsa.importService(new EndpointDescription(importProps));
				if (reg.getException() == null)
					importRegistration = reg;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Map<String, Object> createImportProps(ID localID, IRemoteServiceReference rsRef) {
		Map<String, Object> result = new HashMap<String, Object>();
		IRemoteServiceID rsID = rsRef.getID();
		ID rcID = rsID.getContainerID();
		result.put(RemoteConstants.ENDPOINT_ID, rcID.getName());
		result.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, rcID.getNamespace().getName());
		result.put(RemoteConstants.ENDPOINT_TIMESTAMP, System.currentTimeMillis());
		result.put(RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, "*");
		result.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, rsID.getContainerRelativeID());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID,
				UUID.randomUUID().toString());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, UUID.randomUUID().toString());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_
				+ "org.eclipse.ecf.mgmt.framework", "1.0.0");
		result.put("objectClass", new String[] { IBundleEventHandler.class.getName() });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED,
				new String[] { "ecf.generic.client" });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED,
				new String[] { "passByValue,exactlyOnce,ordered" });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, "true");
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS,
				new String[] { "ecf.generic.client" });
		result.put("ecf.endpoint.connecttarget.id", localID.getName());
		result.put("ecf.endpoint.idfilter.ids", new String[] { rcID.getName() });

		return result;
	}

	private void handleRemoteServiceUnregisteredEvent(IRemoteServiceReference rsRef) {
		synchronized (this) {
			if (importRegistration == null)
				return;
			try {
				importRegistration.close();
				importRegistration = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private IRemoteServiceListener rmListener = new IRemoteServiceListener() {
		@Override
		public void handleServiceEvent(IRemoteServiceEvent event) {
			List<String> clazzes = Arrays.asList(event.getClazzes());
			IRemoteServiceReference rsRef = event.getReference();
			if (clazzes.contains(clazz.getName())) {
				if (event instanceof IRemoteServiceRegisteredEvent) {
					handleRemoteServiceRegisteredEvent(event.getLocalContainerID(), rsRef);
				} else if (event instanceof IRemoteServiceUnregisteredEvent) {
					handleRemoteServiceUnregisteredEvent(rsRef);
				}
			}
		}
	};

	private IContainer exportContainer;
	private IRemoteServiceContainerAdapter exportContainerAdapter;

	class MyRSAListener implements RemoteServiceAdminListener {
		@Override
		public void remoteAdminEvent(RemoteServiceAdminEvent event) {
			int type = event.getType();
			if (RemoteServiceAdminEvent.EXPORT_REGISTRATION == type) {
				ExportReference exportRef = (ExportReference) event.getExportReference();
				if (bmRef == exportRef.getExportedService()) {
					exportContainer = containerManager.getContainer(exportRef.getContainerID());
					if (exportContainer != null) {
						exportContainerAdapter = (IRemoteServiceContainerAdapter) exportContainer
								.getAdapter(IRemoteServiceContainerAdapter.class);
						exportContainerAdapter.addRemoteServiceListener(rmListener);
					}
				}
			} else if (RemoteServiceAdminEvent.EXPORT_UNREGISTRATION == type) {
				ExportReference exportRef = (ExportReference) event.getExportReference();
				if (bmRef == exportRef.getExportedService()) {
					if (exportContainerAdapter != null) {
						exportContainerAdapter.removeRemoteServiceListener(rmListener);
						exportContainerAdapter = null;
						exportContainer = null;
					}
				}
			}
		}
	}
}
