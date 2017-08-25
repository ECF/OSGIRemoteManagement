/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
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
import java.util.Map;

import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;

public class ComponentDescriptionMTO implements Serializable {

	private static final long serialVersionUID = 7122734634014752227L;
	private final String activate;
	private final String[] configurationPid;
	private final String configurationPolicy;
	private final String deactivate;
	private final boolean defaultEnabled;
	private final String factory;
	private final ComponentId id;
	private final boolean immediate;
	private final String implementationClass;
	private final String modified;
	private final Map<String, Object> properties;
	private final ReferenceMTO[] references;
	private final String scope;
	private final String[] serviceInterfaces;

	public ComponentDescriptionMTO(ComponentDescriptionDTO dto) {
		this.activate = dto.activate;
		this.configurationPid = dto.configurationPid;
		this.configurationPolicy = dto.configurationPolicy;
		this.deactivate = dto.deactivate;
		this.defaultEnabled = dto.defaultEnabled;
		this.factory = dto.factory;
		this.id = new ComponentId(dto.bundle.id, dto.name);
		this.immediate = dto.immediate;
		this.implementationClass = dto.implementationClass;
		this.modified = dto.modified;
		this.properties = dto.properties;
		List<ReferenceMTO> refs = new ArrayList<ReferenceMTO>();
		for (ReferenceDTO rdto : dto.references)
			refs.add(new ReferenceMTO(rdto));
		this.references = refs.toArray(new ReferenceMTO[refs.size()]);
		this.scope = dto.scope;
		this.serviceInterfaces = dto.serviceInterfaces;
	}

	public String getActivate() {
		return activate;
	}

	public String[] getConfigurationPid() {
		return configurationPid;
	}

	public String getConfigurationPolicy() {
		return configurationPolicy;
	}

	public String getDeactivate() {
		return deactivate;
	}

	public boolean isDefaultEnabled() {
		return defaultEnabled;
	}

	public String getFactory() {
		return factory;
	}

	public ComponentId getComponentId() {
		return id;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public String getImplementationClass() {
		return implementationClass;
	}

	public String getModified() {
		return modified;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public ReferenceMTO[] getReferences() {
		return references;
	}

	public String getScope() {
		return scope;
	}

	public String[] getServiceInterfaces() {
		return serviceInterfaces;
	}

	@Override
	public String toString() {
		return "ComponentDescriptionMTO [activate=" + activate + ", configurationPid="
				+ Arrays.toString(configurationPid) + ", configurationPolicy=" + configurationPolicy + ", deactivate="
				+ deactivate + ", defaultEnabled=" + defaultEnabled + ", factory=" + factory + ", id=" + this.id
				+ ", immediate=" + immediate + ", implementationClass=" + implementationClass + ", modified=" + modified
				+ ", properties=" + properties + ", references=" + Arrays.toString(references) + ", scope=" + scope
				+ ", serviceInterfaces=" + Arrays.toString(serviceInterfaces) + "]";
	}

}
