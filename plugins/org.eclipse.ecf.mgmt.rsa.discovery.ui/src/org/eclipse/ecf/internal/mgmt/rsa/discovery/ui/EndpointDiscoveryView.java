/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.mgmt.rsa.discovery.ui;

import java.util.List;

import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.AbstractEndpointNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointAsyncInterfacesNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointConfigTypesNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointConnectTargetIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointFrameworkIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointGroupNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointIntentsNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointInterfacesNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointNamespaceNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointPackageVersionNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointPropertyGroupNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointPropertyNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointRemoteServiceFilterNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointRemoteServiceIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointServiceIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointTimestampNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.ImportRegistrationNode;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleException;
import org.osgi.service.remoteserviceadmin.EndpointEvent;

public class EndpointDiscoveryView extends ViewPart {

	public static final String ID = "org.eclipse.ecf.mgmt.rsa.discovery.ui.model.views.EndpointDiscoveryView";

	private TreeViewer viewer;
	private Action startRSAAction;
	private Action copyValueAction;
	private Action copyNameAction;
	private Action importAction;
	private Action unimportAction;
	private Clipboard clipboard;

	private DiscoveryComponent discovery;

	public EndpointDiscoveryView() {
	}

	class EndpointContentProvider extends BaseWorkbenchContentProvider {

		EndpointGroupNode invisibleRoot;
		EndpointGroupNode root;

		public EndpointGroupNode getRoot() {
			return root;
		}

		void initialize() {
			invisibleRoot = new EndpointGroupNode("");
			root = new EndpointGroupNode("Discovered Endpoints");
			invisibleRoot.addChild(root);
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
	}

