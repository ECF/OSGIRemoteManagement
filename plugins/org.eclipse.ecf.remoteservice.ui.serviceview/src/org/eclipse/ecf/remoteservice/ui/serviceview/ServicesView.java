/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.remoteservice.ui.internal.serviceview.DiscoveryComponent;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.RegisteringBundleIdNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.UsingBundleIdsNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.FrameworkDTO;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;

/**
 * @since 3.3
 */
public class ServicesView extends AbstractServicesView {

	public static final String ID_VIEW = "org.eclipse.ecf.remoteservice.ui.serviceview.ServiceView"; //$NON-NLS-1$

	public ServicesView() {
	}

	@Override
	public void dispose() {
		DiscoveryComponent discovery = DiscoveryComponent.getDefault();
		BundleContext ctxt = discovery.getContext();
		if (ctxt != null)
			ctxt.removeServiceListener(serviceListener);
		discovery.setServicesView(null);
		super.dispose();
	}

	private List<ServiceReferenceDTO> getServiceDTOs(BundleContext ctxt) {
		return ctxt.getBundle(0).adapt(FrameworkDTO.class).services;
	}

	private ServiceReferenceDTO getServiceDTO(BundleContext ctxt, ServiceReference<?> sr) {
		long serviceId = (Long) sr.getProperty(Constants.SERVICE_ID);
		for (ServiceReferenceDTO ref : getServiceDTOs(ctxt))
			if (serviceId == ref.id)
				return ref;
		return null;
	}

	private ServiceListener serviceListener = new ServiceListener() {
		@Override
		public void serviceChanged(final ServiceEvent event) {
			final TreeViewer v = getTreeViewer();
			if (v == null)
				return;
			v.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					TreeViewer tv = getTreeViewer();
					if (tv == null)
						return;
					BundleContext ctxt = DiscoveryComponent.getDefault().getContext();
					if (ctxt == null)
						return;
					ServiceReference<?> sr = event.getServiceReference();
					ServiceReferenceDTO srDTO = null;
					switch (event.getType()) {
					// add
					case ServiceEvent.REGISTERED:
						srDTO = getServiceDTO(ctxt, sr);
						if (srDTO != null)
							getServicesRoot().addChild(
									createServiceNode(srDTO.id, srDTO.bundle, srDTO.usingBundles, srDTO.properties));
						break;
					// modified properties
					case ServiceEvent.MODIFIED:
						srDTO = getServiceDTO(ctxt, sr);
						if (srDTO != null) {
							ServiceNode sn = findServiceNode(srDTO.id);
							if (sn != null)
								sn.setProperties(srDTO.properties);
						}
						break;
					// removed
					case ServiceEvent.UNREGISTERING:
						ServiceNode sn = findServiceNode(getServiceId(sr));
						if (sn != null)
							getServicesRoot().removeChild(sn);
						break;
					}
					tv.setExpandedState(getServicesRoot(), true);
					tv.refresh();
				}
			});
		}
	};

	protected void initializeServices() {
		DiscoveryComponent d = DiscoveryComponent.getDefault();
		d.setServicesView(this);
		final BundleContext ctxt = d.getContext();
		ctxt.addServiceListener(serviceListener);

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<ServiceNode> snds = new ArrayList<ServiceNode>();
				for (ServiceReferenceDTO sr : getServiceDTOs(ctxt))
					snds.add(createServiceNode(sr.id, sr.bundle, sr.usingBundles, sr.properties));
				addServiceNodes(snds);
			}
		}).start();
	}
	
	protected ServiceNode createServiceNode(long serviceId, long bundleId, long[] usingBundleIds,
			Map<String, Object> properties) {
		org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa = DiscoveryComponent.getDefault().getRSA();
		ExportReference eRef = null;
		ImportReference iRef = null;
		if (rsa != null) {
			for (ExportReference er : rsa.getExportedServices()) {
				long exServiceId = getServiceId(er.getExportedService());
				if (exServiceId == serviceId)
					eRef = er;
			}
			if (eRef == null)
				for (ImportReference ir : rsa.getImportedEndpoints()) {
					long imServiceId = getServiceId(ir.getImportedService());
					if (imServiceId == serviceId)
						iRef = ir;
				}
		}
		ServiceNode result = new ServiceNode(bundleId, usingBundleIds, properties, eRef, iRef);
		result.addChild(new RegisteringBundleIdNode(bundleId));
		result.addChild(new UsingBundleIdsNode("Using Bundles", usingBundleIds));
		return result;
	}


	private ServiceNode findServiceNode(ExportReference eRef) {
		AbstractServicesNode[] services = getServicesRoot().getChildren();
		for (AbstractServicesNode asn : services) {
			if (asn instanceof ServiceNode) {
				ServiceNode sn = (ServiceNode) asn;
				if (eRef == sn.getExportRef())
					return sn;
			}
		}
		return null;
	}

	private ServiceNode findServiceNode(ImportReference iRef) {
		AbstractServicesNode[] services = getServicesRoot().getChildren();
		for (AbstractServicesNode asn : services) {
			if (asn instanceof ServiceNode) {
				ServiceNode sn = (ServiceNode) asn;
				if (iRef == sn.getImportRef())
					return sn;
			}
		}
		return null;
	}


	private long getServiceId(ServiceReference<?> ref) {
		return (Long) ref.getProperty(Constants.SERVICE_ID);
	}

	public void handleRSAEvent(final RemoteServiceAdminEvent event) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null) return;
				Throwable t = event.getException();
				if (t == null) {
					switch (event.getType()) {
					case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
						ExportReference eRef = event.getExportReference();
						ServiceNode sn = findServiceNode(getServiceId(eRef.getExportedService()));
						if (sn != null)
							sn.setExportRef(eRef);
						break;
					case RemoteServiceAdminEvent.EXPORT_UNREGISTRATION:
					case RemoteServiceAdminEvent.EXPORT_ERROR:
					case RemoteServiceAdminEvent.EXPORT_UPDATE:
					case RemoteServiceAdminEvent.EXPORT_WARNING:
						ServiceNode sn1 = findServiceNode(event.getExportReference());
						if (sn1 != null)
							sn1.setExportRef(null);
						break;
					case RemoteServiceAdminEvent.IMPORT_REGISTRATION:
						ImportReference iRef = event.getImportReference();
						ServiceNode sn2 = findServiceNode(getServiceId(iRef.getImportedService()));
						if (sn2 != null)
							sn2.setImportRef(iRef);
						break;
					case RemoteServiceAdminEvent.IMPORT_UNREGISTRATION:
					case RemoteServiceAdminEvent.IMPORT_ERROR:
					case RemoteServiceAdminEvent.IMPORT_UPDATE:
					case RemoteServiceAdminEvent.IMPORT_WARNING:
						ServiceNode sn3 = findServiceNode(event.getImportReference());
						if (sn3 != null)
							sn3.setImportRef(null);
						break;
					}
					tv.setExpandedState(getServicesRoot(), true);
					tv.refresh();
				}
			}
		});
	}

}
