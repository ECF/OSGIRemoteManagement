/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;

/**
 * @since 3.3
 */
public class FeaturesAdapterFactory implements IAdapterFactory {

	private FeaturesRootNodeWorkbenchAdapter featuresInstallerRootAdapter = new FeaturesRootNodeWorkbenchAdapter();
	private FeaturesNodeWorkbenchAdapter featuresInstallerAdapter = new FeaturesNodeWorkbenchAdapter();
	private FeatureNodeWorkbenchAdapter featureAdapter = new FeatureNodeWorkbenchAdapter();
	private RepositoryNodeWorkbenchAdapter repositoryAdapter = new RepositoryNodeWorkbenchAdapter();

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adapterType.isInstance(adaptableObject)) {
			return adaptableObject;
		}
		if (adapterType == IWorkbenchAdapter.class || adapterType == IWorkbenchAdapter2.class
				|| adapterType == IWorkbenchAdapter3.class) {
			return getWorkbenchElement(adaptableObject);
		}
		return null;
	}

	protected Object getWorkbenchElement(Object adaptableObject) {
		if (adaptableObject instanceof FeaturesRootNode)
			return featuresInstallerRootAdapter;
		if (adaptableObject instanceof FeaturesNode)
			return featuresInstallerAdapter;
		if (adaptableObject instanceof RepositoryNode)
			return repositoryAdapter;
		if (adaptableObject instanceof FeatureNode)
			return featureAdapter;
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class, IWorkbenchAdapter2.class, IWorkbenchAdapter3.class };
	}

}
