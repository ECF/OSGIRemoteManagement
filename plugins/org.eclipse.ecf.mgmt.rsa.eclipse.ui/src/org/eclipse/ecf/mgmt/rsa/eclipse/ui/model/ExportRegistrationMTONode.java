/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.mgmt.rsa.EndpointDescriptionMTO;
import org.eclipse.ecf.mgmt.rsa.ExportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ExportRegistrationMTO;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointPropertySource;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportRegistrationNode;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.framework.Constants;

public class ExportRegistrationMTONode extends ExportRegistrationNode {

	private final ExportRegistrationMTO remoteExportRegistrationMTO;

	public ExportRegistrationMTONode(ExportRegistrationMTO eReg) {
		super(eReg.getException());
		this.remoteExportRegistrationMTO = eReg;
	}

	@Override
	public boolean isClosed() {
		return hasError();
	}

	@Override
	public String getValidName() {
		Map<String, Object> props = getEndpointProperties();
		return (props == null) ? null
				: PropertyUtils.convertStringArrayToString((String[]) props.get(Constants.OBJECTCLASS));
	}

	Map<String, Object> getEndpointProperties() {
		ExportReferenceMTO er = this.remoteExportRegistrationMTO.getExportReference();
		EndpointDescriptionMTO ed = (er == null) ? null : er.getExportedEndpoint();
		return (ed == null) ? null : ed.getProperties();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class) {
			Map<String, Object> props = getEndpointProperties();
			if (props != null)
				return new EndpointPropertySource(props);
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
