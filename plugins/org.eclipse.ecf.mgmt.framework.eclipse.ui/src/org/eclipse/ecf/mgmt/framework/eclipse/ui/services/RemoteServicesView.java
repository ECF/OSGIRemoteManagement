/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui.services;

import java.util.Collection;

import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceListener;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceEvent;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceHolder;
import org.eclipse.ecf.mgmt.framework.IServiceManagerAsync;
import org.eclipse.ecf.mgmt.framework.ServiceEventMTO;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.IServiceEventHandlerDelegate;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteServiceManagerComponent;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.ServiceEventHandler;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerContentProvider;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerNode;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerRootNode;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceNode;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.serviceview.AbstractServicesView;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesContentProvider;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.RegisteringBundleIdNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesContentProvider;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.UsingBundleIdsNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewSite;
import org.osgi.framework.ServiceEvent;

public class RemoteServicesView extends AbstractServicesView {

	public static final String VIEW_ID = "org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteServicesView";

	public RemoteServicesView() {
	}

	private Action refreshAction;

	@Override
	protected void makeActions() {
		refreshAction = new Action() {
			public void run() {
				RemoteServiceManagerNode node = getSelectedRSManagerNode();
				if (node != null)
					refresh(node);
			};
		};
		refreshAction.setText("Refresh remote OSGi services");
	}

	protected void fillContextMenu(IMenuManager manager) {
		ITreeSelection selection = (ITreeSelection) getTreeViewer().getSelection();
		if (selection != null) {
			Object e = selection.getFirstElement();
			if (e instanceof RemoteServiceManagerNode)
				manager.add(refreshAction);
		}
	}

	private RemoteServiceManagerNode getSelectedRSManagerNode() {
		AbstractServicesNode aNode = getSelectedNode();
		return (aNode == null) ? null
				: (aNode instanceof RemoteServiceManagerNode) ? (RemoteServiceManagerNode) aNode : null;
	}

	@Override
	protected ServicesContentProvider createContentProvider(IViewSite viewSite) {
		return new RemoteServiceManagerContentProvider(viewSite);
	}

	private IRemoteServiceListener rsListener = new IRemoteServiceListener() {
		@Override
		public void handleEvent(RemoteServiceEvent e) {
			int type = e.getType();
			RemoteServiceHolder<IServiceManagerAsync> h = e.getRemoteServiceHolder(IServiceManagerAsync.class);
			if (type == RemoteServiceEvent.ADDED) {
				IRemoteServiceReference ref = h.getRemoteServiceReference();
				addRemoteServiceManager(h.getRemoteService(), ref);
				ServiceEventHandler.addDelegate(ref.getID(), new ServiceEventHandlerDelegate());
			} else if (type == RemoteServiceEvent.REMOVED) {
				IRemoteServiceReference ref = h.getRemoteServiceReference();
				removeRemoteServiceManager(ref);
				ServiceEventHandler.removeDelegates();
			}
		}
	};

	@Override
	public void dispose() {
		ServiceEventHandler.removeDelegates();
		RemoteServiceManagerComponent.getInstance().removeListener(rsListener);
		super.dispose();
	}

	RemoteServiceManagerRootNode getRootNode() {
		AbstractServicesContentProvider c = getContentProvider();
		return (c == null)?null:(RemoteServiceManagerRootNode) c.getServicesRoot();
	}

	protected void removeRemoteServiceManager(final IRemoteServiceReference rsRef) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				getRootNode().removeServiceManagerNode(rsRef);
				viewer.refresh();
			}
		});
	}

	void addRemoteServiceManager(final IServiceManagerAsync s, final IRemoteServiceReference rsRef) {
		updateRemoteServiceManager(s, rsRef, null);
	}

	private void updateRemoteServiceManager(final IServiceManagerAsync s, final IRemoteServiceReference rsRef,
			final RemoteServiceManagerNode node) {
		final TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		s.getServiceReferencesAsync().whenComplete((result, exception) -> {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (exception != null)
						logAndShowError("Exception using remote service reference=" + rsRef, exception);
					else {
						RemoteServiceManagerNode managerNode = (node == null)
								? getRootNode().getServiceManagerNode(rsRef, s) : node;
						managerNode.clearChildren();
						for (ServiceReferenceMTO srMTO : result)
							managerNode.addChild(createServiceNode(srMTO));
						viewer.expandToLevel(2);
						viewer.refresh();
					}
				}
			});
		});
	}

	ServiceNode createServiceNode(ServiceReferenceMTO srMTO) {
		long bundleId = srMTO.getBundle();
		long[] usingBundleIds = srMTO.getUsingBundles();
		ServiceNode sn = new RemoteServiceNode(bundleId, usingBundleIds, srMTO.getProperties(),srMTO.getExportImportMode());
		sn.addChild(new RegisteringBundleIdNode(bundleId));
		sn.addChild(new UsingBundleIdsNode("Using Bundles", usingBundleIds));
		return sn;
	}

	void refresh(RemoteServiceManagerNode rsManagerNode) {
		updateRemoteServiceManager(rsManagerNode.getRemoteServiceManager(),
				rsManagerNode.getRemoteServiceReference(), rsManagerNode);
	}

	class ServiceEventHandlerDelegate implements IServiceEventHandlerDelegate {
		@Override
		public void handleServiceEvent(final IRemoteServiceID rsID, final ServiceEventMTO serviceEvent) {
			final TreeViewer v = getTreeViewer();
			if (v != null && !v.getControl().isDisposed()) {
				v.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						RemoteServiceManagerRootNode rootNode = getRootNode();
						if (rootNode == null)
							return;
						RemoteServiceManagerNode managerNode = rootNode.getServiceManagerNode(rsID);
						if (managerNode != null) {
							ServiceReferenceMTO srMTO = serviceEvent.getServiceReferenceMTO();
							ServiceNode serviceNode = managerNode.getServiceNode(srMTO.getId());
							if (serviceNode != null)
								// remove old one
								managerNode.removeChild(serviceNode);
							if (serviceEvent.getType() != ServiceEvent.UNREGISTERING)
								// Create new one and add
								managerNode.addChild(createServiceNode(srMTO));
							v.expandToLevel(2);
							v.refresh();
						}
					}
				});
			}
		}
	}

	@Override
	protected void initializeServices() {
		Collection<RemoteServiceHolder<IServiceManagerAsync>> existing = RemoteServiceManagerComponent.getInstance()
				.addListener(rsListener, IServiceManagerAsync.class);
		for (RemoteServiceHolder<IServiceManagerAsync> rh : existing) {
			IRemoteServiceReference ref = rh.getRemoteServiceReference();
			addRemoteServiceManager(rh.getRemoteService(), ref);
			ServiceEventHandler.addDelegate(ref.getID(), new ServiceEventHandlerDelegate());
		}
	}

}
