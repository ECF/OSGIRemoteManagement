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

import org.eclipse.ecf.mgmt.rsa.EndpointDescriptionMTO;
import org.eclipse.ecf.mgmt.rsa.ImportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ImportRegistrationMTO;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ImportRegistrationNode;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.osgi.framework.Constants;

public class ImportRegistrationMTONode extends ImportRegistrationNode {

	private final ImportRegistrationMTO remoteImportRegistrationMTO;

	public ImportRegistrationMTONode(ImportRegistrationMTO eReg) {
		super(eReg.getException());
		this.remoteImportRegistrationMTO = eReg;
	}

	@Override
	public String getValidName() {
		Map<String, Object> props = getEndpointProperties();
		return (props == null) ? null
				: PropertyUtils.convertStringArrayToString((String[]) props.get(Constants.OBJECTCLASS));
	}

	Map<String, Object> getEndpointProperties() {
		ImportReferenceMTO ir = this.remoteImportRegistrationMTO.getImportReference();
		EndpointDescriptionMTO ed = (ir == null) ? null : ir.getImportedEndpoint();
		return (ed == null) ? null : ed.getProperties();
	}

}
