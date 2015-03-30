/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.discovery.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.remoteserviceadmin.EndpointEvent;

public class EndpointDiscoveryView extends ViewPart {

	public static final String ID = "org.eclipse.ecf.mgmt.rsa.discovery.ui.views.EndpointDiscoveryView";

	private TreeViewer viewer;
	private Action startRSAAction;
	private Action copyValueAction;
	private Action importAction;
	private Clipboard clipboard;
	
	private DiscoveryComponent discovery;
	
	class ENode implements IAdaptable {
		private String name;
		private ParentENode parent;

		public ENode(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(ParentENode parent) {
			this.parent = parent;
		}

		public ParentENode getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
			return null;
		}
	}

	class ParentENode extends ENode {
		private ArrayList<ENode> children;

		public ParentENode(String name) {
			super(name);
			children = new ArrayList<ENode>();
		}

		public void addChild(ENode child) {
			children.add(child);
			child.setParent(this);
		}

		public void addChildAtIndex(int index, ENode child) {
			children.add(index, child);
			child.setParent(this);
		}
		
		public void removeChild(ENode child) {
			children.remove(child);
			child.setParent(null);
		}

		public ENode[] getChildren() {
			return (ENode[]) children.toArray(new ENode[children
					.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class NameValueParentNode extends ParentENode {
		private Object value;
		
		public NameValueParentNode(String name, Object value) {
			super(name);
			this.value = value;
		}
		
		public Object getValue() {
			return value;
		}
		
		public String toString() {
			return getName() + ": " + ((value == null) ? "" : value.toString());
		}
	}
	
	class ImportRegistrationNode extends ParentENode {
		
		private final static String IMPORTED_NAME = "IMPORTED";
		
		private final ImportRegistration importReg;
		
		public ImportRegistrationNode(ImportRegistration importRegistration) {
			super(IMPORTED_NAME);
			this.importReg = importRegistration;
		}
		
		public ImportRegistration getIR() {
			return importReg;
		}
	}
	
	class EDNode extends ParentENode {

		private final EndpointDescription ed;
		private ImportRegistrationNode irn;
		
		public EDNode(EndpointDescription ed) {
			this(ed,null);
		}

		public EDNode(EndpointDescription ed, ImportRegistration irn) {
			super(ed.getContainerID().getName()+":"+ed.getRemoteServiceId());
			this.ed = ed;
			setIRN(irn);
		}
		
		public void setIRN(ImportRegistration ir) {
			if (ir == null) {
				if (this.irn != null) removeChild(this.irn);
				this.irn = null;
			} else {
				this.irn = new ImportRegistrationNode(ir);
				addChildAtIndex(0,irn);
			}
		}
		
		public boolean equals(Object other) {
			if (other instanceof EDNode) {
				EDNode o = (EDNode) other;
				return ed.getId().equals(o.ed.getId());
			}
			return false;
		}

		public int hashCode() {
			return ed.getId().hashCode();
		}
		
		public EndpointDescription getEndpoint() {
			return ed;
		}
		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			if (adapter == IPropertySource.class) 
				return new EndpointDescriptionPropertySource(ed);
			return null;
		}
	}

	class EndpointDescriptionPropertySource implements IPropertySource {

		private final Map<String,Object> props;
		private final List<IPropertyDescriptor> descriptors;
		
		public EndpointDescriptionPropertySource(EndpointDescription ed) {
			this.props = ed.getProperties();
			descriptors = new ArrayList<IPropertyDescriptor>();
			for(String k: props.keySet()) 
				descriptors.add(new PropertyDescriptor(k,k));
		}

		@Override
		public Object getEditableValue() {
			return null;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
		}

		@Override
		public Object getPropertyValue(Object id) {
			Object val = props.get(id);
			if (val != null) {
				if (val instanceof String) return val;
				else if (val instanceof String[]) return convertArrayToString((String[]) val);
				else if (val != null) return val.toString();
			}
			return null;
		}

		private String convertArrayToString(String[] val) {
			List<String> results = new ArrayList<String>();
			for(String s: val)
				results.add(s);
			return results.toString();
		}
		
		@Override
		public boolean isPropertySet(Object id) {
			return false;
		}

		@Override
		public void resetPropertyValue(Object id) {
		}

		@Override
		public void setPropertyValue(Object id, Object value) {
		}
		
	}
	class NameValueENode extends ENode {

		private Object value;

		public NameValueENode(String name, Object value) {
			super(name);
			this.value = value;
		}

		public Object getValue() {
			return value;
		}
		
		public String toString() {
			return getName() + ": " + ((value == null) ? "" : value.toString());
		}
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		
		private ParentENode invisibleRoot;
		private ParentENode root;

		public ParentENode getRoot() {
			return root;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof ENode) {
				return ((ENode) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof ParentENode) {
				return ((ParentENode) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof ParentENode)
				return ((ParentENode) parent).hasChildren();
			return false;
		}

		private void initialize() {
			invisibleRoot = new ParentENode("");
			root = new ParentENode("Discovered Endpoints");
			invisibleRoot.addChild(root);
			List<EndpointEvent> history = discovery.getHistory();
			if (history != null) 
				for(EndpointEvent e: history)
					doEndpointChanged(e);
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof ParentENode)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	public EndpointDiscoveryView() {
	}

	public void createPartControl(Composite parent) {
		this.discovery = DiscoveryComponent.getDefault();
		this.discovery.setView(this);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
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
		if (d != null) d.setView(null);
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
			if (e instanceof NameValueENode || e instanceof NameValueParentNode)
				manager.add(copyValueAction);
			else if (e instanceof EDNode) {
				EDNode edNode = (EDNode) e;
				if (edNode.irn == null)
					manager.add(importAction);
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
		startRSAAction.setToolTipText("Start RemoteServiceAdmin");
		startRSAAction.setEnabled(discovery.getRSA() == null);
		copyValueAction = new Action() {
			public void run() {
				Object o = ((ITreeSelection) viewer.getSelection())
						.getFirstElement();
				String data = (o instanceof NameValueENode) ? ((NameValueENode) o)
						.getValue().toString()
						: (o instanceof NameValueParentNode) ? ((NameValueParentNode) o)
								.getValue().toString() : null;
				if (data != null && data.length() > 0) {
					clipboard.setContents(new Object[] { data },
							new Transfer[] { TextTransfer.getInstance() });
				}
			}
		};
		copyValueAction.setText("Copy Value");
		copyValueAction.setToolTipText("Copy Value");
		copyValueAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		importAction = new Action() {
			public void run() {
				EDNode edNode = ((EDNode) ((ITreeSelection) viewer.getSelection())
						.getFirstElement());
				RemoteServiceAdmin rsa = discovery.getRSA();
				if (rsa == null)
					showMessage("RSA is null, so cannot complete import");
				else {
					// Do import
					ImportRegistration reg = (ImportRegistration) rsa
							.importService(edNode.ed);
					Throwable exception = reg.getException();
					if (exception != null)
						showMessage("RSA import failed with exception: "
								+ exception.getMessage());
					else {
						// Success!  Set registration
						// and refresh
						edNode.setIRN(reg);
						viewer.refresh();
					}
				}
			}
		};
		importAction.setText("Import Remote Service");
		importAction.setToolTipText("Import Remote Service into local framework");
	}

	void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Endpoint Discovery", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	void doEndpointChanged(EndpointEvent event) {
		ParentENode root = ((ViewContentProvider) viewer
				.getContentProvider()).getRoot();
		EndpointDescription ed = (EndpointDescription) event.getEndpoint();
		int type = event.getType();
		switch (type) {
		case EndpointEvent.ADDED:
			root.addChild(createEDNode(ed));
			break;
		case EndpointEvent.REMOVED:
			root.removeChild(new EDNode(ed));
			break;
		}
		viewer.refresh();
	}
	
	void handleEndpointChanged(EndpointEvent event) {
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				doEndpointChanged(event);
			}
		});
	}

	void removeEndpointDescription(ParentENode root,
			EndpointDescription ed) {
		root.removeChild(new EDNode(ed));
		viewer.refresh();
	}

	List<String> getStringArrayProperty(EndpointDescription ed, String prop) {
		Map<String,Object> props = ed.getProperties();
		Object r = props.get(prop);
		List<String> results = new ArrayList<String>();
		if (r == null) return results;
		else if (r instanceof String[]) {
			String[] vals = (String[]) r;
			for(String v: vals)
				results.add(v);
		}
		else if (r instanceof String) results.add((String) r);
		else results.add(r.toString());
		return results;
	}
	
	ImportRegistration findImportRegistration(EndpointDescription ed) {
		RemoteServiceAdmin rsa = discovery.getRSA();
		if (rsa == null) return null;
		List<ImportRegistration> iRegs = rsa.getImportedRegistrations();
		for(ImportRegistration ir: iRegs) {
			ImportReference importRef = (ImportReference) ir.getImportReference();
			if (importRef != null && ed.equals(importRef.getImportedEndpoint())) 
				return ir;
		}
		return null;
	}
	
	EDNode createEDNode(EndpointDescription ed) {
		EDNode edo = new EDNode(ed, findImportRegistration(ed));
		// ID
		edo.addChild(new NameValueENode("ID",ed.getId()));
		// Interfaces
		List<String> intfs = ed.getInterfaces();
		String intfName = (intfs.size() > 1) ? intfs.toString() : intfs.get(0);
		String intfLabel = (intfs.size() > 1) ? "Service Types":"Service Type";
		edo.addChild(new NameValueENode(intfLabel, intfName));
		// Async Interfaces (if present)
		List<String> aintfs = ed.getAsyncInterfaces();
		if (aintfs.size() > 0)
			edo.addChild(new NameValueENode("Async Service", aintfs));
		// Remote Service Host
		org.eclipse.ecf.core.identity.ID cID = ed.getContainerID();
		ParentENode idp = new NameValueParentNode("Host: ",cID.getName());
		Namespace ns = cID.getNamespace();
		// Host children
		idp.addChild(new NameValueENode("NS Name", ns.getName()));
		idp.addChild(new NameValueENode("RS Id", ed
				.getRemoteServiceId()));
		// Remote Intents Supported
		List<String> edIntents = getStringArrayProperty(ed,Constants.REMOTE_INTENTS_SUPPORTED);
		if (edIntents.size() > 0) {
			ParentENode intents = new ParentENode("Intents");
			for (String i : edIntents)
				intents.addChild(new ENode(i));
			idp.addChild(intents);
		}
		// Remote Configs Supported
		List<String> secs = getStringArrayProperty(ed,Constants.REMOTE_CONFIGS_SUPPORTED);
		if (secs.size() > 0) {
			ParentENode scs = new ParentENode("Remote Configs");
			for (String i : secs)
				scs.addChild(new ENode(i));
			idp.addChild(scs);
		}
		edo.addChild(idp);
		// Framework UUID
		edo.addChild(new NameValueENode("Framework UUID",ed.getFrameworkUUID()));
		// Timestamp
		edo.addChild(new NameValueENode("Timestamp", ed.getTimestamp()));
		return edo;
	}
}