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
import java.util.function.Function;

import org.eclipse.ecf.mgmt.framework.IServiceEventHandlerAsync;
import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.ServiceEventMTO;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class ServiceManager extends AbstractManager implements IServiceManager, RemoteServiceAdminListener {

	private final Function<ServiceReferenceDTO, ServiceReferenceMTO> srmapper = srd -> {
		return ServiceReferenceMTO.createMTO(srd, getServiceReferenceExportImportState(srd.id));
	};

	@Override
	public ServiceReferenceMTO[] getServiceReferences() {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, null, srmapper);
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	@Override
	public ServiceReferenceMTO getServiceReference(final long serviceId) {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, srd -> {
			return srd.id == serviceId;
		}, srmapper);
		return results.size() > 0 ? results.get(0) : null;
	}

	@Override
	public ServiceReferenceMTO[] getServiceReferences(final long bundleId) {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, srd -> {
			return srd.bundle == bundleId;
		}, srmapper);
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	protected void fireServiceChangedEvent(final ServiceEvent event) {
		List<IServiceEventHandlerAsync> notify = null;
		synchronized (sehs) {
			notify = new ArrayList<IServiceEventHandlerAsync>(sehs);
		}
		ServiceReferenceMTO mto = getServiceReference(
				(Long) event.getServiceReference().getProperty(org.osgi.framework.Constants.SERVICE_ID));
		if (mto != null)
			for (IServiceEventHandlerAsync beh : notify)
				beh.handleServiceEventAsync(new ServiceEventMTO(event.getType(), mto));
	}

	protected List<IServiceEventHandlerAsync> sehs = new ArrayList<IServiceEventHandlerAsync>();

	protected boolean addServiceEventHandler(IServiceEventHandlerAsync async) {
		synchronized (sehs) {
			return sehs.add(async);
		}
	}

	protected boolean removeServiceEventHandler(IServiceEventHandlerAsync async) {
		synchronized (sehs) {
			return sehs.remove(async);
		}
	}

	private ServiceListener serviceListener = new ServiceListener() {
		@Override
		public void serviceChanged(ServiceEvent event) {
			List<IServiceEventHandlerAsync> notify = null;
			synchronized (sehs) {
				notify = new ArrayList<IServiceEventHandlerAsync>(sehs);
			}
			int eventType = event.getType();
			ServiceReference<?> ref = null;
			ServiceReferenceMTO mto = null;
			for (IServiceEventHandlerAsync beh : notify) {
				if (ref == null)
					ref = event.getServiceReference();
				if (mto == null)
					mto = ServiceReferenceMTO.createMTO(ref, getServiceReferenceExportImportState((Long) ref.getProperty(Constants.SERVICE_ID)));
				beh.handleServiceEventAsync(new ServiceEventMTO(eventType, mto));
			}
		}
	};

	protected void activate(BundleContext context) throws Exception {
		super.activate(context);
		getContext().addServiceListener(serviceListener);
	}

	protected void deactivate() throws Exception {
		getContext().removeServiceListener(serviceListener);
		super.deactivate();
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		ExportReference eRef = event.getExportReference();
		ServiceReference<?> ref = null;
		if (eRef != null) 
			ref = eRef.getExportedService();
		else {
			ImportReference iRef = event.getImportReference();
			ref = iRef.getImportedService();
		}
		if (ref != null)
			serviceListener.serviceChanged(new ServiceEvent(ServiceEvent.MODIFIED,ref));
	}
}
