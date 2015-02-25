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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.FrameworkMTO;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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

	public void bindLogService(LogService logService) {
		this.logService = logService;
	}

	public void unbindLogService(LogService logService) {
		this.logService = null;
	}

	protected IAdapterManager getAdapterManager() {
		return adapterManager;
	}

	public void bindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = adapterManager;
	}

	public void unbindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = null;
	}

	public void activate(BundleContext context) throws Exception {
		this.bundleContext = context;
	}

	public void deactivate() throws Exception {
		this.bundleContext = null;
	}

	protected IStatus createErrorStatus(String message, Throwable t) {
		logError(message, t);
		return new SerializableStatus(IStatus.ERROR, getContext().getBundle().getSymbolicName(), message, t);
	}

	protected IStatus createErrorStatus(String message) {
		logError(message, null);
		return new SerializableStatus(IStatus.ERROR, getContext().getBundle().getSymbolicName(), message, null);
	}

	protected void logError(String message, Throwable t) {
		LogService log = getLogService();
		if (log != null)
			log.log(LogService.LOG_ERROR, message, t);
		System.err.println(message);
		if (t != null)
			t.printStackTrace(System.err);
	}

	protected BundleMTO[] selectBundleMTOs(BundleSelector s) {
		Bundle[] bundles = selectBundles(s);
		return BundleMTO.createMTOs(bundles);
	}

	protected Bundle[] selectBundles(BundleSelector s) {
		List<Bundle> results = new ArrayList<Bundle>();
		for (Bundle b : getContext().getBundles())
			if (s == null || s.select(b))
				results.add(b);
		return results.toArray(new Bundle[results.size()]);
	}

	protected Bundle selectBundle(BundleSelector s) {
		Bundle[] bundles = selectBundles(s);
		return (bundles.length > 0) ? bundles[0] : null;
	}

	protected ServiceReferenceDTO[] selectServiceReferenceDTOs(ServiceReferenceDTOSelector s) {
		List<ServiceReferenceDTO> results = new ArrayList<ServiceReferenceDTO>();
		for (ServiceReferenceDTO srd : getServiceReferenceDTOs())
			if (s == null || s.select(srd))
				results.add(srd);
		return results.toArray(new ServiceReferenceDTO[results.size()]);
	}

	protected ServiceReferenceMTO[] selectServiceReferenceMTOs(ServiceReferenceDTOSelector s) {
		List<ServiceReferenceMTO> results = new ArrayList<ServiceReferenceMTO>();
		for (ServiceReferenceDTO srd : selectServiceReferenceDTOs(s))
			results.add(ServiceReferenceMTO.createMTO(srd));
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	protected Bundle getBundle0(long bundleId) {
		return getContext().getBundle(bundleId);
	}

	protected Bundle getFrameworkBundle() {
		Bundle[] bs = selectBundles(new BundleSelector() {
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
		return new FrameworkMTO(selectBundleMTOs(null), fdto.properties, selectServiceReferenceMTOs(null));
	}

	protected List<ServiceReferenceDTO> getServiceReferenceDTOs() {
		return getFrameworkDTO().services;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.isInstance(this))
			return this;
		final IAdapterManager adapterManager = getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

}
