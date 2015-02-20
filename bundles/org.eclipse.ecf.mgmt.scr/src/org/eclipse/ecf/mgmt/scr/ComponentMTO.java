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
import java.util.Arrays;
import java.util.Map;

import org.apache.felix.scr.Component;
import org.eclipse.ecf.mgmt.PropertiesUtil;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.service.component.ComponentInstance;

public class ComponentMTO implements Serializable {

	private static final long serialVersionUID = -5159960239280902025L;

	private static long curID = 1;

	private static synchronized long generateID() {
		return curID++;
	}

	public static class CompRef {
		private long bid;
		private String name;
		private long id = -1;
		private Long componentId;

		public CompRef(long bundleId, String name, Long componentId) {
			this.bid = bundleId;
			this.name = name;
			this.componentId = componentId;
		}

		public void setId() {
			if (id < 0)
				id = generateID();
		}

		public long getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public long getBundleId() {
			return bid;
		}

		public boolean equals(Object other) {
			if (other instanceof CompRef) {
				CompRef oRef = (CompRef) other;
				return (oRef.bid == bid && name.equals(oRef.name) && componentId.equals(oRef.componentId));
			}
			return false;
		}

		public int hashCode() {
			return componentId.hashCode();
		}
	}

	private static ServiceReferenceMTO findServiceForComponent(Component comp) {
		// If it's not active we give up
		if (comp.getState() != Component.STATE_ACTIVE)
			return null;
		// If it's not exposing any service interfaces then we give up
		String[] compServices = comp.getServices();
		if (compServices == null || compServices.length < 1)
			return null;
		// If it doesn't have a non-null component instance we give up
		ComponentInstance compInstance = comp.getComponentInstance();
		if (compInstance == null)
			return null;
		// If it doesn't have an instance then we give up
		Object compInstanceService = compInstance.getInstance();
		if (compInstanceService == null)
			return null;

		Bundle b = comp.getBundle();
		ServiceReference<?>[] serviceRefs = b.getRegisteredServices();
		if (serviceRefs == null)
			return null;
		ServiceReferenceDTO[] dtos = b.adapt(ServiceReferenceDTO[].class);

		for (ServiceReference<?> sr : serviceRefs) {
			Long serviceId = (Long) sr.getProperty(Constants.SERVICE_ID);
			if (serviceId == null)
				continue;
			else {
				for (ServiceReferenceDTO dto : dtos) {
					if (serviceId.longValue() == dto.id)
						return ServiceReferenceMTO.createMTO(dto);
				}
			}
		}
		return null;
	}

	public static ComponentMTO createMTO(CompRef cRef, Component comp) {
		return new ComponentMTO(cRef.id, comp.getName(), comp.getState(), comp.getBundle().getBundleId(),
				comp.getFactory(), comp.isServiceFactory(), comp.getClassName(), comp.isDefaultEnabled(),
				comp.isImmediate(), comp.getServices(), PropertiesUtil.convertDictionaryToMap(comp.getProperties()),
				ComponentReferenceMTO.createMTOs(comp.getReferences()), (comp.getComponentInstance() == null) ? false
						: true, comp.getActivate(), comp.isActivateDeclared(), comp.getDeactivate(),
				comp.isDeactivateDeclared(), comp.getModified(), comp.getConfigurationPolicy(),
				findServiceForComponent(comp));
	}

	private long id;
	private long componentId;
	private String name;
	private int state;
	private long bundleId;
	private String factory;
	private boolean serviceFactory;
	private String className;
	private boolean defaultEnabled;
	private boolean immediate;
	private String[] services;
	@SuppressWarnings("rawtypes")
	private Map properties;
	private ComponentReferenceMTO[] componentReferenceMTOs;
	private boolean activated;
	private String activate;
	private boolean activateDeclared;
	private String deactivate;
	private boolean deactivateDeclared;
	private String modified;
	private String configurationPolicy;
	private ServiceReferenceMTO serviceInstance;

	ComponentMTO(long id, String name, int state, long bundleId, String factory, boolean serviceFactory,
			String className, boolean defaultEnabled, boolean immediate, String[] services,
			@SuppressWarnings("rawtypes") Map properties, ComponentReferenceMTO[] referenceInfo, boolean activated,
			String activate, boolean activateDeclared, String deactivate, boolean deactivateDeclared, String modified,
			String configurationPolicy, ServiceReferenceMTO serviceInstance) {
		this.id = id;
		Long cId = (Long) properties.get("component.id");
		if (cId != null)
			this.componentId = cId.longValue();
		this.name = name;
		this.state = state;
		this.bundleId = bundleId;
		this.factory = factory;
		this.serviceFactory = serviceFactory;
		this.className = className;
		this.defaultEnabled = defaultEnabled;
		this.immediate = immediate;
		this.services = services;
		this.properties = properties;
		this.componentReferenceMTOs = referenceInfo;
		this.activated = activated;
		this.activate = activate;
		this.activateDeclared = activateDeclared;
		this.deactivate = deactivate;
		this.deactivateDeclared = deactivateDeclared;
		this.modified = modified;
		this.configurationPolicy = configurationPolicy;
		this.serviceInstance = serviceInstance;
	}

	public long getId() {
		return id;
	}

	public long getComponentId() {
		return componentId;
	}

	public String getName() {
		return name;
	}

	public int getState() {
		return state;
	}

	public long getBundleId() {
		return bundleId;
	}

	public String getFactory() {
		return factory;
	}

	public boolean isServiceFactory() {
		return serviceFactory;
	}

	public String getClassName() {
		return className;
	}

	public boolean isDefaultEnabled() {
		return defaultEnabled;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public String[] getServices() {
		return services;
	}

	@SuppressWarnings("rawtypes")
	public Map getProperties() {
		return properties;
	}

	public ComponentReferenceMTO[] getReferences() {
		return componentReferenceMTOs;
	}

	public boolean isActivated() {
		return activated;
	}

	public String getActivate() {
		return activate;
	}

	public boolean isActivateDeclared() {
		return activateDeclared;
	}

	public String getDeactivate() {
		return deactivate;
	}

	public boolean isDeactivateDeclared() {
		return deactivateDeclared;
	}

	public String getModified() {
		return modified;
	}

	public String getConfigurationPolicy() {
		return configurationPolicy;
	}

	public ServiceReferenceMTO getActiveService() {
		return serviceInstance;
	}

	@Override
	public String toString() {
		return "ComponentMTO [id=" + id + ", componentId=" + componentId + ", name=" + name + ", state=" + state
				+ ", bundleId=" + bundleId + ", factory=" + factory + ", serviceFactory=" + serviceFactory
				+ ", className=" + className + ", defaultEnabled=" + defaultEnabled + ", immediate=" + immediate
				+ ", services=" + Arrays.toString(services) + ", properties=" + properties
				+ ", componentReferenceMTOs=" + Arrays.toString(componentReferenceMTOs) + ", activated=" + activated
				+ ", activate=" + activate + ", activateDeclared=" + activateDeclared + ", deactivate=" + deactivate
				+ ", deactivateDeclared=" + deactivateDeclared + ", modified=" + modified + ", configurationPolicy="
				+ configurationPolicy + ", serviceInstance=" + serviceInstance + "]";
	}

}
