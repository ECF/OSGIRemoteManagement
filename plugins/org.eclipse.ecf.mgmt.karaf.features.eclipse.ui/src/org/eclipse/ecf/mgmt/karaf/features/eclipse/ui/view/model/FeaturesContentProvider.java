package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.ui.IViewSite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

public class FeaturesContentProvider extends BaseWorkbenchContentProvider {

	private IViewSite viewSite;
	private final FeaturesRootNode root;

	public FeaturesContentProvider(IViewSite viewSite) {
		this.viewSite = viewSite;
		this.root = new FeaturesRootNode(""); //$NON-NLS-1$
	}

	protected IViewSite getViewSite() {
		return this.viewSite;
	}

	protected FeaturesRootNode getInvisibleRoot() {
		return this.root;
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(viewSite)) {
			return getChildren(getInvisibleRoot());
		}
		return getChildren(parent);
	}

	public FeaturesRootNode getKarafFeaturesInstallerRoot() {
		return getInvisibleRoot();
	}


}
