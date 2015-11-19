/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

/**
 * @since 3.3
 */
public class RSAManagerAdapterFactory extends org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.RSAAdapterFactory {

	private RSAManagerWorkbenchAdapter rsaManagerAdapter = new RSAManagerWorkbenchAdapter();
	private ExportReferenceMTONodeWorkbenchAdapter exportReferenceAdapter = new ExportReferenceMTONodeWorkbenchAdapter();
	private ImportReferenceMTONodeWorkbenchAdapter importReferenceAdapter = new ImportReferenceMTONodeWorkbenchAdapter();

	protected Object getWorkbenchElement(Object adaptableObject) {
		if (adaptableObject instanceof RSAManagerNode)
			return rsaManagerAdapter;
		if (adaptableObject instanceof ExportReferenceMTONode)
			return exportReferenceAdapter;
		if (adaptableObject instanceof ImportReferenceMTONode)
			return importReferenceAdapter;
		return super.getWorkbenchElement(adaptableObject);
	}

}
