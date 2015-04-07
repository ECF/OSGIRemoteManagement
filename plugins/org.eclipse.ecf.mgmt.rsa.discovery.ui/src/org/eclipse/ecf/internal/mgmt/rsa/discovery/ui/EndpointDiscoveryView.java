/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.mgmt.rsa.discovery.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.AbstractEndpointNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointAsyncInterfacesNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointConfigTypesNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointConnectTargetIDNode;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointContentProvider;
import org.eclipse.ecf.mgmt.rsa.discovery.ui.model.EndpointFrameworkIDNode;
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
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescriptionReader;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.IEndpointDescriptionLocator;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
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

	private Action undiscoverAction;

	private Action edefDiscoverAction;

	private Clipboard clipboard;

	private DiscoveryComponent discovery;

	private EndpointContentProvider contentProvider;

	public EndpointDiscoveryView() {
	}

	public void createPartControl(Composite parent) {
		this.discovery = DiscoveryComponent.getDefault();
		this.discovery.setView(this);

		IViewSite viewSite = getViewSite();
		this.contentProvider = new EndpointContentProvider(viewSite,
				"Discovered Endpoints");

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(this.contentProvider);
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(viewSite);

		makeActions();
		hookContextMenu();
		contributeToActionBars();
		// setup clipboard
		clipboard = new Clipboard(viewer.getControl().getDisplay());
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void dispose() {
		super.dispose();
		viewer = null;
		contentProvider = null;
		if (discovery != null) {
			discovery.setView(null);
			discovery = null;
		}
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
		bars.getMenuManager().add(edefDiscoverAction);
		bars.getToolBarManager().add(startRSAAction);
		bars.getToolBarManager().add(edefDiscoverAction);
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
				if (ir == null) {
					manager.add(importAction);
					manager.add(undiscoverAction);
				} else
					manager.add(unimportAction);
			}
		}
	}

	private void logError(String message, Throwable e) {
		RSAPlugin
				.getDefault()
				.getLog()
				.log(new Status(IStatus.ERROR, RSAPlugin.PLUGIN_ID, message, e));
	}

	private void makeActions() {
		startRSAAction = new Action() {
			public void run() {
				if (discovery != null)
					try {
						discovery.startRSA();
						startRSAAction.setEnabled(false);
					} catch (BundleException e) {
						logError("RSA start failed", e);
						showMessage("RSA Start failed with exception: "
								+ e.getMessage()
								+ ".  See Error Log for details.");
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
				if (edNode != null) {
					RemoteServiceAdmin rsa = discovery.getRSA();
					if (rsa == null)
						showMessage("RSA is null, so cannot import");
					else {
						// Do import
						EndpointDescription ed = edNode
								.getEndpointDescription();
						ImportRegistration reg = (ImportRegistration) rsa
								.importService(ed);
						// Check if import exception
						Throwable exception = reg.getException();
						if (exception != null) {
							logError("RSA importService failed", exception);
							showMessage("RSA importService failed with exception: "
									+ exception.getMessage()
									+ ".  See Error Log for details.");
						} else {
							// Success! Set registration
							// and refresh
							edNode.setImportReg(new ImportRegistrationNode(reg));
							viewer.refresh();
						}
					}
				}
			}
		};
		importAction.setText("Import Remote Service");
		importAction
				.setToolTipText("Import Remote Service into local framework");
		importAction.setImageDescriptor(RSAImageRegistry.DESC_RSPROXY_CO);

		unimportAction = new Action() {
			public void run() {
				EndpointNode edNode = getEDNodeSelected();
				if (edNode != null) {
					ImportRegistration ir = edNode.getImportRegistration();
					if (ir == null)
						return;
					try {
						ir.close();
						edNode.setImportReg(null);
						viewer.refresh();
					} catch (Exception e) {
						logError("Cannote close import registration", e);
						showMessage("Cannot close import registration exception: "
								+ e.getMessage()
								+ ".  See Error Log for details.");
					}
				}
			}
		};
		unimportAction.setText("Close Imported Remote Service");
		unimportAction
				.setToolTipText("Close the Previously-Imported Remote Service");

		edefDiscoverAction = new Action() {
			public void run() {
				IEndpointDescriptionLocator locator = discovery
						.getEndpointDescriptionLocator();
				if (locator != null) {
					FileDialog dialog = new FileDialog(viewer.getControl()
							.getShell(), SWT.OPEN);
					dialog.setFilterExtensions(new String[] { "*.xml" });
					dialog.setText("Open EDEF File");
					dialog.setFilterPath(null);
					String result = dialog.open();
					if (result != null)
						try {
							EndpointDescription[] eds = (EndpointDescription[]) new EndpointDescriptionReader()
									.readEndpointDescriptions(new FileInputStream(
											result));
							if (eds != null) {
								for (int i = 0; i < eds.length; i++)
									locator.discoverEndpoint(eds[i]);
							}
						} catch (IOException e) {
							logError("Endpoint description parsing failed", e);
							showMessage("Endpoint description parsing failed with exception "
									+ e.getMessage()
									+ ".  See Error Log for details.");
						}
				}
			}
		};
		edefDiscoverAction.setText("Open EDEF File...");
		edefDiscoverAction
				.setToolTipText("Discover Endpoints by reading EDEF file");
		edefDiscoverAction.setEnabled(discovery.getRSA() != null);
		edefDiscoverAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

		undiscoverAction = new Action() {
			public void run() {
				EndpointNode endpoint = getEDNodeSelected();
				if (endpoint != null
						&& endpoint.getImportRegistration() == null) {
					IEndpointDescriptionLocator l = discovery
							.getEndpointDescriptionLocator();
					if (l != null
							&& MessageDialog
									.openQuestion(viewer.getControl()
											.getShell(), "Remove Endpoint",
											"Are you sure you want to remove this endpoint?"))
						l.undiscoverEndpoint(endpoint.getEndpointDescription());

				}
			}
		};
		undiscoverAction.setText("Remove/Undiscover Endpoint");
		undiscoverAction.setToolTipText("Remove this endpoint");
	}

	EndpointNode getEDNodeSelected() {
		AbstractEndpointNode aen = getNodeSelected();
		return (aen instanceof EndpointNode) ? (EndpointNode) aen : null;
	}

	boolean isRootSelected() {
		return contentProvider.getRootNode().equals(getNodeSelected());
	}

	AbstractEndpointNode getNodeSelected() {
		return ((AbstractEndpointNode) ((ITreeSelection) viewer.getSelection())
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
				AbstractEndpointNode root = contentProvider.getRootNode();
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
		for (String intf : ed.getInterfaces())
			ein.addChild(new EndpointPackageVersionNode(EndpointNode
					.getPackageName(intf)));
		edo.addChild(ein);
		// Async Interfaces (if present)
		List<String> aintfs = ed.getAsyncInterfaces();
		if (aintfs.size() > 0) {
			EndpointAsyncInterfacesNode ain = new EndpointAsyncInterfacesNode();
			for (String intf : ed.getAsyncInterfaces())
				ain.addChild(new EndpointPackageVersionNode(EndpointNode
						.getPackageName(intf)));
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