package org.eclipse.ecf.mgmt.kura.host;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.framework.IBundleEventHandler;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
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
	private Collection<ExportRegistration> bmRegs;

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

	@Activate
	protected void activate(BundleContext c) throws Exception {

		//c.registerService(RemoteServiceAdminListener.class, new MyRSAListener(), null);
		Map<String, Object> rsProps = createRemoteServiceProperties();

		bmRegs = this.rsa.exportService(this.bmRef, rsProps);
		// should only be one exportregistration for this provider
		Throwable t = bmRegs.iterator().next().getException();
		if (t != null) {
			bmRegs.forEach(reg -> reg.close());
			bmRegs = null;
			throw new ServiceException("Could not export bundle manager service", t);
		} else {

		}

	}

	@Deactivate
	protected void deactivate() throws Exception {
		if (bmRegs != null) {
			bmRegs.forEach(reg -> reg.close());
			bmRegs = null;
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
		Map<String,Object> result = new HashMap<String,Object>();
		IRemoteServiceID rsID = rsRef.getID();
		ID rcID = rsID.getContainerID();
		result.put(RemoteConstants.ENDPOINT_ID, rcID.getName());
		result.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, rcID.getNamespace().getName());
		result.put(RemoteConstants.ENDPOINT_TIMESTAMP, System.currentTimeMillis());
		result.put(RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, "*");
		result.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, rsID.getContainerRelativeID() );
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID, UUID.randomUUID().toString());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, UUID.randomUUID().toString());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_+"org.eclipse.ecf.mgmt.framework", "1.0.0");
		result.put("objectClass", new String[] { IBundleEventHandler.class.getName() });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED, new String[] { "ecf.generic.client" }  );
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED, new String[] { "passByValue,exactlyOnce,ordered" }  );
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, "true");
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, new String[] { "ecf.generic.client" }  );
		result.put("ecf.endpoint.connecttarget.id", localID.getName());
		result.put("ecf.endpoint.idfilter.ids", new String[] { rcID.getName() } );

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