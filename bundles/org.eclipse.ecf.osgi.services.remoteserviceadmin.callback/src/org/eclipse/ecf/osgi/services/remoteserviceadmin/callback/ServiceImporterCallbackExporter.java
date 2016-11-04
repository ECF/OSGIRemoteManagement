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
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class ServiceImporterCallbackExporter extends CallbackSupport implements RemoteServiceAdminListener {

	protected class ExportableCallback {
		private ServiceReference<?> serviceRef;
		private ExportRegistration exportReg;

		ExportableCallback(ServiceReference<?> serviceRef) {
			this.serviceRef = serviceRef;
		}

		void export(Map<String, Object> properties) throws Throwable {
			Collection<ExportRegistration> exportRegs = getRSA().exportService(this.serviceRef, properties);
			if (exportRegs.size() > 0) {
				ExportRegistration exportReg = exportRegs.iterator().next();
				if (exportReg != null) {
					Throwable t = exportReg.getException();
					if (t != null)
						throw t;
					else
						this.exportReg = exportReg;
				}
			}
		}
	}

	private Map<Class<?>, ExportableCallback> serviceCallbackMap = new HashMap<Class<?>, ExportableCallback>();

	public void addCallbackForService(Class<?> service, ServiceReference<?> callbackRef) {
		synchronized (serviceCallbackMap) {
			serviceCallbackMap.put(service, new ExportableCallback(callbackRef));
		}
	}

	public void removeCallbackForService(Class<?> service) {
		synchronized (serviceCallbackMap) {
			ExportableCallback ecb = serviceCallbackMap.remove(service);
			if (ecb != null && ecb.exportReg != null) {
				ecb.exportReg.close();
				ecb.exportReg = null;
			}
		}
	}

	ExportableCallback findExportableCallback(List<String> services, List<String> asyncServices) {
		for (Class<?> c : serviceCallbackMap.keySet())
			if (services.contains(c.getName()) || asyncServices.contains(c.getName()))
				return serviceCallbackMap.get(c);
		return null;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		int type = event.getType();
		Throwable t = event.getException();
		if (t == null) {
			ImportReference importRef = (ImportReference) event.getImportReference();
			if (importRef != null) {
				ServiceReference<?> proxyRef = importRef.getImportedService();
				if (proxyRef != null) {
					synchronized (serviceCallbackMap) {
						if (type == RemoteServiceAdminEvent.IMPORT_REGISTRATION) {
							EndpointDescription ed = (EndpointDescription) importRef.getImportedEndpoint();
							if (ed != null) {
								ExportableCallback exportableCallback = findExportableCallback(ed.getInterfaces(),
										ed.getAsyncInterfaces());
								if (exportableCallback != null && exportableCallback.exportReg == null)
									try {
										exportableCallback.export(
												createCallbackExportProperties(ed, exportableCallback.serviceRef));
									} catch (Throwable e) {
										logException("Could not export callback=" + exportableCallback, e);
									}
							}
						} else if (type == RemoteServiceAdminEvent.IMPORT_UNREGISTRATION) {
							EndpointDescription ed = (EndpointDescription) importRef.getImportedEndpoint();
							if (ed != null) {
								ExportableCallback exportableCallback = findExportableCallback(ed.getInterfaces(),
										ed.getAsyncInterfaces());
								if (exportableCallback != null && exportableCallback.exportReg != null) {
									exportableCallback.exportReg.close();
									exportableCallback.exportReg = null;
								}
							}
						}
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
		Class<?> clazz = findMatchingInterface(intfs,svc.getClass());
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
