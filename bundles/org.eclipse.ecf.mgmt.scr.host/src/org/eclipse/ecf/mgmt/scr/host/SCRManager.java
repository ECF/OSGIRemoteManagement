/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr.host;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.scr.ComponentMTO;
import org.eclipse.ecf.mgmt.scr.ComponentMTO.CompRef;
import org.eclipse.ecf.mgmt.scr.ISCRManager;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentConstants;

public class SCRManager extends AbstractManager implements ISCRManager {

	private ScrService scrService;

	void bindScrService(ScrService svc) {
		this.scrService = svc;
	}

	void unbindScrService(ScrService svc) {
		this.scrService = null;
	}

	private Hashtable<CompRef, CompRef> compRefs = new Hashtable<CompRef, CompRef>(101);

	private Long getComponentId(Component comp) {
		return (Long) comp.getProperties().get(ComponentConstants.COMPONENT_ID);
	}

	public ComponentMTO getComponent(long componentId) {
		Component c = scrService.getComponent(componentId);
		return (c == null)?null:getComponentMTO(c);
	}

	private ComponentMTO getComponentMTO(Component comp) {
		CompRef ref = null;
		CompRef cRef = new CompRef(comp.getBundle().getBundleId(), comp.getName(), getComponentId(comp));
		synchronized (compRefs) {
			ref = (CompRef) compRefs.get(cRef);
			if (ref == null) {
				ref = cRef;
				ref.setId();
				compRefs.put(ref, ref);
			}
		}
		return ComponentMTO.createMTO(ref, comp);
	}

	private ComponentMTO[] getComponentMTOsForBundle(Bundle bundle) {
		Component[] comps = (bundle == null) ? scrService.getComponents() : scrService.getComponents(bundle);
		if (comps == null) return new ComponentMTO[0];
		List<ComponentMTO> results = selectAndMap(Arrays.asList(comps),null,c -> {
			return getComponentMTO(c);
		});
		return results.toArray(new ComponentMTO[results.size()]);
	}

	public ComponentMTO[] getComponents() {
		return getComponentMTOsForBundle(null);
	}

	private IStatus enableDisableComponent(long id, boolean enable) {
		CompRef cRef = findCRefForId(id);
		if (cRef == null)
			return createErrorStatus("Component with id=" + id + " cannot be found"); //$NON-NLS-1$ //$NON-NLS-2$
		Component comp = findComponent(scrService, cRef);
		if (comp == null)
			return createErrorStatus("Component with id=" + id + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			if (enable)
				comp.enable();
			else
				comp.disable();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (IllegalStateException e) {
			return createErrorStatus("Component with id=" + id + " cannot be " + ((enable) ? "enabled" : "disabled"), e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	public IStatus enable(long id) {
		return enableDisableComponent(id, true);
	}

	private Component findComponent(ScrService scrService, CompRef cRef) {
		Bundle bundle = findBundleForId(cRef.getBundleId());
		if (bundle == null)
			return null;
		Component[] bundleComponents = scrService.getComponents(bundle);
		if (bundleComponents == null)
			return null;
		for (int i = 0; i < bundleComponents.length; i++) {
			String cRefName = cRef.getName();
			if (cRefName != null && cRefName.equals(bundleComponents[i].getName()))
				return bundleComponents[i];
		}
		return null;
	}

	private Bundle findBundleForId(long bid) {
		Bundle[] bundles = getContext().getBundles();
		if (bundles == null)
			return null;
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getBundleId() == bid)
				return bundles[i];
		}
		return null;
	}

	private CompRef findCRefForId(long id) {
		synchronized (compRefs) {
			for (Iterator<CompRef> i = compRefs.keySet().iterator(); i.hasNext();) {
				CompRef cr = (CompRef) i.next();
				if (cr.getId() == id)
					return cr;
			}
		}
		return null;
	}

	public IStatus disable(long id) {
		return enableDisableComponent(id, false);
	}

	public ComponentMTO[] getComponents(long bundleId) {
		Bundle bundle = getBundle0(bundleId);
		if (bundle == null)
			return null;
		return getComponentMTOsForBundle(bundle);
	}

}
