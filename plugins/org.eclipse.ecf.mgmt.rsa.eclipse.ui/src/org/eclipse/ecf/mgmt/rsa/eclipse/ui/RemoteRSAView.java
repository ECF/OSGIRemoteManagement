/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui;

import java.util.Collection;

import org.eclipse.ecf.mgmt.rsa.ExportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ExportRegistrationMTO;
import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManagerAsync;
import org.eclipse.ecf.mgmt.rsa.ImportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ImportRegistrationMTO;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.model.ExportReferenceMTONode;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.model.ImportReferenceMTONode;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.model.RSAManagerContentProvider;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.model.RSAManagerNode;
import org.eclipse.ecf.mgmt.rsa.eclipse.ui.model.RemoteRSAManagersRootNode;
import org.eclipse.ecf.mgmt.rsa.internal.eclipse.ui.RSAManagerComponent;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceListener;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceEvent;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceHolder;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.AbstractRemoteServiceAdminView;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportedServicesRootNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ImportedEndpointsRootNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IViewSite;

public class RemoteRSAView extends AbstractRemoteServiceAdminView {

	public static final String ID_VIEW = "org.eclipse.ecf.mgmt.eclipse.ui.RemoteRSAView"; //$NON-NLS-1$

	private Action refreshExportedAction;
	private Action refreshImportedAction;
	private Action refreshBothAction;

	@Override
	protected void makeActions() {
		super.makeActions();
		refreshExportedAction = new Action() {
			public void run() {
				AbstractRSANode rsaNode = getSelectedNode();
				RSAManagerNode managerNode = null;
				if (rsaNode instanceof ExportedServicesRootNode)
					managerNode = (RSAManagerNode) rsaNode.getParent();
				if (managerNode != null)
					refreshExports(managerNode.getRemoteServiceAdminManager(),
							managerNode.getRemoteServiceAdminManagerRef());
			};
		};
		refreshExportedAction.setText("Refresh remote exported services");
		refreshImportedAction = new Action() {
			public void run() {
				AbstractRSANode rsaNode = getSelectedNode();
				RSAManagerNode managerNode = null;
				if (rsaNode instanceof ImportedEndpointsRootNode)
					managerNode = (RSAManagerNode) rsaNode.getParent();
				if (managerNode != null)
					refreshImports(managerNode.getRemoteServiceAdminManager(),
							managerNode.getRemoteServiceAdminManagerRef());
			};
		};
		refreshImportedAction.setText("Refresh remote imported endpoints");
		refreshBothAction = new Action() {
			public void run() {
				RSAManagerNode managerNode = getSelectedManagerNode();
				if (managerNode != null)
					refreshBoth(managerNode.getRemoteServiceAdminManager(),
							managerNode.getRemoteServiceAdminManagerRef());
			};
		};
		refreshBothAction.setText("Refresh remote exports and imports");
	}

	@Override
	protected void fillContextMenu(IMenuManager manager) {
		ITreeSelection selection = (ITreeSelection) viewer.getSelection();
		if (selection != null) {
			Object e = selection.getFirstElement();
			if (e instanceof RSAManagerNode) {
				manager.add(refreshBothAction);
			} else if (e instanceof ExportReferenceMTONode || e instanceof ExportedServicesRootNode) {
				manager.add(refreshExportedAction);
			} else if (e instanceof ImportReferenceMTONode || e instanceof ImportedEndpointsRootNode) {
				manager.add(refreshImportedAction);
			}
		}
	}

	private RSAManagerNode getSelectedManagerNode() {
		AbstractRSANode aNode = getSelectedNode();
		return (aNode == null) ? null : (aNode instanceof RSAManagerNode) ? (RSAManagerNode) aNode : null;
	}

	@Override
	protected RSAManagerContentProvider createContentProvider(IViewSite viewSite) {
		return new RSAManagerContentProvider(viewSite);
	}

