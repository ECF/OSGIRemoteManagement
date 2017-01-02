/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceListener;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceEvent;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceHolder;
import org.eclipse.ecf.mgmt.karaf.features.FeatureEventMTO;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManagerAsync;
import org.eclipse.ecf.mgmt.karaf.features.FeatureMTO;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryEventMTO;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryMTO;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.Activator;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.FeaturesFilteredTree;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.FeaturesInstallerHandler;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.KarafFeaturesListener;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.RemoteKarafFeaturesInstaller;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.AbstractFeaturesNode;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.FeatureNode;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.FeaturesContentProvider;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.FeaturesNode;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.FeaturesRootNode;
import org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model.RepositoryNode;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

public class FeaturesInstallerView extends ViewPart {

	public static final String ID_VIEW = "org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.FeaturesInstallerView"; //$NON-NLS-1$

	private TreeViewer viewer;
	private FeaturesContentProvider contentProvider;
	private FeaturesFilteredTree filteredTree;

	private Action refreshAction;
	private Action disconnectAction;
	private Action addRepoAction;
	private Action removeRepoAction;
	private Action installFeatureAction;
	private Action uninstallFeatureAction;

	protected void fillContextMenu(IMenuManager manager) {
		ITreeSelection selection = (ITreeSelection) getTreeViewer().getSelection();
		if (selection != null) {
			Object e = selection.getFirstElement();
			if (e instanceof FeaturesNode) {
				manager.add(addRepoAction);
				manager.add(new Separator());
				manager.add(refreshAction);
				manager.add(disconnectAction);
			} else if (e instanceof RepositoryNode) {
				manager.add(removeRepoAction);
			} else if (e instanceof FeatureNode) {
				FeatureNode fn = (FeatureNode) e;
				if (!fn.isInstalled())
					manager.add(installFeatureAction);
				else
					manager.add(uninstallFeatureAction);
			}
		}
	}

	protected void makeActions() {
		refreshAction = new Action() {
			public void run() {
				FeaturesNode node = getSelectedFeaturesNode();
				if (node != null)
					refresh(node);
			};
		};
		refreshAction.setText("Refresh");
		disconnectAction = new Action() {
			public void run() {
				FeaturesNode node = getSelectedFeaturesNode();
				if (node != null)
					disconnect(node);
			}
		};
		disconnectAction.setText("Disconnect");
		addRepoAction = new Action() {
			public void run() {
				FeaturesNode node = getSelectedFeaturesNode();
				if (node != null)
					addRepo(node);
			}
		};
		addRepoAction.setText("Add Repo...");
		removeRepoAction = new Action() {
			public void run() {
				RepositoryNode node = getSelectedRepositoryNode();
				if (node != null)
					removeRepo(node);
			}
		};
		removeRepoAction.setText("Remove Repo...");
		installFeatureAction = new Action() {
			public void run() {
				FeatureNode node = getSelectedFeatureNode();
				if (node != null)
					installFeature(node);
			}
		};
		installFeatureAction.setText("Install...");
		uninstallFeatureAction = new Action() {
			public void run() {
				FeatureNode node = getSelectedFeatureNode();
				if (node != null)
					uninstallFeature(node);
			}
		};
		uninstallFeatureAction.setText("Uninstall...");
	}

	void installFeature(FeatureNode node) {
		final FeaturesNode managerNode = (FeaturesNode) node.getParent().getParent();
		if (MessageDialog.openConfirm(viewer.getControl().getShell(), "Install Feature",
				"Install feature '" + node.getId() + "'?")) {
			managerNode.getKarafFeaturesInstaller().installFeatureAsync(node.getName(), node.getVersion())
					.whenComplete((v, exception) -> {
						if (exception != null)
							logAndShowError("Remote Karaf Feature Installer could not install feature " + node.getId(),
									exception);
					});
		}
	}

	void uninstallFeature(FeatureNode node) {
		final FeaturesNode managerNode = (FeaturesNode) node.getParent().getParent();
		if (MessageDialog.openConfirm(viewer.getControl().getShell(), "Uninstall Feature",
				"Uninstall feature '" + node.getId() + "'?")) {
			managerNode.getKarafFeaturesInstaller().uninstallFeatureAsync(node.getName(), node.getVersion())
					.whenComplete((v, exception) -> {
						if (exception != null)
							logAndShowError(
									"Remote Karaf Feature Installer could not uninstall feature " + node.getId(),
									exception);
					});
		}
	}

