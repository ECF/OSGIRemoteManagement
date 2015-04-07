/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.discovery.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ui.views.properties.IPropertySource;

public abstract class AbstractEndpointNode implements IAdaptable {

	private AbstractEndpointNode parent;
	private final List<AbstractEndpointNode> children = new ArrayList<AbstractEndpointNode>();

	public static final String getPackageName(String fqClassName) {
		int lastDot = fqClassName.lastIndexOf('.');
		return (lastDot == -1) ? fqClassName : fqClassName
				.substring(0, lastDot);
	}

	public static final List<String> getStringArrayProperty(
			Map<String, Object> props, String propName) {
		Object r = props.get(propName);
		List<String> results = new ArrayList<String>();
		if (r == null)
			return results;
		else if (r instanceof String[]) {
			String[] vals = (String[]) r;
			for (String v : vals)
				results.add(v);
		} else if (r instanceof String)
			results.add((String) r);
		else
			results.add(r.toString());
		return results;
	}

	protected AbstractEndpointNode() {
		this(null);
	}

	protected AbstractEndpointNode(AbstractEndpointNode parent) {
		this.parent = parent;
	}

	protected void setParent(AbstractEndpointNode p) {
		this.parent = p;
	}

	protected Map<String, Object> getEndpointDescriptionProperties() {
		return getEndpointDescription().getProperties();
	}

	protected List<String> getStringArrayProperty(String propName) {
		return getStringArrayProperty(getEndpointDescriptionProperties(),
				propName);
	}

	public EndpointDescription getEndpointDescription() {
		AbstractEndpointNode parent = getParent();
		return (parent == null) ? null : parent.getEndpointDescription();
	}

	public AbstractEndpointNode getParent() {
		return this.parent;
	}

	public void addChild(AbstractEndpointNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChildAtIndex(int index, AbstractEndpointNode child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(AbstractEndpointNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public AbstractEndpointNode[] getChildren() {
		return (AbstractEndpointNode[]) children
				.toArray(new AbstractEndpointNode[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySource.class) {
			EndpointDescription ed = getEndpointDescription();
			if (ed != null)
				return new EndpointPropertySource(ed.getProperties());
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
