/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.registry.host;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.registry.ConfigurationElementMTO;
import org.eclipse.ecf.mgmt.registry.ExtensionMTO;
import org.eclipse.ecf.mgmt.registry.ExtensionPointMTO;
import org.eclipse.ecf.mgmt.registry.IExtensionRegistryManager;

public class ExtensionRegistryManager extends AbstractManager implements IExtensionRegistryManager {

	private IExtensionRegistry extensionRegistry;
	
	protected void bindExtensionRegistry(IExtensionRegistry registry) {
		this.extensionRegistry = registry;
	}
	
	protected void unbindExtensionRegistry(IExtensionRegistry registry) {
		this.extensionRegistry = null;
	}
	
	protected List<IExtensionPoint> getEPoints() {
		return Arrays.asList(extensionRegistry.getExtensionPoints());
	}
	
	protected ConfigurationElementMTO createMTO(
			IConfigurationElement e) {
		IConfigurationElement children[] = e.getChildren();
		ConfigurationElementMTO[] childs = new ConfigurationElementMTO[children.length];
		for (int i = 0; i < children.length; i++)
			childs[i] = createMTO(children[i]);
		String attributeNames[] = e.getAttributeNames();
		Map<String,String> attributes = new HashMap<String,String>();
		for (int i = 0; i < attributeNames.length; i++) {
			String v = e.getAttribute(attributeNames[i]);
			if (v != null)
				attributes.put(attributeNames[i], v);
		}
		return new ConfigurationElementMTO(e.getName(), e.getValue(),
				e.getNamespaceIdentifier(), e.getDeclaringExtension()
						.getUniqueIdentifier(), getBundle0(
						e.getContributor().getName()).getBundleId(),
				e.isValid(), attributes, childs);
	}
	
	protected ExtensionMTO createMTO(IExtension e) {
		List<ConfigurationElementMTO> ceis = selectAndMap(Arrays.asList(e.getConfigurationElements()),null,ce -> {
			return createMTO(ce);
		});
		return new ExtensionMTO(e.getLabel(),
				e.getExtensionPointUniqueIdentifier(),
				e.getNamespaceIdentifier(), e.getSimpleIdentifier(),
				e.getUniqueIdentifier(), e.isValid(), getBundle0(
						e.getContributor().getName()).getBundleId(),
				ceis.toArray(new ConfigurationElementMTO[ceis.size()]));
	}

	protected ExtensionPointMTO createMTO(IExtensionPoint ep) {
		List<ExtensionMTO> exs = selectAndMap(Arrays.asList(ep.getExtensions()),null,e -> {
			return createMTO(e);
		});
		return new ExtensionPointMTO(ep.getLabel(),
				ep.getNamespaceIdentifier(), ep.getSimpleIdentifier(),
				ep.getUniqueIdentifier(), ep.isValid(), getBundle0(
						ep.getContributor().getName()).getBundleId(),
				exs.toArray(new ExtensionMTO[exs.size()]));
	}

	protected List<IExtension> getAllExtensions() {
		String namespaces[] = extensionRegistry.getNamespaces();
		List<IExtension> l = new ArrayList<IExtension>();
		for (int i = 0; i < namespaces.length; i++) {
			IExtension es[] = extensionRegistry.getExtensions(namespaces[i]);
			for (int j = 0; j < es.length; j++)
				l.add(es[j]);
		}
		return l;
	}

	@Override
	public String[] getExtensionPointIds() {
		List<String> results = selectAndMap(getEPoints(),null,ep -> {
			return ep.getUniqueIdentifier();
		});
		return results.toArray(new String[results.size()]);
	}

	@Override
	public ExtensionPointMTO getExtensionPoint(String extensionPointId) {
		List<ExtensionPointMTO> results = selectAndMap(getEPoints(),ep -> {
			return ep.getUniqueIdentifier().equals(extensionPointId);
		},ep -> {
			return createMTO(ep);
		});
		return results.size()==0?null:results.get(0);
	}

	@Override
	public ExtensionPointMTO[] getExtensionPointsForContributor(String contributorId) {
		List<ExtensionPointMTO> results = selectAndMap(getEPoints(),ep -> {
			return ep.getContributor().getName().equals(contributorId);
		},ep -> {
			return createMTO(ep);
		});
		return results.toArray(new ExtensionPointMTO[results.size()]);
	}

	@Override
	public ExtensionPointMTO[] getExtensionPoints() {
		List<ExtensionPointMTO> results = selectAndMap(getEPoints(),null,ep -> {
			return createMTO(ep);
		});
		return results.toArray(new ExtensionPointMTO[results.size()]);
	}

	@Override
	public ExtensionMTO getExtension(String extensionPointId, String extensionId) {
		List<ExtensionMTO> results = selectAndMap(select(getEPoints(),ep -> {
			return ep.getUniqueIdentifier().equals(extensionPointId);
			}),ep -> {
				return (ep.getExtension(extensionId) != null);
			},e -> {
			return createMTO(e.getExtension(extensionId));
		});
		return results.size()==0?null:results.get(0);
	}

	@Override
	public ExtensionMTO[] getExtensionsForContributor(String contributorId) {
		List<ExtensionMTO> results = selectAndMap(getAllExtensions(),ex -> {
			return ex.getContributor().equals(contributorId);
		}, ex -> {
			return createMTO(ex);
		});
		return results.toArray(new ExtensionMTO[results.size()]);
	}

	protected IExtensionPoint findExtensionPoint(String extensionPointId) {
		List<IExtensionPoint> eps = select(getEPoints(),ep -> {
			return ep.getUniqueIdentifier().equals(extensionPointId);
		});
		return (eps.size()==0)?null:eps.get(0);
	}
	
	@Override
	public ExtensionMTO[] getExtensions(String extensionPointId) {
		IExtensionPoint ep = findExtensionPoint(extensionPointId);
		if (ep == null) return null;
		List<ExtensionMTO> results = selectAndMap(Arrays.asList(ep.getExtensions()),null,ex -> {
			return createMTO(ex);
		});
		return results.toArray(new ExtensionMTO[results.size()]);
	}

	@Override
	public ConfigurationElementMTO[] getConfigurationElements(String extensionPointId) {
		IExtensionPoint ep = findExtensionPoint(extensionPointId);
		if (ep == null) return null;
		List<ConfigurationElementMTO> results = selectAndMap(Arrays.asList(ep.getConfigurationElements()),null,ex -> {
			return createMTO(ex);
		});
		return results.toArray(new ConfigurationElementMTO[results.size()]);
	}

}
