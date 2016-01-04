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
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteServiceManagerComponent;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerContentProvider;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerNode;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteServiceManagerRootNode;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.serviceview.ServicesView;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.RegisteringBundleIdNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServiceNode;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.ServicesContentProvider;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.UsingBundleIdsNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewSite;

public class RemoteServicesView extends ServicesView {

	public static final String VIEW_ID = "org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteServicesView";
	
	public RemoteServicesView() {
	}

	@Override
	protected ServicesContentProvider createContentProvider(IViewSite viewSite) {
		return new RemoteServiceManagerContentProvider(viewSite);
	}
	
	private IRemoteServiceListener rsListener = new IRemoteServiceListener() {
		@Override
		public void handleEvent(RemoteServiceEvent e) {
			int type = e.getType();
			RemoteServiceHolder h = e.getRemoteServiceHolder();
			if (type == RemoteServiceEvent.ADDED)
				addRemoteServiceManager((IServiceManagerAsync) h.getRemoteService(), h.getRemoteServiceReference());
			else if (type == RemoteServiceEvent.REMOVED)
				removeRemoteServiceManager(h.getRemoteServiceReference());
		}
		
	};
	
	@Override
	public void dispose() {
		RemoteServiceManagerComponent.getInstance().removeListener(rsListener);
		super.dispose();
	}

	RemoteServiceManagerRootNode getRootNode() {
		return (RemoteServiceManagerRootNode) getContentProvider().getServicesRoot();
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
		TreeViewer viewer = getTreeViewer();
		if (viewer == null) return;
		s.getServiceReferencesAsync().whenComplete((result,exception) -> {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (exception != null)
						handleExportException(rsRef, exception);
					else {
						RemoteServiceManagerNode managerNode = getRootNode().getServiceManagerNode(rsRef, s);
						managerNode.clearChildren();
						for(ServiceReferenceMTO srMTO: result) {
							long bundleId = srMTO.getBundle();
							long[] usingBundleIds = srMTO.getUsingBundles();
							ServiceNode sn = new ServiceNode(bundleId,usingBundleIds,srMTO.getProperties());
							sn.addChild(new RegisteringBundleIdNode(bundleId));
							sn.addChild(new UsingBundleIdsNode("Using Bundles", usingBundleIds));
							managerNode.addChild(sn);
						}
						viewer.expandToLevel(2);
						viewer.refresh();
					}
				}
			});
		});
		
	}
	protected void handleExportException(IRemoteServiceReference rsRef, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initializeServices() {
		Collection<RemoteServiceHolder> existing = RemoteServiceManagerComponent.getInstance().addListener(rsListener, IServiceManagerAsync.class);
		for(RemoteServiceHolder rh: existing) 
			addRemoteServiceManager((IServiceManagerAsync) rh.getRemoteService(), rh.getRemoteServiceReference());
	}

}