	private IRemoteServiceListener rsListener = new IRemoteServiceListener() {
		@Override
		public void handleEvent(RemoteServiceEvent e) {
			int type = e.getType();
			RemoteServiceHolder h = e.getRemoteServiceHolder();
			if (type == RemoteServiceEvent.ADDED) 
				refreshBoth((IRemoteServiceAdminManagerAsync) h.getRemoteService(), h.getRemoteServiceReference());
			else if (type == RemoteServiceEvent.REMOVED) {
				if (viewer == null)
					return;
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer == null)
							return;
						getRootNode().removeRSAManagerNode(h.getRemoteServiceReference());
						viewer.refresh();
					}
				});				
			}
		}
	};
	
	@Override
	public void dispose() {
		RSAManagerComponent.getInstance().removeListener(rsListener);
		super.dispose();
	}

	private Collection<RemoteServiceHolder> initialRemoteServiceHolders;
	
	@Override
	protected void setupListeners() {
		initialRemoteServiceHolders = RSAManagerComponent.getInstance().addListener(rsListener, IRemoteServiceAdminManagerAsync.class);
	}

	@Override
	protected void updateModel() {
		if (initialRemoteServiceHolders != null) {
			for(RemoteServiceHolder h: initialRemoteServiceHolders) 
				refreshBoth((IRemoteServiceAdminManagerAsync) h.getRemoteService(), h.getRemoteServiceReference());
			initialRemoteServiceHolders = null;
		}
		super.updateModel();
	}
	private void refreshExports(IRemoteServiceAdminManagerAsync rsaManagerAsync, IRemoteServiceReference rsRef) {
		if (viewer == null)
			return;
		if (rsRef != null) {
			rsaManagerAsync.getExportedServicesAsync().whenComplete((result, exception) -> {
				if (viewer == null)
					return;
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer == null)
							return;
						if (exception != null)
							handleExportException(rsRef, exception);
						else {
							RSAManagerNode managerNode = getRootNode().getRSAManagerNode(rsRef, rsaManagerAsync);
							ExportedServicesRootNode exportedRoot = managerNode.getExportedServicesRootNode();
							exportedRoot.clearChildren();
							for (ExportRegistrationMTO ereg : result) {
								Throwable t = ereg.getException();
								if (t == null) {
									ExportReferenceMTO eref = ereg.getExportReference();
									if (eref != null)
										exportedRoot.addChild(new ExportReferenceMTONode(eref));
								}
							}
							viewer.expandToLevel(4);
							viewer.refresh();
						}
					}
				});
			});
		}
	}

	private void refreshImports(IRemoteServiceAdminManagerAsync rsaManagerAsync, IRemoteServiceReference rsRef) {
		rsaManagerAsync.getImportedEndpointsAsync().whenComplete((result, exception) -> {
			if (viewer == null)
				return;
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (viewer == null)
						return;
					if (exception != null)
						handleImportException(rsRef, exception);
					else {
						RSAManagerNode managerNode = getRootNode().getRSAManagerNode(rsRef, rsaManagerAsync);
						ImportedEndpointsRootNode importedRoot = managerNode.getImportedEndpointsRootNode();
						importedRoot.clearChildren();
						for (ImportRegistrationMTO ireg : result) {
							Throwable t = ireg.getException();
							if (t == null) {
								ImportReferenceMTO iref = ireg.getImportReference();
								if (iref != null)
									importedRoot.addChild(new ImportReferenceMTONode(iref));
							}
						}
						viewer.expandToLevel(4);
						viewer.refresh();
					}
				}
			});
		});
	}

	protected void handleImportException(IRemoteServiceReference rsRef, Throwable exception) {
		// TODO Auto-generated method stub
		System.out.println("handleImportException. rsRef=" + rsRef + ";exception=" + exception);
		if (exception != null)
			exception.printStackTrace();
	}

	RemoteRSAManagersRootNode getRootNode() {
		return ((RSAManagerContentProvider) contentProvider).getRoot();
	}

	private void handleExportException(IRemoteServiceReference rsRef, Throwable exception) {
		// TODO Auto-generated method stub
		System.out.println("handleExportException. rsRef=" + rsRef + ";exception=" + exception);
		if (exception != null)
			exception.printStackTrace();
	}

	private void refreshBoth(IRemoteServiceAdminManagerAsync rsaManager, IRemoteServiceReference rsRef) {
		refreshImports(rsaManager, rsRef);
		refreshExports(rsaManager, rsRef);
	}

}
