/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.CallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ImportCallbackAssociation;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ImportCallbackAssociator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class ImportCallbackAssociatorImpl extends AbstractCallbackAssociator implements RemoteServiceAdminListener, ImportCallbackAssociator {

	protected class ExportableCallback implements ImportCallbackAssociation {
		protected Class<?> importedServiceInterface;
		protected CallbackRegistrar callbackRegistrar;
		protected ServiceRegistration<?> callbackRegistration;
		protected Map<ImportReference, List<ExportRegistration>> callbackExportRegs;

		public CallbackRegistrar getCallbackRegistrar() {
			return this.callbackRegistrar;
		}
		
		public Class<?> getImportedServiceInterface() {
			return importedServiceInterface;
		}
		
		public void disassociate() {
			removeCallbackRegistrar(this.importedServiceInterface);
		}
		
		protected ExportableCallback(Class<?> importedServiceInterface, CallbackRegistrar registrar) {
			this.importedServiceInterface = importedServiceInterface;
			this.callbackRegistrar = registrar;
			this.callbackRegistration = null;
			this.callbackExportRegs = Collections.synchronizedMap(new HashMap<ImportReference, List<ExportRegistration>>());
		}

		protected boolean registerAndExportCallback(ImportReference importedServiceRef) throws Throwable {
			if (importedServiceRef == null)
				return false;
			EndpointDescription ed = (EndpointDescription) importedServiceRef.getImportedEndpoint();
			if (ed == null)
				return false;
			// first look to see if we've already exported for this import
			// reference
			List<ExportRegistration> exportRegistrations = null;
			exportRegistrations = this.callbackExportRegs.get(importedServiceRef);
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
				Collection<ExportRegistration> exportRegs = getRemoteServiceAdmin().exportService(callbackRef, properties);
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

		protected void closeRegistrations(List<ExportRegistration> eRegs) {
			if (eRegs != null)
				for (ExportRegistration er : eRegs)
					try {
						er.close();
					} catch (Exception e) {
						logException("Could not close export registration=" + er, e);
					}
		}

		protected void close(ImportReference importReference) {
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

		protected void closeAll() {
			Set<ImportReference> keys = new HashSet<ImportReference>(this.callbackExportRegs.keySet());
			for (Iterator<ImportReference> i = keys.iterator(); i.hasNext();)
				close(i.next());
		}
	}

	protected Map<Class<?>, ExportableCallback> serviceCallbackMap = new HashMap<Class<?>, ExportableCallback>();

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
	
	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		super.activate(bundleContext);
		this.serviceCallbackMap = new HashMap<Class<?>, ExportableCallback>();
	}

	@Deactivate
	protected void deactivate() {
		if (serviceCallbackMap != null) {
			serviceCallbackMap.clear();
			serviceCallbackMap = null;
		}
		super.deactivate();
	}

	public ImportCallbackAssociation associateCallbackRegistrar(Class<?> importedServiceClass, CallbackRegistrar callbackRegistrar) {
		if (importedServiceClass == null || callbackRegistrar == null)
			throw new NullPointerException("Service and registrar must both be non-null");
		ExportableCallback ec = null;
		synchronized (serviceCallbackMap) {
			ec = serviceCallbackMap.get(importedServiceClass);
			if (ec != null) {
				if (!callbackRegistrar.equals(ec.getCallbackRegistrar()))
					throw new RuntimeException("importedServiceClass="+importedServiceClass.getName()+" is already associated with callbackRegistrar="+ec.getCallbackRegistrar());
			} else {
				ec = new ExportableCallback(importedServiceClass, callbackRegistrar);
				serviceCallbackMap.put(importedServiceClass, ec);
			}
		}
		return ec;
	}

	protected CallbackRegistrar getAssociatedRegistrar(Class<?> importedServiceClass) {
		ExportableCallback ec = null;
		synchronized (serviceCallbackMap) {
			ec = serviceCallbackMap.get(importedServiceClass);
		}
		return (ec == null)?null:ec.callbackRegistrar;
	}

	protected void removeCallbackRegistrar(Class<?> importedServiceClass) {
		synchronized (serviceCallbackMap) {
			serviceCallbackMap.remove(importedServiceClass);
		}
	}

	protected ExportableCallback findExportableCallback(List<String> services, List<String> asyncServices) {
		synchronized (serviceCallbackMap) {
			for (Class<?> c : serviceCallbackMap.keySet())
				if (services.contains(c.getName()) || asyncServices.contains(c.getName()))
					return serviceCallbackMap.get(c);
		}
		return null;
	}

	protected ExportableCallback findExportableCallback(ImportReference importRef) {
		synchronized (serviceCallbackMap) {
			for (ExportableCallback cb : serviceCallbackMap.values()) {
				List<ExportRegistration> exportRegs = cb.callbackExportRegs.get(importRef);
				if (exportRegs != null)
					return cb;
			}
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
				ExportableCallback cb = null;
					if (type == RemoteServiceAdminEvent.IMPORT_REGISTRATION) {
						EndpointDescription ed = (EndpointDescription) importRef.getImportedEndpoint();
						if (ed != null)
							cb = findExportableCallback(ed.getInterfaces(), ed.getAsyncInterfaces());
					} else if (type == RemoteServiceAdminEvent.IMPORT_UNREGISTRATION) 
						cb = findExportableCallback(importRef);
				if (cb != null) {
					if (type == RemoteServiceAdminEvent.IMPORT_REGISTRATION) {
						try {
							cb.registerAndExportCallback(importRef);
						} catch (Throwable e) {
							logException("Could not export callback=" + cb, e);
						}
					} else if (type == RemoteServiceAdminEvent.IMPORT_UNREGISTRATION) {
						cb.close(importRef);
					}
				}
			}
		}
	}

	protected Map<String, Object> createCallbackExportProperties(EndpointDescription ed, ServiceReference<?> serviceRef) {
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
