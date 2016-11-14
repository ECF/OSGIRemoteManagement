/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class ServiceImporterCallbackExporter extends CallbackSupport implements RemoteServiceAdminListener {

	protected class ExportableCallback {
		private ICallbackRegistrar callbackRegistrar;
		private ServiceRegistration<?> callbackRegistration;
		private Map<ImportReference, List<ExportRegistration>> callbackExportRegs;

		ExportableCallback(ICallbackRegistrar registrar) {
			this.callbackRegistrar = registrar;
			this.callbackRegistration = null;
			this.callbackExportRegs = new HashMap<ImportReference, List<ExportRegistration>>();
		}

		synchronized boolean registerAndExportCallback(ImportReference importedServiceRef) throws Throwable {
			if (importedServiceRef == null)
				return false;
			EndpointDescription ed = (EndpointDescription) importedServiceRef.getImportedEndpoint();
			if (ed == null)
				return false;
			// first look to see if we've already exported for this import
			// reference
			List<ExportRegistration> exportRegistrations = this.callbackExportRegs.get(importedServiceRef);
			if (exportRegistrations != null)
				return false;
			callbackRegistration = this.callbackRegistrar.registerCallback(importedServiceRef);
			if (callbackRegistration == null)
				throw new NullPointerException("Callback registration cannot be null");
			ServiceReference<?> callbackRef = this.callbackRegistration.getReference();
			exportRegistrations = new ArrayList<ExportRegistration>();
			try {
				Map<String, Object> properties = createCallbackExportProperties(ed,
						this.callbackRegistration.getReference());
				Collection<ExportRegistration> exportRegs = getRSA().exportService(callbackRef, properties);
				if (exportRegs.size() > 0) {
					ExportRegistration exportReg = exportRegs.iterator().next();
					if (exportReg != null) {
						Throwable t = exportReg.getException();
						if (t != null)
							throw t;
						else
							exportRegistrations.add(exportReg);
					}
				}
				if (exportRegistrations.size() > 0)
					this.callbackExportRegs.put(importedServiceRef, exportRegistrations);
				return true;
			} catch (Throwable e) {
				logException("Could not export callback serviceRef=" + callbackRef, e);
				this.callbackRegistration.unregister();
				this.callbackRegistration = null;
				throw e;
			}
		}

		private void closeRegistrations(List<ExportRegistration> eRegs) {
			if (eRegs != null)
				for (ExportRegistration er : eRegs)
					try {
						er.close();
					} catch (Exception e) {
						logException("Could not close export registration=" + er, e);
					}
		}

		synchronized void close(ImportReference importReference) {
			List<ExportRegistration> exportRegs = this.callbackExportRegs.remove(importReference);
			if (exportRegs != null)
				closeRegistrations(exportRegs);
			if (this.callbackExportRegs.size() == 0 && this.callbackRegistration != null) {
				try {
					this.callbackRegistration.unregister();
				} catch (Exception e) {
					logException("Exception unregistering callback", e);
				}
				this.callbackRegistration = null;
			}
		}

		synchronized void closeAll() {
			Set<ImportReference> keys = new HashSet<ImportReference>(this.callbackExportRegs.keySet());
			for (Iterator<ImportReference> i = keys.iterator(); i.hasNext();)
				close(i.next());
		}
	}

	private Map<Class<?>, ExportableCallback> serviceCallbackMap = new HashMap<Class<?>, ExportableCallback>();

	public void addServiceCallback(Class<?> service, ICallbackRegistrar registrar) {
		if (service == null || registrar == null)
			throw new NullPointerException("Service and registrar must both be non-null");
		synchronized (serviceCallbackMap) {
			serviceCallbackMap.put(service, new ExportableCallback(registrar));
		}
	}

	public void removeCallbackForService(Class<?> service) {
		synchronized (serviceCallbackMap) {
			ExportableCallback ecb = serviceCallbackMap.remove(service);
			if (ecb != null)
				ecb.closeAll();
		}
	}

	ExportableCallback findExportableCallback(List<String> services, List<String> asyncServices) {
		for (Class<?> c : serviceCallbackMap.keySet())
			if (services.contains(c.getName()) || asyncServices.contains(c.getName()))
				return serviceCallbackMap.get(c);
		return null;
	}

	ExportableCallback findExportableCallback(ImportReference importRef) {
		for (ExportableCallback cb : serviceCallbackMap.values()) {
			List<ExportRegistration> exportRegs = cb.callbackExportRegs.get(importRef);
			if (exportRegs != null)
				return cb;
		}
		return null;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		int type = event.getType();
		Throwable t = event.getException();
		if (t == null) {
			ImportReference importRef = (ImportReference) event.getImportReference();
			if (importRef != null) {
				synchronized (serviceCallbackMap) {
					if (type == RemoteServiceAdminEvent.IMPORT_REGISTRATION) {
						EndpointDescription ed = (EndpointDescription) importRef.getImportedEndpoint();
						if (ed != null) {
							ExportableCallback exportableCallback = findExportableCallback(ed.getInterfaces(),
									ed.getAsyncInterfaces());
							if (exportableCallback != null)
								try {
									exportableCallback.registerAndExportCallback(importRef);
								} catch (Throwable e) {
									logException("Could not export callback=" + exportableCallback, e);
								}
						}
					} else if (type == RemoteServiceAdminEvent.IMPORT_UNREGISTRATION) {
						ExportableCallback cb = findExportableCallback(importRef);
						if (cb != null)
							cb.close(importRef);
					}
				}
			}
		}
	}

	private Map<String, Object> createCallbackExportProperties(EndpointDescription ed, ServiceReference<?> serviceRef) {
		ID cID = ed.getContainerID();
		IContainer c = getContainerConnectedToID(cID);
		ContainerTypeDescription ctd = getContainerTypeDescription(c.getID());
		String containerFactoryId = ctd.getName();
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put("service.exported.interfaces", "*");
		props.put("ecf.exported.async.interfaces", "*");
		props.put("service.exported.configs", containerFactoryId);
		props.put("ecf.endpoint.connecttarget.id", cID.getName());
		if (c != null)
			props.put("ecf.endpoint.idfilter.ids", new String[] { c.getID().getName() });

		// test
		String endpointid = UUID.randomUUID().toString();
		props.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointid);
		props.put(ECF_RSA_PROP_PREFIX + org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointid);
		String fwuuid = getContext().getProperty("org.osgi.framework.uuid");
		if (fwuuid != null)
			props.put(ECF_RSA_PROP_PREFIX + RemoteConstants.ENDPOINT_FRAMEWORK_UUID, fwuuid);
		props.put(
				ECF_RSA_PROP_PREFIX
						+ org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_TIMESTAMP,
				System.currentTimeMillis());
		props.put(ECF_RSA_PROP_PREFIX + org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED,
				new String[] { containerFactoryId });
		props.put(ECF_RSA_PROP_PREFIX + org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED,
				ctd.getSupportedIntents());
		props.put(ECF_RSA_PROP_PREFIX + org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS,
				new String[] { containerFactoryId });

		List<String> intfs = Arrays.asList((String[]) serviceRef.getProperty("objectClass"));
		Object svc = getContext().getService(serviceRef);
		Class<?> clazz = findMatchingInterface(intfs, svc.getClass());
		getContext().ungetService(serviceRef);
		if (clazz != null) {
			String pkgName = getPackageName(clazz.getName());
			String pkgVer = getCallbackPackageVersion(clazz, pkgName);
			if (pkgVer != null)
				props.put(ECF_RSA_PROP_PREFIX
						+ org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_ + pkgName,
						pkgVer);
		}
		return props;
	}

}
