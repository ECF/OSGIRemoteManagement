/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSAWorkbenchAdapter;
import org.eclipse.ecf.remoteservices.ui.RSAImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 */
public class ExportReferenceMTONodeWorkbenchAdapter extends AbstractRSAWorkbenchAdapter {

	@Override
	public String getLabel(Object object) {
		return ((ExportReferenceMTONode) object).getName();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return RSAImageRegistry.RS_OBJ;
	}
}
