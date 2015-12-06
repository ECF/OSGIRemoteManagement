/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview.model;

import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.framework.Constants;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ImportReference;

public class ServiceNode extends AbstractServicesNode {

	private final long bundleId;
	private final long[] usingBundleIds;
	private Map<String, Object> properties;
	private ExportReference exportRef;
	private ImportReference importRef;

	public ServiceNode(long bundleId, long[] usingBundles, Map<String, Object> props, ExportReference eRef, ImportReference iRef) {
		this.bundleId = bundleId;
		this.usingBundleIds = usingBundles;
		this.properties = props;
		this.exportRef = eRef;
		this.importRef = iRef;
	}

	public ServiceNode(long bundleId, long[] usingBundles, Map<String, Object> props) {
		this(bundleId, usingBundles, props, null, null);
	}
	
	public void setProperties(Map<String, Object> updatedProperties) {
		this.properties = updatedProperties;
	}

	public long getServiceId() {
		return (Long) getProperties().get(Constants.SERVICE_ID);
	}

	public long getBundleId() {
		return this.bundleId;
	}

	public long[] getUsingBundleIds() {
		return this.usingBundleIds;
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public String[] getServiceInterfaces() {
		return (String[]) this.properties.get(Constants.OBJECTCLASS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class)
			return new ServicePropertySource(getProperties());
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public void setExportRef(ExportReference ref) {
		this.exportRef = ref;
		this.importRef = null;
	}

	public void setImportRef(ImportReference ref) {
		this.importRef = ref;
		this.exportRef = null;
	}

	public int getExportedImportedState() {
		if (this.exportRef == null) {
			if (this.importRef == null) return 0;
			else return 2;
		} else return 1;
	}

	public ExportReference getExportRef() {
		return this.exportRef;
	}
	
	public ImportReference getImportRef() {
		return this.importRef;
	}

}
