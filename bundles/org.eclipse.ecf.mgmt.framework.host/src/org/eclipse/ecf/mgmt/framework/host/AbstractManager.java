/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.host;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.FrameworkMTO;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.log.LogService;

public abstract class AbstractManager implements IAdaptable {

	interface BundleSelector {
		boolean select(Bundle b);
	}

	interface ServiceReferenceDTOSelector {
		boolean select(ServiceReferenceDTO srd);
	}

	private BundleContext bundleContext;
	private LogService logService;
	private IAdapterManager adapterManager;

	protected BundleContext getContext() {
		return bundleContext;
	}

	protected LogService getLogService() {
		return logService;
	}

	void bindLogService(LogService logService) {
		this.logService = logService;
	}

	void unbindLogService(LogService logService) {
		this.logService = null;
	}

	protected IAdapterManager getAdapterManager() {
		return adapterManager;
	}

	void bindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = adapterManager;
	}

	void unbindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = null;
	}

	void activate(BundleContext context) {
		this.bundleContext = context;
	}

	void deactivate() {
		this.bundleContext = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map convertDictionaryToMap(Dictionary dict) {
		Map result = new HashMap();
		for (Enumeration e = dict.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			result.put(key, dict.get(key));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> convertHeadersToMap(Bundle b) {
		return (Map<String, String>) convertDictionaryToMap(b.getHeaders());
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.isInstance(this))
			return this;
		final IAdapterManager adapterManager = getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

	protected static boolean isSerializable(Object o) {
		try {
			ObjectOutputStream ois = new ObjectOutputStream(
					new ByteArrayOutputStream());
			ois.writeObject(o);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map convertProperties(Dictionary dict) {
		Map result = new HashMap();
		for (Enumeration e = dict.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Object value = dict.get(key);
			if (isSerializable(value))
				result.put(key, value);
			else
				result.put(key, value.toString());
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map convertProperties(ServiceReference serviceReference) {
		Map props = new HashMap();
		String keys[] = serviceReference.getPropertyKeys();
		for (int i = 0; i < keys.length; i++) {
			Object value = serviceReference.getProperty(keys[i]);
			if (isSerializable(value))
				props.put(keys[i], value);
			else
				props.put(keys[i], value.toString());
		}
		return props;
	}

	protected IStatus createErrorStatus(String message, Throwable t) {
		logError(message, t);
		return new SerializableStatus(IStatus.ERROR, getContext().getBundle()
				.getSymbolicName(), message, t);
	}

	protected IStatus createErrorStatus(String message) {
		logError(message, null);
		return new SerializableStatus(IStatus.ERROR, getContext().getBundle()
				.getSymbolicName(), message, null);
	}

	protected void logError(String message, Throwable t) {
		LogService log = getLogService();
		if (log != null)
			log.log(LogService.LOG_ERROR, message, t);
		System.err.println(message);
		if (t != null)
			t.printStackTrace(System.err);
	}

	protected BundleMTO[] findBundleMTOs(BundleSelector s) {
		Bundle[] bundles = findBundles(s);
		List<BundleMTO> results = new ArrayList<BundleMTO>();
		for (Bundle b : bundles)
			results.add(createBundleMTO(b));
		return results.toArray(new BundleMTO[results.size()]);
	}

	protected Bundle[] findBundles(BundleSelector s) {
		List<Bundle> results = new ArrayList<Bundle>();
		for (Bundle b : getContext().getBundles())
			if (s == null || s.select(b))
				results.add(b);
		return results.toArray(new Bundle[results.size()]);
	}

	protected ServiceReferenceDTO[] findServiceReferenceDTOs(
			ServiceReferenceDTOSelector s) {
		List<ServiceReferenceDTO> results = new ArrayList<ServiceReferenceDTO>();
		for (ServiceReferenceDTO srd : getServiceReferenceDTOs())
			if (s == null || s.select(srd))
				results.add(srd);
		return results.toArray(new ServiceReferenceDTO[results.size()]);
	}

	protected ServiceReferenceMTO[] findServiceReferenceMTOs(
			ServiceReferenceDTOSelector s) {
		List<ServiceReferenceMTO> results = new ArrayList<ServiceReferenceMTO>();
		for (ServiceReferenceDTO srd : findServiceReferenceDTOs(s))
			results.add(new ServiceReferenceMTO(srd));
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	protected BundleMTO createBundleMTO(Bundle bundle) {
		return new BundleMTO(bundle.adapt(BundleDTO.class),
				convertHeadersToMap(bundle));
	}

	protected Bundle getFrameworkBundle() {
		Bundle[] bs = findBundles(new BundleSelector() {
			public boolean select(Bundle b) {
				return b.getBundleId() == 0;
			}
		});
		return bs[0];
	}

	protected FrameworkDTO getFrameworkDTO() {
		return getFrameworkBundle().adapt(FrameworkDTO.class);
	}

	protected FrameworkMTO createFrameworkMTO() {
		FrameworkDTO fdto = getFrameworkDTO();
		return new FrameworkMTO(findBundleMTOs(null), fdto.properties,
				findServiceReferenceMTOs(null));
	}

	protected List<ServiceReferenceDTO> getServiceReferenceDTOs() {
		return getFrameworkDTO().services;
	}

}
