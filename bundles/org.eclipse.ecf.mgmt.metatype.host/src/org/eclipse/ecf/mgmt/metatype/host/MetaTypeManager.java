/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.metatype.host;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.metatype.AttributeDefinitionMTO;
import org.eclipse.ecf.mgmt.metatype.IMetaTypeManager;
import org.eclipse.ecf.mgmt.metatype.MetaTypeInformationMTO;
import org.eclipse.ecf.mgmt.metatype.ObjectClassDefinitionMTO;
import org.osgi.framework.Bundle;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

public class MetaTypeManager extends AbstractManager implements IMetaTypeManager {

	private MetaTypeService metaTypeService;
	
	protected void bindMetaTypeService(MetaTypeService mts) {
		this.metaTypeService = mts;
	}
	
	protected void unbindMetaTypeService(MetaTypeService mts) {
		this.metaTypeService = null;
	}
	
	protected MetaTypeService getMetatypeService() {
		return this.metaTypeService;
	}
	
	protected MetaTypeInformation getMetaTypeInformation0(long bundleId) {
		Bundle bundle = getBundle0(bundleId);
		if (bundle == null)
			return null;
		MetaTypeInformation mta = getMetatypeService().getMetaTypeInformation(bundle);
		if (mta == null)
			return null;
		return mta;
	}
	
	@Override
	public MetaTypeInformationMTO getMetaTypeInformation(long bundleId) {
		MetaTypeInformation mta = getMetaTypeInformation0(bundleId);
		if (mta == null)
			return null;
		return new MetaTypeInformationMTO(mta.getPids(),mta.getFactoryPids(),getBundle0(bundleId).getBundleId());
	}

	protected ObjectClassDefinition getObjectClassDefinition0(long bundleId, String ocdId) {
		MetaTypeInformation mta = getMetaTypeInformation0(bundleId);
		if (mta == null)
			return null;
		return mta.getObjectClassDefinition(ocdId, null);
	}
	
	@Override
	public ObjectClassDefinitionMTO getObjectClassDefinition(long bundleId, String ocdId, int filter) {
		ObjectClassDefinition ocd = getObjectClassDefinition0(bundleId, ocdId);
		if (ocd == null)
			return null;
		AttributeDefinition[] attributeDefinitions = ocd.getAttributeDefinitions(filter);
		if (attributeDefinitions == null)
			return null;
		List<AttributeDefinitionMTO> mtos = new ArrayList<AttributeDefinitionMTO>();
		for(AttributeDefinition ad: attributeDefinitions) 
			mtos.add(new AttributeDefinitionMTO(ad.getName(),ad.getID(),ad.getCardinality(),ad.getType(),ad.getOptionValues(),ad.getOptionLabels(),ad.getDefaultValue()));
		return new ObjectClassDefinitionMTO(ocd.getName(),ocd.getID(),ocd.getDescription(),mtos.toArray(new AttributeDefinitionMTO[mtos.size()]));
	}

	@Override
	public byte[] getIconBytes(long bundleId, String ocdId, int size) {
		ObjectClassDefinition ocd = getObjectClassDefinition0(bundleId, ocdId);
		if (ocd == null)
			return null;
		try {
			InputStream ins = ocd.getIcon(size);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
			byte[] buffer = new byte[4096];
			int n;
			while ((n = ins.read(buffer)) > 0) 
			    bos.write(buffer, 0, n);
			ins.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			logError("Could not read icon", e);
			return null;
		}
	}

}