	void removeRepo(final RepositoryNode repoNode) {
		final FeaturesNode managerNode = (FeaturesNode) repoNode.getParent();
		if (MessageDialog.openConfirm(viewer.getControl().getShell(), "Remove Repository",
				"Remove repository named '" + repoNode.getName() + "'?")) {
			managerNode.getKarafFeaturesInstaller().removeRepositoryAsync(repoNode.getUri(), false)
					.whenComplete((v, exception) -> {
						if (exception != null)
							logAndShowError("Remote Karaf Feature Installer could not remove repo " + repoNode.getUri(),
									exception);
					});
		}
	}

	void addRepo(final FeaturesNode managerNode) {
		InputDialog id = new InputDialog(viewer.getControl().getShell(), "Add Repository", "Enter repository URL", "",
				new IInputValidator() {
					@Override
					public String isValid(String newText) {
						if ("".equals(newText))
							return null;
						try {
							URI.create(newText);
						} catch (Exception e) {
							return "Malformed URI: " + e.getMessage();
						}
						return null;
					}
				});
		int i = id.open();
		if (i == Window.OK) {
			String value = id.getValue();
			if (!"".equals(value)) {
				if (!value.endsWith(".xml"))
					value = value + ".xml";
				final String uriString = value;
				URI u = null;
				try {
					u = URI.create(uriString);
				} catch (Exception e) {
					// should not happen
				}
				final URI uri = u;
				managerNode.getKarafFeaturesInstaller().addRepositoryAsync(uri).whenComplete((v, exception) -> {
					if (exception != null)
						logAndShowError("Remote Karaf Feature Installer could not add repo " + uri, exception);
				});
			}
		}
	}

	void disconnect(FeaturesNode managerNode) {
		IRemoteServiceReference rsRef = managerNode.getKarafFeaturesInstallerRef();
		IRemoteServiceID rsID = rsRef.getID();
		IContainer c = RemoteKarafFeaturesInstaller.getInstance().getContainerForID(rsID.getContainerID());
		if (c != null)
			try {
				c.disconnect();
			} catch (Exception e) {
				logAndShowError("Remote Karaf Feature Install could not connect using reference " + rsRef, e);
			}
	}

	private FeaturesNode getSelectedFeaturesNode() {
		AbstractFeaturesNode aNode = getSelectedNode();
		return (aNode == null) ? null : (aNode instanceof FeaturesNode) ? (FeaturesNode) aNode : null;
	}

	private RepositoryNode getSelectedRepositoryNode() {
		AbstractFeaturesNode aNode = getSelectedNode();
		return (aNode == null) ? null : (aNode instanceof RepositoryNode) ? (RepositoryNode) aNode : null;
	}

