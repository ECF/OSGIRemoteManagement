/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
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
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class ServiceExporterCallbackImporter extends CallbackSupport implements RemoteServiceAdminListener {

	private Map<ServiceReference<?>, ExportedService> exportedServices;

	public class ExportedService {

		public class ExportedServiceRemoteServiceListener implements IRemoteServiceListener {
			@Override
			public void handleServiceEvent(IRemoteServiceEvent event) {
				if (Arrays.asList(event.getClazzes()).contains(callbackClass.getName())) {
					if (event instanceof IRemoteServiceRegisteredEvent) {
						if (importRegistration == null) {
							try {
								importRegistration = (ImportRegistration) getRSA().importService(
										new EndpointDescription(createImportProperties(event.getReference())));
								if (importRegistration == null)
									throw new RuntimeException("Callback import registration returned is null");
								Throwable t = importRegistration.getException();
								if (t != null)
									throw new RuntimeException("Exception importing callback service", t);
							} catch (Exception e) {
								importRegistration = null;
								logException("Exception in callback rsa import", e);
							}
						}
					} else if (event instanceof IRemoteServiceUnregisteredEvent) {
						if (importRegistration != null) {
							try {
								importRegistration.close();
								importRegistration = null;
							} catch (Exception e) {
								logException("Exception in callback rsa import", e);
							}
						}
					}
				}
			}
		}

		private Class<?> callbackClass;
		private IContainer exportingContainer;
		private ImportRegistration importRegistration;
		private ExportedServiceRemoteServiceListener listener;

		public ExportedService(Class<?> callbackClass) {
			this.callbackClass = callbackClass;
			this.listener = new ExportedServiceRemoteServiceListener();
		}

		private IRemoteServiceContainerAdapter getRSAdapter() {
			if (exportingContainer != null)
				return (IRemoteServiceContainerAdapter) exportingContainer
						.getAdapter(IRemoteServiceContainerAdapter.class);
			else
				return null;
		}

		synchronized void exportViaContainer(ID containerID) {
			exportingContainer = getContainerManager().getContainer(containerID);
			IRemoteServiceContainerAdapter ca = getRSAdapter();
			if (ca != null)
				ca.addRemoteServiceListener(listener);
		}

		synchronized void unexportViaContainer() {
			IRemoteServiceContainerAdapter ca = getRSAdapter();
			if (ca != null)
				ca.removeRemoteServiceListener(listener);
			exportingContainer = null;
		}

		protected Map<String, Object> createImportProperties(IRemoteServiceReference rsRef) {
			Map<String, Object> result = new HashMap<String, Object>();
			IRemoteServiceID rsID = rsRef.getID();
			ID rcID = rsID.getContainerID();
			result.put(RemoteConstants.ENDPOINT_ID, rcID.getName());
			result.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, rcID.getNamespace().getName());
			result.put("objectClass", new String[] { callbackClass.getName() });
			result.put("ecf.endpoint.connecttarget.id", exportingContainer.getID().getName());
			result.put("ecf.endpoint.idfilter.ids", new String[] { rcID.getName() });
			result.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, rsID.getContainerRelativeID());
			result.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED, "true");
			result.put(RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, "*");

			for (String key : rsRef.getPropertyKeys())
				if (key.startsWith(ECF_RSA_PROP_PREFIX))
					result.put(key.substring(ECF_RSA_PROP_PREFIX.length()), rsRef.getProperty(key));

			return result;
		}

	}

	public void addExportedServiceCallback(ServiceReference<?> exportedServiceReference, Class<?> callbackClass) {
		this.exportedServices.put(exportedServiceReference, new ExportedService(callbackClass));
	}

	public void removeExportedServiceCallback(ServiceReference<?> exportedServiceReference) {
		this.exportedServices.remove(exportedServiceReference);
	}

	private ServiceRegistration<RemoteServiceAdminListener> listenerReg;

	public void activate(BundleContext bundleContext) throws Exception {
		super.activate(bundleContext);
		exportedServices = new HashMap<ServiceReference<?>, ExportedService>();
		listenerReg = bundleContext.registerService(RemoteServiceAdminListener.class, this, null);
	}

	public void deactivate() {
		if (listenerReg != null) {
			listenerReg.unregister();
			listenerReg = null;
		}
		if (exportedServices != null) {
			exportedServices.clear();
			exportedServices = null;
		}
		super.deactivate();
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
