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
import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.IBundleManagerAsync;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteBundleManagerComponent;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteServiceManagerComponent;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteBundleManagerContentProvider;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteBundleManagerNode;
import org.eclipse.ecf.mgmt.framework.eclipse.ui.services.model.RemoteBundleManagerRootNode;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.ui.bundleview.AbstractBundlesView;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.AbstractBundlesNode;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundleNode;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewSite;

public class RemoteBundlesView extends AbstractBundlesView {

	public static final String VIEW_ID = "org.eclipse.ecf.mgmt.framework.eclipse.ui.RemoteBundlesView";

	public RemoteBundlesView() {
	}

	private Action refreshAction;

	@Override
	protected void makeActions() {
		super.makeActions();
		refreshAction = new Action() {
			public void run() {
				RemoteBundleManagerNode node = getSelectedRSManagerNode();
				if (node != null)
					refresh(node);
			};
		};
		refreshAction.setText("Refresh remote OSGi services");
	}

	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
		ITreeSelection selection = (ITreeSelection) getTreeViewer().getSelection();
		if (selection != null) {
			Object e = selection.getFirstElement();
			if (e instanceof RemoteBundleManagerNode)
				manager.add(refreshAction);
		}
	}

	private RemoteBundleManagerNode getSelectedRSManagerNode() {
		AbstractBundlesNode aNode = getSelectedNode();
		return (aNode == null) ? null
				: (aNode instanceof RemoteBundleManagerNode) ? (RemoteBundleManagerNode) aNode : null;
	}

	@Override
	protected BundlesContentProvider createContentProvider(IViewSite viewSite) {
		return new RemoteBundleManagerContentProvider(viewSite);
	}

	private IRemoteServiceListener rsListener = new IRemoteServiceListener() {
		@Override
		public void handleEvent(RemoteServiceEvent e) {
			int type = e.getType();
			RemoteServiceHolder h = e.getRemoteServiceHolder();
			if (type == RemoteServiceEvent.ADDED)
				addRemoteBundleManager((IBundleManagerAsync) h.getRemoteService(), h.getRemoteServiceReference());
			else if (type == RemoteServiceEvent.REMOVED)
				removeRemoteServiceManager(h.getRemoteServiceReference());
		}

	};

	@Override
	public void dispose() {
		RemoteServiceManagerComponent.getInstance().removeListener(rsListener);
		super.dispose();
	}

	RemoteBundleManagerRootNode getRootNode() {
		return (RemoteBundleManagerRootNode) getContentProvider().getBundlesRoot();
	}

	protected void removeRemoteServiceManager(final IRemoteServiceReference rsRef) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				getRootNode().removeBundleManagerNode(rsRef);
				viewer.refresh();
			}
		});
	}

	void addRemoteBundleManager(final IBundleManagerAsync s, final IRemoteServiceReference rsRef) {
		updateRemoteBundleManager(s, rsRef, null);
	}

	private void updateRemoteBundleManager(final IBundleManagerAsync s, final IRemoteServiceReference rsRef,
			final RemoteBundleManagerNode node) {
		final TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		s.getBundlesAsync().whenComplete((result, exception) -> {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (exception != null)
						logAndShowError("Exception using remote service reference=" + rsRef, exception);
					else {
						RemoteBundleManagerNode managerNode = (node == null)
								? getRootNode().getBundleManagerNode(rsRef, s) : node;
						managerNode.clearChildren();
						for (BundleMTO bMTO : result) {
							BundleNode bn = createBundleNode(bMTO.getId(), bMTO.getLastModified(), bMTO.getState(),
									bMTO.getSymbolicName(), bMTO.getVersion(), bMTO.getManifest(), bMTO.getLocation());
							managerNode.addChild(bn);
						}
						viewer.expandToLevel(2);
						viewer.refresh();
					}
				}
			});
		});
	}

	void refresh(RemoteBundleManagerNode managerNode) {
		updateRemoteBundleManager(managerNode.getBundleManager(), managerNode.getBundleManagerRef(), managerNode);
	}

	@Override
	protected void initializeBundles() {
		Collection<RemoteServiceHolder> existing = RemoteBundleManagerComponent.getInstance().addListener(rsListener,
				IBundleManagerAsync.class);
		for (RemoteServiceHolder rh : existing)
			addRemoteBundleManager((IBundleManagerAsync) rh.getRemoteService(), rh.getRemoteServiceReference());
	}

	private RemoteBundleManagerNode findRemoteBundleManagerForBundleNode(BundleNode bn) {
		AbstractBundlesNode abn = bn.getParent();
		return (abn instanceof RemoteBundleManagerNode) ? (RemoteBundleManagerNode) abn : null;
	}

	@Override
	protected void stopBundlesAction(BundleNode[] bns) {
		if (bns.length > 0) {
			for (int i=0; i < bns.length; i++) {
				final boolean last = i == (bns.length -1);
				RemoteBundleManagerNode rbn = findRemoteBundleManagerForBundleNode(bns[0]);
				if (rbn != null)
				rbn.getBundleManager().stopAsync(bns[i].getId()).whenComplete((status, exception) -> {
					if (exception != null) {
						System.out.println("Remote transport error: "+exception.getMessage());
						exception.printStackTrace();
					} else if (!status.isOK()) {
						System.out.println("Remote error: " + status.getMessage());
						Throwable t = status.getException();
						if (t != null)
							t.printStackTrace();
					} else if (last) 
						refresh(rbn);
				});
			}
		}
	}
	
	@Override
	protected void startBundlesAction(BundleNode[] bns) {
		if (bns.length > 0) {
			for (int i=0; i < bns.length; i++) {
				final boolean last = i == (bns.length -1);
				RemoteBundleManagerNode rbn = findRemoteBundleManagerForBundleNode(bns[0]);
				if (rbn != null)
				rbn.getBundleManager().startAsync(bns[i].getId()).whenComplete((status, exception) -> {
					if (exception != null) {
						System.out.println("Remote error");
						exception.printStackTrace();
					} else if (!status.isOK()) {
						System.out.println("Remote status error: " + status.getMessage());
					} else if (last)
						refresh(rbn);
				});
			}
		}
	}

}
