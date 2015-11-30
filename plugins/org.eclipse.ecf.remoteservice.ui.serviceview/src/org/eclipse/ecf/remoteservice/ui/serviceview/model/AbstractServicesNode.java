/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * @since 3.3
 */
public class AbstractServicesNode implements IAdaptable {

	public static final String CLOSED = "Closed";

	private AbstractServicesNode parent;
	private final List<AbstractServicesNode> children = new ArrayList<AbstractServicesNode>();

	public AbstractServicesNode() {
	}

	public AbstractServicesNode getParent() {
		return this.parent;
	}

	protected void setParent(AbstractServicesNode p) {
		this.parent = p;
	}

	public void addChild(AbstractServicesNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChildAtIndex(int index, AbstractServicesNode child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(AbstractServicesNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public AbstractServicesNode[] getChildren() {
		return (AbstractServicesNode[]) children.toArray(new AbstractServicesNode[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public void clearChildren() {
		children.clear();
	}

	public static String convertStringArrayToString(String[] strings) {
		return PropertyUtils.convertStringArrayToString(strings);
	}

	public static Map<String, Object> convertServicePropsToMap(ServiceReference<?> sr) {
		return PropertyUtils.convertServicePropsToMap(sr);
	}

	protected String convertObjectClassToString(ServiceReference<?> sr) {
		return (sr == null) ? CLOSED : convertStringArrayToString((String[]) sr.getProperty(Constants.OBJECTCLASS));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}
}
