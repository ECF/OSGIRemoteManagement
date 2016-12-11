/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.AbstractBundlesContentProvider;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.AbstractBundlesNode;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundleNode;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesContentProvider;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesRootNode;
import org.eclipse.ecf.remoteservice.ui.internal.bundleview.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

public abstract class AbstractBundlesView extends ViewPart {

	private TreeViewer viewer;
	private AbstractBundlesContentProvider contentProvider;
	private BundlesFilteredTree filteredTree;
	private Action stopBundleAction;
	private Action startBundleAction;
	private Action uninstallBundleAction;
	
	protected void fillContextMenu(IMenuManager manager) {
		Object first = getSelection().getFirstElement();
		if (first instanceof BundleNode) {
			BundleNode bn = (BundleNode) first;
			int bState = bn.getState();
			switch (bState) {
			case Bundle.ACTIVE:
				manager.add(stopBundleAction);
				manager.add(new Separator());
				manager.add(uninstallBundleAction);
				break;
			case Bundle.RESOLVED:
			case Bundle.STARTING:
				manager.add(startBundleAction);
				manager.add(new Separator());
				manager.add(uninstallBundleAction);
				break;
			}
		}
	}

	protected ITreeSelection getSelection() {
		return getTreeViewer().getStructuredSelection();
	}

	protected abstract void stopBundlesAction(BundleNode[] bns);

	protected abstract void startBundlesAction(BundleNode[] bns);

	protected abstract void uninstallBundlesAction(BundleNode[] array);

	protected void makeActions() {
		stopBundleAction = new Action() {
			public void run() {
				ITreeSelection selection = getSelection();
				if (selection.getFirstElement() instanceof BundleNode) {
					@SuppressWarnings("rawtypes")
					Iterator i = selection.iterator();
					List<BundleNode> bns = new ArrayList<BundleNode>();
					while (i.hasNext())
						bns.add((BundleNode) i.next());
					stopBundlesAction(bns.toArray(new BundleNode[bns.size()]));
				}
			}
		};
		stopBundleAction.setText("Stop Bundle");
		startBundleAction = new Action() {
			public void run() {
				ITreeSelection selection = getSelection();
				if (selection.getFirstElement() instanceof BundleNode) {
					@SuppressWarnings("rawtypes")
					Iterator i = selection.iterator();
					List<BundleNode> bns = new ArrayList<BundleNode>();
					while (i.hasNext())
						bns.add((BundleNode) i.next());
					startBundlesAction(bns.toArray(new BundleNode[bns.size()]));
				}
			}
		};
		startBundleAction.setText("Start Bundle");
		uninstallBundleAction = new Action() {
			public void run() {
				ITreeSelection selection = getSelection();
				if (selection.getFirstElement() instanceof BundleNode) {
					@SuppressWarnings("rawtypes")
					Iterator i = selection.iterator();
					List<BundleNode> bns = new ArrayList<BundleNode>();
					while (i.hasNext())
						bns.add((BundleNode) i.next());
					uninstallBundlesAction(bns.toArray(new BundleNode[bns.size()]));
				}
			}
		};
		uninstallBundleAction.setText("Uninstall Bundle");
	}

	protected void log(int level, String message, Throwable e) {
		Activator.getDefault().getLog().log(new Status(level, Activator.PLUGIN_ID, message, e));
	}

	protected void logWarning(String message, Throwable e) {
		log(IStatus.WARNING, message, e);
	}

	protected void logError(String message, Throwable e) {
		log(IStatus.ERROR, message, e);
	}

	protected void logAndShowError(String message, Throwable exception) {
		logError(message, exception);
		MessageDialog.openInformation(viewer.getControl().getShell(), "Error", message
				+ ((exception != null) ? "\nException: " + exception.getMessage() + "\nSee Error Log for stack" : ""));
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AbstractBundlesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.contentProvider = createContentProvider(getViewSite());

		this.viewer = createTreeViewer(composite);

		makeActions();
		hookContextMenu();

		initializeBundles();
	}

	@Override
	public void setFocus() {
		FilteredTree filteredTree = getFilteredTree();
		if (filteredTree != null) {
			Text filterText = filteredTree.getFilterControl();
			if (filterText != null)
				filterText.setFocus();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		this.viewer = null;
		this.contentProvider = null;
	}

	protected TreeViewer getTreeViewer() {
		return viewer;
	}

	protected FilteredTree getFilteredTree() {
		return filteredTree;
	}

	protected TreeViewer createTreeViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		filteredTree = createFilteredTree(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI, new PatternFilter());

		TreeViewer viewer = filteredTree.getViewer();

		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.setInput(getViewSite());

		viewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof BundleNode && e2 instanceof BundleNode) {
					return new Long(((BundleNode) e1).getId() - ((BundleNode) e2).getId()).intValue();
				}
				return super.compare(viewer, e1, e2);
			}
		});

		getViewSite().setSelectionProvider(viewer);
		return viewer;
	}

	protected BundlesFilteredTree createFilteredTree(Composite parent, int options, PatternFilter filter) {
		BundlesFilteredTree result = new BundlesFilteredTree(this, parent, options, filter);
		result.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gd = new GridData(GridData.FILL_BOTH);
		result.setLayoutData(gd);
		return result;
	}

	protected AbstractBundlesContentProvider getContentProvider() {
		return contentProvider;
	}

	protected BundlesContentProvider createContentProvider(IViewSite viewSite) {
		return new BundlesContentProvider(viewSite);
	}

	protected void initializeBundles() {
		// do nothing;
	}

	protected BundleNode findBundleNode(long bundleId) {
		AbstractBundlesNode[] bundles = getBundlesRoot().getChildren();
		for (AbstractBundlesNode abn : bundles) {
			if (abn instanceof BundleNode) {
				BundleNode sn = (BundleNode) abn;
				if (bundleId == sn.getId())
					return sn;
			}
		}
		return null;
	}

	protected BundlesRootNode getBundlesRoot() {
		return getContentProvider().getBundlesRoot();
	}

	protected Tree getUndisposedTree() {
		if (viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed())
			return null;
		return viewer.getTree();
	}

	protected String getTitleSummary() {
		Tree tree = getUndisposedTree();
		String type = "bundles";
		int total = getBundlesRoot().getChildren().length;
		if (tree == null)
			return NLS.bind("Filter matched {0} of {1} {2}.", (new String[] { "0", "0", type })); //$NON-NLS-1$ //$NON-NLS-2$
		return NLS.bind("Filter matched {0} of {1} {2}.",
				(new String[] { Integer.toString(tree.getItemCount()), Integer.toString(total), type }));
	}

	protected void updateTitle() {
		setContentDescription(getTitleSummary());
	}

	protected AbstractBundlesNode getSelectedNode() {
		return ((AbstractBundlesNode) ((ITreeSelection) viewer.getSelection()).getFirstElement());
	}

	protected BundleNode createBundleNode(long id, long lastModified, int state, String symbolicName, String version,
			Map<String, String> manifest, String location) {
		return new BundleNode(id, lastModified, state, symbolicName, version, manifest, location);
	}

	protected void addBundleNodes(final Collection<BundleNode> bns) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null)
					return;
				BundlesRootNode brn = getBundlesRoot();
				for (BundleNode bn : bns)
					brn.addChild(bn);
				tv.setExpandedState(brn, true);
				tv.refresh();
			}
		});
	}

}
