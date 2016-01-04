/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview;

import java.util.Collection;
import java.util.Map;

import org.eclipse.ecf.remoteservice.ui.services.IServicesView;
import org.eclipse.ecf.remoteservice.ui.serviceview.model.AbstractServicesContentProvider;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

public abstract class AbstractServicesView extends ViewPart implements IServicesView {

	private TreeViewer viewer;
	private AbstractServicesContentProvider contentProvider;
	private ServicesFilteredTree filteredTree;

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.contentProvider = createContentProvider(getViewSite());
		
		this.viewer = createTreeViewer(composite);
		
		initializeServices();
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

		filteredTree = createFilteredTree(composite, SWT.H_SCROLL | SWT.V_SCROLL, new PatternFilter());

		TreeViewer viewer = filteredTree.getViewer();

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
		return viewer;
	}
	
	protected ServicesFilteredTree createFilteredTree(Composite parent, int options, PatternFilter filter) {
		ServicesFilteredTree result = new ServicesFilteredTree(this, parent, options, filter);
		result.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gd = new GridData(GridData.FILL_BOTH);
		result.setLayoutData(gd);
		return result;
	}
	
	protected AbstractServicesContentProvider getContentProvider() {
		return contentProvider;
	}
	
	protected ServicesContentProvider createContentProvider(IViewSite viewSite) {
		return new ServicesContentProvider(viewSite);
	}
	
	protected void initializeServices() {
		// do nothing;
	}
	
	protected ServiceNode findServiceNode(long serviceId) {
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

	protected ServicesRootNode getServicesRoot() {
		return getContentProvider().getServicesRoot();
	}

	protected Tree getUndisposedTree() {
		if (viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed())
			return null;
		return viewer.getTree();
	}

	protected String getTitleSummary() {
		Tree tree = getUndisposedTree();
		String type = "services";
		int total = getServicesRoot().getChildren().length;
		if (tree == null)
			return NLS.bind("Filter matched {0} of {1} {2}.", (new String[] { "0", "0", type })); //$NON-NLS-1$ //$NON-NLS-2$
		return NLS.bind("Filter matched {0} of {1} {2}.",
				(new String[] { Integer.toString(tree.getItemCount()), Integer.toString(total), type }));
	}

	protected void updateTitle() {
		setContentDescription(getTitleSummary());
	}
	
	protected AbstractServicesNode getSelectedNode() {
		return ((AbstractServicesNode) ((ITreeSelection) viewer.getSelection()).getFirstElement());
	}

	@Override
	public void selectService(final String remoteId, final long serviceId) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null)
					return;
				ServiceNode sn = findServiceNode(serviceId);
				if (sn != null)
					tv.setSelection(new StructuredSelection(sn));
			}
		});
	}

	@Override
	public String getRemoteId() {
		// We are interested in the local services view, which means the
		// remote id is null
		return null;
	}

	protected ServiceNode createServiceNode(long serviceId, long bundleId, long[] usingBundleIds, Map<String, Object> properties) {
		ServiceNode result = new ServiceNode(bundleId, usingBundleIds, properties);
		result.addChild(new RegisteringBundleIdNode(bundleId));
		result.addChild(new UsingBundleIdsNode("Using Bundles", usingBundleIds));
		return result;
	}
	
	protected void addServiceNodes(final Collection<ServiceNode> sns) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null) return;
				ServicesRootNode srn = getServicesRoot();
				for (ServiceNode sn : sns)
					srn.addChild(sn);
				tv.setExpandedState(getServicesRoot(), true);
				tv.refresh();
			}
		});
	}

}
