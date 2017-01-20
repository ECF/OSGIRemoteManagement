/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.mgmt.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.ServiceReferenceDTO;

public class ServiceReferenceMTO implements Serializable {

	public static final byte EXPORTED = 1;
	public static final byte IMPORTED = 2;
	public static final byte LOCAL = 0;

	private static final long serialVersionUID = -4088391130982105496L;

	private final long id;
	private final long bundle;
	private final Map<String, Object> properties;
	private final long[] usingBundles;
	private final int exportImportMode;

	public static ServiceReferenceMTO createMTO(ServiceReferenceDTO dto, int exportImportMode) {
		return new ServiceReferenceMTO(dto, exportImportMode);
	}

	private Map<String, Object> getMapFromProperties(ServiceReference<?> ref) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : ref.getPropertyKeys())
			result.put(key, ref.getProperty(key));
		return result;
	}

	public static ServiceReferenceMTO createMTO(ServiceReference<?> ref, int exportImportMode) {
		return new ServiceReferenceMTO(ref, exportImportMode);
	}

	public static ServiceReferenceMTO createMTO(ServiceReference<?> ref) {
		return createMTO(ref, LOCAL);
	}
	
	public static ServiceReferenceMTO createMTO(ServiceReferenceDTO dto) {
		return new ServiceReferenceMTO(dto, LOCAL);
	}

	public static long[] usingBundles(ServiceReference<?> ref) {
		Bundle[] using = ref.getUsingBundles();
		if (using == null)
			return new long[0];
		List<Bundle> bundles = Arrays.asList(using);
		long[] results = new long[bundles.size()];
		int i = 0;
		for (Iterator<Bundle> it = bundles.iterator(); it.hasNext(); i++)
			results[i] = it.next().getBundleId();
		return results;
	}

	@SuppressWarnings("unchecked")
	ServiceReferenceMTO(ServiceReference<?> ref, int exportImportMode) {
		this.id = (Long) ref.getProperty(Constants.SERVICE_ID);
		this.bundle = ref.getBundle().getBundleId();
		this.properties = (Map<String, Object>) PropertiesUtil.convertMapToSerializableMap(getMapFromProperties(ref));
		this.usingBundles = usingBundles(ref);
		this.exportImportMode = exportImportMode;
	}

	@SuppressWarnings("unchecked")
	ServiceReferenceMTO(ServiceReferenceDTO srDTO, int exportImportMode) {
		this.id = srDTO.id;
		this.bundle = srDTO.bundle;
		this.properties = (Map<String, Object>) PropertiesUtil.convertMapToSerializableMap(srDTO.properties);
		this.usingBundles = srDTO.usingBundles;
		this.exportImportMode = exportImportMode;
	}

	public long getId() {
		return id;
	}

	public long getBundle() {
		return bundle;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public long[] getUsingBundles() {
		return usingBundles;
	}

	public String[] getServices() {
		return (String[]) properties.get(Constants.OBJECTCLASS);
	}

	public int getRanking() {
		Integer ranking = (Integer) properties.get(Constants.SERVICE_RANKING);
		if (ranking == null)
			return 0;
		else
			return ranking.intValue();
	}

	@Override
	public String toString() {
		return "ServiceReferenceMTO [id=" + id + ", bundle=" + bundle + ", properties=" + properties + ", usingBundles="
				+ Arrays.toString(usingBundles) + ", exportImportMode=" + exportImportMode + "]";
	}

	public int getExportImportMode() {
		return exportImportMode;
	}

}
