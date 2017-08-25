/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.scr.ComponentConfigurationMTO;
import org.eclipse.ecf.mgmt.scr.ComponentDescriptionMTO;
import org.eclipse.ecf.mgmt.scr.ComponentId;
import org.eclipse.ecf.mgmt.scr.IServiceComponentRuntimeManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

public class ServiceComponentRuntimeManager extends AbstractManager implements IServiceComponentRuntimeManager {

	private static final ComponentConfigurationMTO[] EMPTY_CONFIGURATIONS = new ComponentConfigurationMTO[0];
	
	private ServiceComponentRuntime scr;

	protected void bindScrService(ServiceComponentRuntime svc) {
		this.scr = svc;
	}

	protected void unbindScrService(ServiceComponentRuntime svc) {
		this.scr = null;
	}

	@Override
	public ComponentConfigurationMTO[] getComponentConfigurations(ComponentId componentId) {
		if (componentId == null) return EMPTY_CONFIGURATIONS;
		List<ComponentConfigurationMTO> results = new ArrayList<ComponentConfigurationMTO>();
		ComponentDescriptionDTO cddto = getDTO(componentId.getBundleId(), componentId.getName());
		if (cddto != null)
			for (ComponentConfigurationDTO ccdto : this.scr.getComponentConfigurationDTOs(cddto))
				results.add(new ComponentConfigurationMTO(ccdto));
		return results.toArray(new ComponentConfigurationMTO[results.size()]);
	}

	@Override
	public ComponentDescriptionMTO getComponentDescription(ComponentId componentId) {
		if (componentId == null)
			return null;
		ComponentDescriptionMTO result = null;
		ComponentDescriptionDTO cddto = getDTO(componentId.getBundleId(), componentId.getName());
		if (cddto != null)
			result = new ComponentDescriptionMTO(cddto);
		return result;
	}

	protected ComponentDescriptionDTO getDTO(long bundleId, String name) {
		ComponentDescriptionDTO result = null;
		Bundle b = getBundle0(bundleId);
		if (b != null) {
			ComponentDescriptionDTO dto = new ComponentDescriptionDTO();
			dto.bundle = b.adapt(BundleDTO.class);
			dto.name = name;
			result = this.scr.getComponentDescriptionDTO(b, name);
		}
		return result;
	}

	@Override
	public ComponentDescriptionMTO[] getComponentDescriptions(long[] bundleIds) {
		List<Bundle> bundles = new ArrayList<Bundle>();
		List<ComponentDescriptionMTO> results = new ArrayList<ComponentDescriptionMTO>();
		for (long bid : bundleIds) {
			Bundle b = getBundle0(bid);
			if (b != null)
				bundles.add(b);
		}
		Collection<ComponentDescriptionDTO> cddtos = this.scr
				.getComponentDescriptionDTOs(bundles.toArray(new Bundle[bundles.size()]));
		for (ComponentDescriptionDTO cddto : cddtos)
			results.add(new ComponentDescriptionMTO(cddto));
		return results.toArray(new ComponentDescriptionMTO[results.size()]);
	}

	@Override
	public boolean isComponentEnabled(ComponentId componentId) {
		if (componentId == null)
			return false;
		ComponentDescriptionDTO cddto = getDTO(componentId.getBundleId(), componentId.getName());
		return (cddto != null) ? this.scr.isComponentEnabled(cddto) : false;
	}

	@Override
	public IStatus enableComponent(ComponentId componentId) {
		if (componentId == null)
			return createErrorStatus("ComponentId must not be null");
		return enableOrDisable(componentId.getBundleId(), componentId.getName(), true);
	}

	protected IStatus enableOrDisable(long bundleId, String name, boolean enable) {
		ComponentDescriptionDTO cddto = getDTO(bundleId, name);
		IStatus result = Status.OK_STATUS;
		if (cddto != null) {
			Promise<Void> promise = (enable) ? this.scr.enableComponent(cddto)
					: this.scr.disableComponent(cddto);
			Throwable t = null;
			try {
				t = promise.getFailure();
			} catch (InterruptedException e) {
				t = e;
			}
			if (t != null)
				result = createErrorStatus("Could not " + ((enable) ? "enable" : "disable") + " component bundleId="
						+ bundleId + ";name=" + name, t);
		}
		return result;
	}

	@Override
	public IStatus disableComponent(ComponentId componentId) {
		if (componentId == null)
			return createErrorStatus("ComponentId must not be null");
		return enableOrDisable(componentId.getBundleId(), componentId.getName(), false);
	}

}