	private FeatureNode getSelectedFeatureNode() {
		AbstractFeaturesNode aNode = getSelectedNode();
		return (aNode == null) ? null : (aNode instanceof FeatureNode) ? (FeatureNode) aNode : null;
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
		final Throwable t = (exception instanceof InvocationTargetException) ? exception.getCause() : exception;
		if (t != null) {
			logError(message, t);
			if (viewer != null)
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (viewer != null) {
							MessageDialog.openInformation(viewer.getControl().getShell(),
									"Remote Karaf Feature Install Error",
									message + "\n\nSee Error Log for details and stack trace");
							try {
								FeaturesInstallerView.this.getSite().getWorkbenchWindow().getActivePage()
										.showView("org.eclipse.pde.runtime.LogView");
							} catch (Exception e) {
							}
						}
					}
				});
		}
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	class FeaturesInstallerHandlerDelegate implements FeaturesInstallerHandler {
		@Override
		public void handleFeatureEvent(IRemoteServiceID rsID, final FeatureEventMTO featureEvent) {
			runUi(new Runnable() {
				@Override
				public void run() {
					final TreeViewer v = getTreeViewer();
					if (v == null)
						return;
					FeaturesNode managerNode = getKarafFeaturesInstallerRoot().getFeaturesNode(rsID);
					if (managerNode != null) {
						FeatureMTO fMTO = featureEvent.getFeatureMTO();
						if (fMTO != null) {
							FeatureNode featureNode = managerNode.getFeatureNode(fMTO.getId());
							if (featureNode != null) {
								RepositoryNode rn = (RepositoryNode) featureNode.getParent();
								if (rn != null)
									rn.removeChild(featureNode);
								rn.addChild(new FeatureNode(fMTO));
							}
						}
					}
					v.refresh();
				}
			});
		}

		private void runUi(Runnable r) {
			final TreeViewer v = getTreeViewer();
			if (v != null && !v.getControl().isDisposed())
				v.getControl().getDisplay().asyncExec(r);

		}

		@Override
		public void handleRepoEvent(IRemoteServiceID rsID, RepositoryEventMTO repoEvent) {
			runUi(new Runnable() {
				@Override
				public void run() {
					final TreeViewer v = getTreeViewer();
					if (v == null)
						return;
					FeaturesNode managerNode = getKarafFeaturesInstallerRoot().getFeaturesNode(rsID);
					if (managerNode != null) {
						RepositoryMTO rMTO = repoEvent.getRepositoryMTO();
						if (rMTO != null) {
							RepositoryNode repoNode = managerNode.getRepositoryNode(rMTO.getUri());
							if (repoNode != null)
								managerNode.removeChild(repoNode);
							if (repoEvent.getType() != RepositoryEventMTO.REMOVED)
								managerNode.addChild(new RepositoryNode(rMTO));
						}
						v.refresh();
					}
				}
			});
		}
	};

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

		Collection<RemoteServiceHolder<FeatureInstallManagerAsync>> existing = RemoteKarafFeaturesInstaller
				.getInstance().getNotifier().addListener(rsListener, FeatureInstallManagerAsync.class);
		for (RemoteServiceHolder<FeatureInstallManagerAsync> rh : existing)
			addRemoteKarafFeaturesInstaller(rh.getRemoteService(), rh.getRemoteServiceReference());
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

	private IRemoteServiceListener rsListener = new IRemoteServiceListener() {
		@Override
		public void handleEvent(RemoteServiceEvent e) {
			int type = e.getType();
			RemoteServiceHolder<FeatureInstallManagerAsync> h = e
					.getRemoteServiceHolder(FeatureInstallManagerAsync.class);
			if (type == RemoteServiceEvent.ADDED)
				addRemoteKarafFeaturesInstaller(h.getRemoteService(), h.getRemoteServiceReference());
			else if (type == RemoteServiceEvent.REMOVED)
				removeRemoteKarafFeaturesInstaller(h.getRemoteServiceReference());
		}
	};

	void addRemoteKarafFeaturesInstaller(final FeatureInstallManagerAsync s, final IRemoteServiceReference rsRef) {
		KarafFeaturesListener.addDelegate(rsRef.getID(), new FeaturesInstallerHandlerDelegate());
		update(s, rsRef, null);
	}

	protected void removeRemoteKarafFeaturesInstaller(final IRemoteServiceReference rsRef) {
		KarafFeaturesListener.removeDelegate(rsRef.getID());
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer v = getTreeViewer();
				if (v == null)
					return;
				FeaturesRootNode frn = getKarafFeaturesInstallerRoot();
				if (frn != null) {
					frn.removeFeaturesInstallerNode(rsRef);
					v.refresh();
				}
			}
		});
	}

	private void update(final FeatureInstallManagerAsync s, final IRemoteServiceReference rsRef,
			final FeaturesNode node) {
		final TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;

		s.listRepositoriesAsync().whenComplete((result, exception) -> {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (exception != null)
						logAndShowError("Exception using remote service reference=" + rsRef, exception);
					else {
						FeaturesNode managerNode = (node == null)
								? getKarafFeaturesInstallerRoot().getFeaturesNode(rsRef, s) : node;
						managerNode.clearChildren();
						for (RepositoryMTO srMTO : result)
							managerNode.addChild(new RepositoryNode(srMTO));
						viewer.expandToLevel(2);
						viewer.refresh();
					}
				}
			});
		});
	}

	void refresh(FeaturesNode rsManagerNode) {
		update(rsManagerNode.getKarafFeaturesInstaller(), rsManagerNode.getKarafFeaturesInstallerRef(), rsManagerNode);
	}

	@Override
	public void dispose() {
		super.dispose();
		RemoteKarafFeaturesInstaller.getInstance().getNotifier().removeListener(rsListener);
		KarafFeaturesListener.removeDelegates();
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
				if (e1 instanceof FeatureNode && e2 instanceof FeatureNode)
					return ((FeatureNode) e1).getName().compareTo(((FeatureNode) e2).getName());
				if (e1 instanceof RepositoryNode && e2 instanceof RepositoryNode)
					return ((RepositoryNode) e1).getUri().compareTo(((RepositoryNode) e2).getUri());
				return 0;
			}
		});

		getViewSite().setSelectionProvider(viewer);
		return viewer;
	}

	FeaturesFilteredTree createFilteredTree(Composite parent, int options, PatternFilter filter) {
		FeaturesFilteredTree result = new FeaturesFilteredTree(this, parent, options, filter);
		result.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gd = new GridData(GridData.FILL_BOTH);
		result.setLayoutData(gd);
		return result;
	}

	protected FeaturesContentProvider getContentProvider() {
		return contentProvider;
	}

	protected FeaturesContentProvider createContentProvider(IViewSite viewSite) {
		return new FeaturesContentProvider(viewSite);
	}

	protected FeatureNode findFeatureNode(String featureId) {
		FeaturesRootNode frn = getKarafFeaturesInstallerRoot();
		if (frn == null)
			return null;
		AbstractFeaturesNode[] features = frn.getChildren();
		for (AbstractFeaturesNode afn : features) {
			if (afn instanceof FeatureNode) {
				FeatureNode sn = (FeatureNode) afn;
				if (featureId.equals(sn.getId()))
					return sn;
			}
		}
		return null;
	}

	protected FeaturesRootNode getKarafFeaturesInstallerRoot() {
		FeaturesContentProvider fcp = getContentProvider();
		if (fcp == null)
			return null;
		return fcp.getKarafFeaturesInstallerRoot();
	}

	protected Tree getUndisposedTree() {
		if (viewer == null || viewer.getTree() == null || viewer.getTree().isDisposed())
			return null;
		return viewer.getTree();
	}

	protected AbstractFeaturesNode getSelectedNode() {
		return ((AbstractFeaturesNode) ((ITreeSelection) viewer.getSelection()).getFirstElement());
	}

	public void selectFeature(final String remoteId, final String featureId) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null)
					return;
				FeatureNode sn = findFeatureNode(featureId);
				if (sn != null)
					tv.setSelection(new StructuredSelection(sn));
			}
		});
	}

	protected void addServiceNodes(final Collection<FeatureNode> sns) {
		TreeViewer viewer = getTreeViewer();
		if (viewer == null)
			return;
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeViewer tv = getTreeViewer();
				if (tv == null)
					return;
				FeaturesRootNode srn = getKarafFeaturesInstallerRoot();
				if (srn != null) {
					for (FeatureNode sn : sns)
						srn.addChild(sn);
					tv.setExpandedState(getKarafFeaturesInstallerRoot(), true);
					tv.refresh();
				}
			}
		});
	}

	public void updateTitle() {
		setContentDescription(getTitleSummary());
	}

	protected String getTitleSummary() {
		Tree tree = getUndisposedTree();
		String type = "services";
		FeaturesRootNode frn = getKarafFeaturesInstallerRoot();
		if (frn != null) {
			int total = frn.getChildren().length;
			if (tree == null)
				return NLS.bind("Filter matched {0} of {1} {2}.", (new String[] { "0", "0", type })); //$NON-NLS-1$ //$NON-NLS-2$
			return NLS.bind("Filter matched {0} of {1} {2}.",
					(new String[] { Integer.toString(tree.getItemCount()), Integer.toString(total), type }));
		} else
			return "";
	}

	private static final String REPOSITORY_URLS = "REPOSITORY_URLS";
	
	List<String> repos = new ArrayList<String>();
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento != null) {
			String s = memento.getString(REPOSITORY_URLS);
			if (s != null && !"".equals(s)) {
				String[] strs = s.split(",");
				for(String str: strs) 
					repos.add(str);
			}
		}
	}
	
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		StringBuffer buf = new StringBuffer();
		for(Iterator<String> i = repos.iterator(); i.hasNext(); ) {
			String s = i.next();
			buf.append(s);
			if (i.hasNext())
				buf.append(",");
		}
		memento.putString(REPOSITORY_URLS, buf.toString());
	}
}
