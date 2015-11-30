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

import org.eclipse.ecf.mgmt.rsa.ImportReferenceMTO;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.EndpointDescriptionRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ServiceIdNode;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.osgi.framework.Constants;

public class ImportReferenceMTONode extends AbstractRSANode {

	private ImportReferenceMTO importReference;
	private final String label;

	public ImportReferenceMTONode(ImportReferenceMTO ir) {
		this.importReference = ir;
		addChild(new ServiceIdNode(importReference.getImportedService(), "OSGi Proxy Service"));
		Map<String, Object> props = importReference.getImportedEndpoint().getProperties();
		this.label = PropertyUtils.convertStringArrayToString((String[]) props.get(Constants.OBJECTCLASS));
		addChild(new EndpointDescriptionRSANode(props));
	}

	public String getName() {
		return label;
	}
}
