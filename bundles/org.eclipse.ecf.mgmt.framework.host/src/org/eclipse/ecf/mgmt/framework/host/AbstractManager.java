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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import org.osgi.service.log.LogService;

public abstract class AbstractManager implements IAdaptable {

	private BundleContext bundleContext;
	private LogService logService;
	private IAdapterManager adapterManager;

	protected BundleContext getContext() {
		return bundleContext;
	}

	protected LogService getLogService() {
		return logService;
	}

	protected void bindLogService(LogService logService) {
		this.logService = logService;
	}

	protected void unbindLogService(LogService logService) {
		this.logService = null;
	}

	protected IAdapterManager getAdapterManager() {
		return adapterManager;
	}

	protected void bindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = adapterManager;
	}

	protected void unbindAdapterManager(IAdapterManager adapterManager) {
		this.adapterManager = null;
	}

	protected void activate(BundleContext context) throws Exception {
		this.bundleContext = context;
	}

	protected void deactivate() throws Exception {
		this.bundleContext = null;
	}

	protected IStatus createErrorStatus(String message, Throwable t) {
		logError(message, t);
		return new SerializableStatus(IStatus.ERROR, getContext().getBundle().getSymbolicName(), message, t);
	}

	protected IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

	protected void logError(String message, Throwable t) {
		LogService log = getLogService();
		if (log != null)
			log.log(LogService.LOG_ERROR, message, t);
		System.err.println(message);
		if (t != null)
			t.printStackTrace(System.err);
	}

	protected List<Bundle> getAllBundles() {
		return Arrays.asList(getContext().getBundles());
	}

	protected <T> List<T> select(List<T> source, final Predicate<T> filter) {
		return source.stream().filter(new Predicate<T>() {
			public boolean test(T ins) {
				return filter == null?true:filter.test(ins);
			}
		}).collect(Collectors.toList());
	}

	protected <T, R> List<R> selectAndMap(List<T> source, final Predicate<T> filter, Function<T, R> map) {
		return select(source, filter).stream().map(map).collect(Collectors.toList());
	}

	protected Bundle getBundle0(long bundleId) {
		return getContext().getBundle(bundleId);
	}

	protected Bundle getFrameworkBundle() {
		return getBundle0(0);
	}

	protected FrameworkDTO getFrameworkDTO() {
		return getFrameworkBundle().adapt(FrameworkDTO.class);
	}

	protected FrameworkMTO createFrameworkMTO() {
		List<BundleMTO> bundleMTOs = selectAndMap(getAllBundles(), null, b -> {
			return BundleMTO.createMTO(b);
		});
		FrameworkDTO frameworkDTO = getFrameworkDTO();
		List<ServiceReferenceMTO> srMTOs = selectAndMap(frameworkDTO.services, null, srDTO -> {
			return ServiceReferenceMTO.createMTO(srDTO);
		});
		return new FrameworkMTO(bundleMTOs.toArray(new BundleMTO[bundleMTOs.size()]), frameworkDTO.properties,
				srMTOs.toArray(new ServiceReferenceMTO[srMTOs.size()]));
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == null)
			return null;
		if (adapter.isInstance(this))
			return this;
		final IAdapterManager adapterManager = getAdapterManager();
		if (adapterManager == null)
			return null;
		return adapterManager.loadAdapter(this, adapter.getName());
	}

}
