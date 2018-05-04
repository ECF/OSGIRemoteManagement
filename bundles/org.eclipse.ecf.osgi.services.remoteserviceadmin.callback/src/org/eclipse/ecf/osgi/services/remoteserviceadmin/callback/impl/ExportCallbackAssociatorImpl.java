/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ExportCallbackAssociation;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ExportCallbackAssociator;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceRegisteredEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteServiceUnregisteredEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class ExportCallbackAssociatorImpl extends AbstractCallbackAssociator implements RemoteServiceAdminListener, ExportCallbackAssociator {

	
	protected class CallbackAssociation implements ExportCallbackAssociation {

		protected ServiceReference<?> exportableServiceReference;
		protected Class<?> callbackClass;
		protected IContainer exportingContainer;
		protected ImportRegistration importRegistration;
		protected CallbackAssociationRemoteServiceListener listener;

		public class CallbackAssociationRemoteServiceListener implements IRemoteServiceListener {
			@Override
			public void handleServiceEvent(IRemoteServiceEvent event) {
				if (Arrays.asList(event.getClazzes()).contains(callbackClass.getName())) {
					if (event instanceof IRemoteServiceRegisteredEvent) {
						if (importRegistration == null) {
							try {
								importRegistration = (ImportRegistration) getRemoteServiceAdmin().importService(
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

		protected CallbackAssociation(ServiceReference<?> exportableServiceReference, Class<?> callbackClass) {
			this.exportableServiceReference = exportableServiceReference;
			this.callbackClass = callbackClass;
			this.listener = new CallbackAssociationRemoteServiceListener();
		}

		protected IRemoteServiceContainerAdapter getRSAdapter() {
			if (exportingContainer != null)
				return (IRemoteServiceContainerAdapter) exportingContainer
						.getAdapter(IRemoteServiceContainerAdapter.class);
			else
				return null;
		}

		synchronized protected void exportViaContainer(ID containerID) {
			exportingContainer = getContainerManager().getContainer(containerID);
			IRemoteServiceContainerAdapter ca = getRSAdapter();
			if (ca != null)
				ca.addRemoteServiceListener(listener);
		}

		synchronized protected void unexportViaContainer() {
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

		public Class<?> getCallbackServiceInterface() {
			return this.callbackClass;
		}
		
		public ServiceReference<?> getExportableServiceReference() {
			return this.exportableServiceReference;
		}
		
		@Override
		public void disassociate() {
			removeAssociatedCallback(this.exportableServiceReference);
		}

	}

	@Reference
	protected void bindContainerManager(IContainerManager cm) {
		super.bindContainerManager(cm);
	}
	
	protected void unbindContainerManager(IContainerManager cm) {
		super.unbindContainerManager(cm);
	}
	
	@Reference
	protected void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.bindRemoteServiceAdmin(rsa);
	}
	
	protected void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		super.unbindRemoteServiceAdmin(rsa);
	}
	
	private Map<ServiceReference<?>, CallbackAssociation> callbackAssociations;

	public ExportCallbackAssociation associateExportableWithCallback(ServiceReference<?> exportableServiceReference, Class<?> callbackClass) {
		CallbackAssociation ca = null;
		synchronized (callbackAssociations) {
			ca = callbackAssociations.get(exportableServiceReference);
			if (ca != null) {
				Class<?> cc = ca.getCallbackServiceInterface();
				if (!callbackClass.equals(cc))
					throw new RuntimeException("exportableServiceReference="+exportableServiceReference+" already associated with callbackServiceInterface="+cc.getName());
			} else {
				ca = new CallbackAssociation(exportableServiceReference, callbackClass);
				this.callbackAssociations.put(exportableServiceReference, ca);
			}
		}
		return ca;
	}

	protected Class<?> getAssociatedCallback(ServiceReference<?> exportedServiceReference) {
		CallbackAssociation es = null;
		synchronized (callbackAssociations) {
			es = this.callbackAssociations.get(exportedServiceReference);
		}
		return (es == null)?null:es.callbackClass;
	}
	
	protected void removeAssociatedCallback(ServiceReference<?> exportedServiceReference) {
		synchronized (callbackAssociations) {
			this.callbackAssociations.remove(exportedServiceReference);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		super.activate(bundleContext);
		callbackAssociations = new HashMap<ServiceReference<?>, CallbackAssociation>();
	}

	@Deactivate
	protected void deactivate() {
		if (callbackAssociations != null) {
			callbackAssociations.clear();
			callbackAssociations = null;
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
					CallbackAssociation es = null;
					synchronized (callbackAssociations) {
						if (type == RemoteServiceAdminEvent.EXPORT_REGISTRATION) 
							es = callbackAssociations.get(svcRef);
						else if (type == RemoteServiceAdminEvent.EXPORT_UNREGISTRATION) 
							es = callbackAssociations.remove(svcRef);
					}
					if (es != null) {
						if (type == RemoteServiceAdminEvent.EXPORT_REGISTRATION)
							es.exportViaContainer(exportRef.getContainerID());
						else if (type == RemoteServiceAdminEvent.EXPORT_UNREGISTRATION) 
							es.unexportViaContainer();
					}
				}
			}
		}
	}

}
