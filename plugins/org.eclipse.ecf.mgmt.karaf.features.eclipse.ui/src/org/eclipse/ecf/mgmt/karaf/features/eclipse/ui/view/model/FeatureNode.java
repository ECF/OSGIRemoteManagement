/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.mgmt.karaf.features.FeatureMTO;
import org.eclipse.ui.views.properties.IPropertySource;

public class FeatureNode extends AbstractFeaturesNode {

	private FeatureMTO featureMTO;

	public FeatureNode(FeatureMTO mto) {
		this.featureMTO = mto;
	}

	public String getId() {
		return featureMTO.getId();
	}

	public String getName() {
		return featureMTO.getName();
	}

	public String getNamespace() {
		return featureMTO.getNamespace();
	}

	public String getVersion() {
		return featureMTO.getVersion();
	}

	public String getDescription() {
		return featureMTO.getDescription();
	}

	public String getDetails() {
		return featureMTO.getDetails();
	}

	public boolean hasVersion() {
		return featureMTO.hasVersion();
	}

	public boolean isHidden() {
		return featureMTO.isHidden();
	}

	public boolean isInstalled() {
		return featureMTO.isInstalled();
	}

	private Map<String, String> getFeatureProperties() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("Id", getId());
		result.put("Name", getName());
		result.put("Installed", String.valueOf(isInstalled()));
		result.put("Namespace", getNamespace());
		result.put("Version", getVersion());
		result.put("Description", getDescription());
		result.put("Details", getDetails());
		result.put("hasVersion", String.valueOf(hasVersion()));
		result.put("isHidden", String.valueOf(isHidden()));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class)
			return new FeaturePropertySource(getFeatureProperties());
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
