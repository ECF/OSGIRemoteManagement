/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.scr.Reference;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ComponentReferenceMTO implements Serializable {

	private static final long serialVersionUID = 4461270131669598394L;

	public static ComponentReferenceMTO createMTO(Reference reference) {
		ServiceReference<?>[] srefs = reference.getServiceReferences();
		if (srefs == null)
			return null;
		long[] serviceReferenceIds = new long[srefs.length];
		for (int i = 0; i < srefs.length; i++)
			serviceReferenceIds[i] = ((Long) srefs[i].getProperty(Constants.SERVICE_ID)).longValue();

		return new ComponentReferenceMTO(reference.getName(), reference.getServiceName(), serviceReferenceIds,
				reference.isSatisfied(), reference.isOptional(), reference.isMultiple(), reference.isStatic(),
				reference.getTarget(), reference.getBindMethodName(), reference.getUnbindMethodName());
	}

	public static ComponentReferenceMTO[] createMTOs(Reference[] references) {
		List<ComponentReferenceMTO> results = new ArrayList<ComponentReferenceMTO>();
		for (Reference r : references)
			results.add(createMTO(r));
		return results.toArray(new ComponentReferenceMTO[results.size()]);
	}

	private final String name;
	private final String serviceName;
	private final long[] serviceReferenceIds;
	private final boolean satisfied;
	private final boolean optional;
	private final boolean multiple;
	private final boolean isStatic;
	private final String target;
	private final String bindMethodName;
	private final String unbindMethodName;

	ComponentReferenceMTO(String name, String serviceName, long[] serviceReferenceIds, boolean satisfied,
			boolean optional, boolean multiple, boolean isstatic, String target, String bindMethodName,
			String unbindMethodName) {
		this.name = name;
		this.serviceName = serviceName;
		this.serviceReferenceIds = serviceReferenceIds;
		this.satisfied = satisfied;
		this.optional = optional;
		this.multiple = multiple;
		this.isStatic = isstatic;
		this.target = target;
		this.bindMethodName = bindMethodName;
		this.unbindMethodName = unbindMethodName;
	}

	public String getName() {
		return name;
	}

	public String getService() {
		return serviceName;
	}

	public long[] getIds() {
		return serviceReferenceIds;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getTarget() {
		return target;
	}

	public String getBindMethodName() {
		return bindMethodName;
	}

	public String getUnbindMethodName() {
		return unbindMethodName;
	}

	@Override
	public String toString() {
		return "ComponentReferenceMTO [name=" + name + ", serviceName=" + serviceName + ", serviceReferenceIds="
				+ Arrays.toString(serviceReferenceIds) + ", satisfied=" + satisfied + ", optional=" + optional
				+ ", multiple=" + multiple + ", isStatic=" + isStatic + ", target=" + target + ", bindMethodName="
				+ bindMethodName + ", unbindMethodName=" + unbindMethodName + "]";
	}

	public ComponentReferenceMTO[] getComponentReferences() {
		// TODO Auto-generated method stub
		return null;
	}
}
