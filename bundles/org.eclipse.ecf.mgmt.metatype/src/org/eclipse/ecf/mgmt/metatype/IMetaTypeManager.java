/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.metatype;

public interface IMetaTypeManager {

	MetaTypeInformationMTO getMetaTypeInformation(long bundleId);
	
	ObjectClassDefinitionMTO getObjectClassDefinition(long bundleId, String ocdId, int filter);
	
	byte[] getIconBytes(long bundleId, String ocdId, int size);
}
