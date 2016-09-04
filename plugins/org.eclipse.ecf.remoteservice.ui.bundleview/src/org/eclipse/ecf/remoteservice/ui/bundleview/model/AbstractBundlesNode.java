/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview.model;

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
public class AbstractBundlesNode implements IAdaptable {

	public static final String CLOSED = "Closed";

	private AbstractBundlesNode parent;
	private final List<AbstractBundlesNode> children = new ArrayList<AbstractBundlesNode>();

	public AbstractBundlesNode() {
	}

	public AbstractBundlesNode getParent() {
		return this.parent;
	}

	protected void setParent(AbstractBundlesNode p) {
		this.parent = p;
	}

	public void addChild(AbstractBundlesNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChildAtIndex(int index, AbstractBundlesNode child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(AbstractBundlesNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public AbstractBundlesNode[] getChildren() {
		return (AbstractBundlesNode[]) children.toArray(new AbstractBundlesNode[children.size()]);
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
