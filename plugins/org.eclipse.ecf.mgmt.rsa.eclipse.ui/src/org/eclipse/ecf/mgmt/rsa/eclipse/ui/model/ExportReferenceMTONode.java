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

import org.eclipse.ecf.mgmt.rsa.ExportReferenceMTO;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.EndpointDescriptionRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ServiceIdNode;
import org.osgi.framework.Constants;

public class ExportReferenceMTONode extends AbstractRSANode {

	private ExportReferenceMTO exportReference;
	private String label;
	
	public ExportReferenceMTONode(ExportReferenceMTO er) {
		this.exportReference = er;
		addChild(new ServiceIdNode(exportReference.getExportedService(), "OSGi Service"));
		Map<String,Object> props = exportReference.getExportedEndpoint().getProperties();
		this.label = convertStringArrayToString(
				(String[]) props.get(Constants.OBJECTCLASS));
		addChild(new EndpointDescriptionRSANode(props));
	}

	public String getName() {
		return label;
	}
}