	public void createPartControl(Composite parent) {
		this.discovery = DiscoveryComponent.getDefault();
		this.discovery.setView(this);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new EndpointContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		// setup clipboard
		clipboard = new Clipboard(viewer.getControl().getDisplay());
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void dispose() {
		viewer = null;
		if (discovery != null) {
			discovery.setView(null);
		}
		DiscoveryComponent d = DiscoveryComponent.getDefault();
		if (d != null)
			d.setView(null);
		super.dispose();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				EndpointDiscoveryView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(startRSAAction);
		bars.getToolBarManager().add(startRSAAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		ITreeSelection selection = (ITreeSelection) viewer.getSelection();
		if (selection != null) {
			Object e = selection.getFirstElement();
			if (e instanceof EndpointPropertyNode) {
				manager.add(copyNameAction);
				manager.add(copyValueAction);
			} else if (e instanceof EndpointNode) {
				EndpointNode edNode = (EndpointNode) e;
				ImportRegistration ir = edNode.getImportRegistration();
				if (ir == null)
					manager.add(importAction);
				else
					manager.add(unimportAction);
			}
		}
	}

	private void makeActions() {
		startRSAAction = new Action() {
			public void run() {
				DiscoveryComponent d = DiscoveryComponent.getDefault();
				if (d != null)
					try {
						d.startRSA();
						startRSAAction.setEnabled(false);
					} catch (BundleException e) {
						// TODO Auto-generated catch block
						// Need to show error message dialog (at least)
						// if this fails
						e.printStackTrace();
					}
			}
		};
		startRSAAction.setText("Start RSA");
		startRSAAction.setToolTipText("Start RemoteServiceAdmin Service");
		startRSAAction.setEnabled(discovery.getRSA() == null);

		copyValueAction = new Action() {
			public void run() {
				Object o = ((ITreeSelection) viewer.getSelection())
						.getFirstElement();
				String data = ((EndpointPropertyNode) o).getPropertyValue()
						.toString();
				if (data != null && data.length() > 0) {
					clipboard.setContents(new Object[] { data },
							new Transfer[] { TextTransfer.getInstance() });
				}
			}
		};
		copyValueAction.setText("Copy Property Value");
		copyValueAction.setToolTipText("Copy Property Value");
		copyValueAction.setImageDescriptor(RSAImageRegistry.DESC_PROPERTY_OBJ);

		copyNameAction = new Action() {
			public void run() {
				Object o = ((ITreeSelection) viewer.getSelection())
						.getFirstElement();
				String data = ((EndpointPropertyNode) o).getPropertyName();
				if (data != null && data.length() > 0) {
					clipboard.setContents(new Object[] { data },
							new Transfer[] { TextTransfer.getInstance() });
				}
			}
		};
		copyNameAction.setText("Copy Property Name");
		copyNameAction.setToolTipText("Copy Property Name");
		copyNameAction.setImageDescriptor(RSAImageRegistry.DESC_PROPERTY_OBJ);

		importAction = new Action() {
			public void run() {
				EndpointNode edNode = getEDNodeSelected();
				RemoteServiceAdmin rsa = discovery.getRSA();
				if (rsa == null)
					showMessage("RSA is null, so cannot import");
				else {
					// Do import
					ImportRegistration reg = (ImportRegistration) rsa
							.importService(edNode.getEndpointDescription());
					// Check if import exception
					Throwable exception = reg.getException();
					if (exception != null)
						showMessage("RSA import failed with exception: "
								+ exception.getMessage());
					else {
						// Success! Set registration
						// and refresh
						edNode.setImportReg(new ImportRegistrationNode(reg));
						viewer.refresh();
					}
				}
			}
		};
		importAction.setText("Import Remote Service");
		importAction
				.setToolTipText("Import Remote Service into local framework");

		unimportAction = new Action() {
			public void run() {
				EndpointNode edNode = getEDNodeSelected();
				ImportRegistration ir = edNode.getImportRegistration();
				if (ir == null)
					return;
				try {
					ir.close();
					edNode.setImportReg(null);
					viewer.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		unimportAction.setText("Close Imported Remote Service");
		unimportAction
				.setToolTipText("Close the Previously-Imported Remote Service");
	}

	EndpointNode getEDNodeSelected() {
		return ((EndpointNode) ((ITreeSelection) viewer.getSelection())
				.getFirstElement());
	}

	void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"EndpointDescriptionNode Discovery", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	void handleEndpointChanged(EndpointEvent event) {
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				AbstractEndpointNode root = ((EndpointContentProvider) viewer
						.getContentProvider()).getRoot();
				EndpointDescription ed = (EndpointDescription) event
						.getEndpoint();
				int type = event.getType();
				switch (type) {
				case EndpointEvent.ADDED:
					root.addChild(createEndpointDescriptionNode(ed));
					break;
				case EndpointEvent.REMOVED:
					root.removeChild(new EndpointNode(ed));
					break;
				}
				viewer.refresh();
			}
		});
	}

	ImportRegistration findImportRegistration(EndpointDescription ed) {
		RemoteServiceAdmin rsa = discovery.getRSA();
		if (rsa == null)
			return null;
		List<ImportRegistration> iRegs = rsa.getImportedRegistrations();
		for (ImportRegistration ir : iRegs) {
			ImportReference importRef = (ImportReference) ir
					.getImportReference();
			if (importRef != null && ed.equals(importRef.getImportedEndpoint()))
				return ir;
		}
		return null;
	}

	EndpointNode createEndpointDescriptionNode(EndpointDescription ed) {
		EndpointNode edo = new EndpointNode(
				ed,
				new org.eclipse.ecf.mgmt.rsa.discovery.ui.model.ImportRegistrationNode(
						findImportRegistration(ed)));
		
		// Interfaces
		EndpointInterfacesNode ein = new EndpointInterfacesNode();
		for(String intf: ed.getInterfaces())
			ein.addChild(new EndpointPackageVersionNode(EndpointNode.getPackageName(intf)));
		edo.addChild(ein);
		// Async Interfaces (if present)
		List<String> aintfs = ed.getAsyncInterfaces();
		if (aintfs.size() > 0) {
			EndpointAsyncInterfacesNode ain = new EndpointAsyncInterfacesNode();
			for(String intf: ed.getAsyncInterfaces())
				ain.addChild(new EndpointPackageVersionNode(EndpointNode.getPackageName(intf)));
			edo.addChild(ain);
		}
		// ID
		edo.addChild(new EndpointIDNode());
		// Remote Service Host
		EndpointPropertyGroupNode idp = new EndpointPropertyGroupNode(
				"Remote Host");
		// Host children
		idp.addChild(new EndpointNamespaceNode());
		idp.addChild(new EndpointRemoteServiceIDNode());
		org.eclipse.ecf.core.identity.ID connectTarget = ed
				.getConnectTargetID();
		if (connectTarget != null)
			idp.addChild(new EndpointConnectTargetIDNode());
		idp.addChild(new EndpointServiceIDNode());
		idp.addChild(new EndpointIntentsNode());
		idp.addChild(new EndpointConfigTypesNode());
		idp.addChild(new EndpointFrameworkIDNode());
		idp.addChild(new EndpointTimestampNode());
		String filter = ed.getRemoteServiceFilter();
		if (filter != null)
			idp.addChild(new EndpointRemoteServiceFilterNode());
		edo.addChild(idp);
		return edo;
	}

}