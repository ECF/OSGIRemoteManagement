package org.eclipse.ecf.mgmt.kura.host;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceUnregisteredEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class ServiceExporterCallbackImporter implements RemoteServiceAdminListener {
	private RemoteServiceAdmin rsa;
	private IContainerManager containerManager;
	private Map<ServiceReference<?>, ExportedService> exportedServices;

	protected class ExportedService {
		private Class<?> callbackClass;
		private IContainer exportingContainer;
		private ImportRegistration importRegistration;
		
		public ExportedService(Class<?> callbackClass) {
			this.callbackClass = callbackClass;
		}

		private IRemoteServiceContainerAdapter getRSAdapter() {
			if (exportingContainer != null)
				return (IRemoteServiceContainerAdapter) exportingContainer
						.getAdapter(IRemoteServiceContainerAdapter.class);
			else
				return null;
		}

		void exportViaContainer(ID containerID) {
			exportingContainer = containerManager.getContainer(containerID);
			IRemoteServiceContainerAdapter ca = getRSAdapter();
			if (ca != null)
				ca.addRemoteServiceListener(listener);
		}

		void unexportViaContainer() {
			IRemoteServiceContainerAdapter ca = getRSAdapter();
			if (ca != null)
				ca.removeRemoteServiceListener(listener);
			exportingContainer = null;
		}

		IRemoteServiceListener listener = new IRemoteServiceListener() {
			@Override
			public void handleServiceEvent(IRemoteServiceEvent event) {
				if (Arrays.asList(event.getClazzes()).contains(callbackClass.getName())) 
					if (event instanceof IRemoteServiceRegisteredEvent) 
						handleRemoteServiceRegisteredEvent(event.getLocalContainerID(), event.getReference());
					else if (event instanceof IRemoteServiceUnregisteredEvent) 
						handleRemoteServiceUnregisteredEvent(event.getLocalContainerID(), event.getReference());
			}
		};

	}

	void handleRemoteServiceRegisteredEvent(ID localContainerID, IRemoteServiceReference ref) {
		synchronized (exportedServices) {
			ExportedService es = null;
			for(ExportedService c: exportedServices.values())
				if (c.exportingContainer != null && c.exportingContainer.getID().equals(localContainerID) && c.importRegistration == null) {
					es = c;
					break;
				}
			if (es != null) 
				try {
					ImportRegistration ir = (ImportRegistration) rsa.importService(new EndpointDescription(createImportProperties(es, ref)));
					if (ir == null)
						throw new RuntimeException("Callback import registration returned is null");
					Throwable t = ir.getException();
					if (t != null)
						throw new RuntimeException("Exception importing callback service",t);
				} catch (Exception e) {
					logException("Exception in callback rsa import",e);
				}
		}
	}

	protected void logException(String string, Throwable e) {
		System.out.println(string);
		if (e != null)
			e.printStackTrace();
	}

	protected Map<String, Object> createImportProperties(ExportedService es, IRemoteServiceReference rsRef) {
		Map<String, Object> result = new HashMap<String, Object>();
		IRemoteServiceID rsID = rsRef.getID();
		ID rcID = rsID.getContainerID();
		result.put(RemoteConstants.ENDPOINT_ID, rcID.getName());
		result.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, rcID.getNamespace().getName());
		result.put("objectClass", new String[] { es.callbackClass.getName() });
		result.put("ecf.endpoint.connecttarget.id", es.exportingContainer.getID().getName());
		result.put("ecf.endpoint.idfilter.ids", new String[] { rcID.getName() });
		result.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, rsID.getContainerRelativeID());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, "true");
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, UUID.randomUUID().toString());
		
		
		result.put(RemoteConstants.ENDPOINT_TIMESTAMP, System.currentTimeMillis());
		result.put(RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, "*");
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID,
				UUID.randomUUID().toString());
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_
				+ "org.eclipse.ecf.mgmt.framework", "1.0.0");
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED,
				new String[] { "ecf.generic.client" });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED,
				new String[] { "passByValue,exactlyOnce,ordered" });
		result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS,
				new String[] { "ecf.generic.client" });

		return result;
	}

	void handleRemoteServiceUnregisteredEvent(ID localContainerID, IRemoteServiceReference ref) {
		synchronized (exportedServices) {
			ExportedService es = null;
			for(ExportedService c: exportedServices.values())
				if (c.exportingContainer != null && c.exportingContainer.getID().equals(localContainerID) && c.importRegistration != null) {
					es = c;
					break;
				}
			if (es != null) 
				try {
					es.importRegistration.close();
					es.importRegistration = null;
				} catch (Exception e) {
					logException("Exception in callback rsa import",e);
				}
		}
	}

	protected void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = rsa;
	}

	protected void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = null;
	}

	protected RemoteServiceAdmin getRSA() {
		return rsa;
	}

	protected void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}

	protected void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
	}

	protected IContainerManager getContainerManager() {
		return this.containerManager;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		int type = event.getType();
		Throwable t = event.getException();
		if (t == null) {
			ExportReference exportRef = (ExportReference) event.getExportReference();
			if (exportRef != null) {
				ServiceReference<?> svcRef = exportRef.getExportedService();
				if (svcRef != null) {
					synchronized (exportedServices) {
						if (type == RemoteServiceAdminEvent.EXPORT_REGISTRATION) {
							ExportedService es = exportedServices.get(svcRef);
							if (es != null)
								es.exportViaContainer(exportRef.getContainerID());
						} else if (type == RemoteServiceAdminEvent.EXPORT_UNREGISTRATION) {
							ExportedService es = exportedServices.remove(svcRef);
							if (es != null)
								es.unexportViaContainer();
						}
					}
				}
			}
		}
	}

}
