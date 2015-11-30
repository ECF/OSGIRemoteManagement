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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.remoteservice.ui.internal.serviceview.DiscoveryComponent;
import org.eclipse.ecf.remoteservice.ui.services.IServicesView;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.RegisteringBundleIdNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesContentProvider;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesRootNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.UsingBundleIdsNode;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
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
public class ServicesView extends ViewPart implements IServicesView {

	public static final String ID_VIEW = "org.eclipse.ecf.remoteservice.ui.serviceview.ServiceView"; //$NON-NLS-1$

	private FilteredTree fFilteredTree;
	private TreeViewer viewer;
	private ServicesContentProvider contentProvider;

	public ServicesView() {
	}

	@Override
	public void dispose() {
		DiscoveryComponent discovery = DiscoveryComponent.getDefault();
		BundleContext ctxt = discovery.getContext();
		if (ctxt != null)
			ctxt.removeServiceListener(serviceListener);
		discovery.setServicesView(null);
		viewer = null;
		contentProvider = null;
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
			final TreeViewer v = viewer;
			if (v == null)
				return;
			v.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (viewer == null)
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
					viewer.setExpandedState(getServicesRoot(), true);
					viewer.refresh();
				}
			});
		}
	};

	@Override
	public String getRemoteId() {
		// We are interested in the local services view, which means the
		// remote id is null
		return null;
	}

	@Override
	public void selectService(final String remoteId, final long serviceId) {
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (viewer == null)
					return;
				ServiceNode sn = findServiceNode(serviceId);
				if (sn != null)
					viewer.setSelection(new StructuredSelection(sn));
			}
		});
	}

	public void updateTitle() {
		setContentDescription(getTitleSummary());
	}

	private Tree getUndisposedTree() {
		if (viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed())
			return null;
		return viewer.getTree();
	}

	private String getTitleSummary() {
		Tree tree = getUndisposedTree();
		String type = "services";
		int total = getServicesRoot().getChildren().length;
		if (tree == null)
			return NLS.bind("Filter matched {0} of {1} {2}.", (new String[] { "0", "0", type })); //$NON-NLS-1$ //$NON-NLS-2$
		return NLS.bind("Filter matched {0} of {1} {2}.",
				(new String[] { Integer.toString(tree.getItemCount()), Integer.toString(total), type }));
	}

	AbstractServicesNode getSelectedNode() {
		return ((AbstractServicesNode) ((ITreeSelection) viewer.getSelection()).getFirstElement());
	}

	@Override
	public void createPartControl(Composite parent) {
		// create the sash form that will contain the tree viewer & text viewer
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeViewer(composite);

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

	private void createTreeViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		fFilteredTree = new ServicesFilteredTree(this, composite, SWT.H_SCROLL | SWT.V_SCROLL, new PatternFilter());
		fFilteredTree.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gd = new GridData(GridData.FILL_BOTH);
		fFilteredTree.setLayoutData(gd);
		viewer = fFilteredTree.getViewer();

		contentProvider = new ServicesContentProvider(getViewSite());

		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setUseHashlookup(true);

		viewer.setInput(getViewSite());

		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof ServiceNode && e2 instanceof ServiceNode) {
					return new Long(((ServiceNode) e2).getServiceId() - ((ServiceNode) e1).getServiceId()).intValue();
				}
				return super.compare(viewer, e1, e2);
			}
		});

		getViewSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		Text filterText = fFilteredTree.getFilterControl();
		if (filterText != null) 
			filterText.setFocus();
	}

	private ServiceNode createServiceNode(long serviceId, long bundleId, long[] usingBundleIds,
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

	private ServiceNode findServiceNode(long serviceId) {
		AbstractServicesNode[] services = getServicesRoot().getChildren();
		for (AbstractServicesNode asn : services) {
			if (asn instanceof ServiceNode) {
				ServiceNode sn = (ServiceNode) asn;
				if (serviceId == sn.getServiceId())
					return sn;
			}
		}
		return null;
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

	private ServicesRootNode getServicesRoot() {
		return ((ServicesContentProvider) contentProvider).getServicesRoot();
	}

	private void addServiceNodes(final Collection<ServiceNode> sns) {
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ServicesRootNode srn = getServicesRoot();
				for (ServiceNode sn : sns)
					srn.addChild(sn);
				viewer.setExpandedState(getServicesRoot(), true);
				viewer.refresh();
			}
		});
	}

	private long getServiceId(ServiceReference<?> ref) {
		return (Long) ref.getProperty(Constants.SERVICE_ID);
	}

	public void handleRSAEvent(final RemoteServiceAdminEvent event) {
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
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
					viewer.setExpandedState(getServicesRoot(), true);
					viewer.refresh();
				}
			}
		});
	}

}
