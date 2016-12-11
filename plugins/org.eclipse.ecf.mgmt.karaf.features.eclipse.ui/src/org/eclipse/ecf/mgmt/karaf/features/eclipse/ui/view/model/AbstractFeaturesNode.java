package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class AbstractFeaturesNode implements IAdaptable {

	public static final String CLOSED = "Closed";

	private AbstractFeaturesNode parent;
	private final List<AbstractFeaturesNode> children = new ArrayList<AbstractFeaturesNode>();

	public AbstractFeaturesNode() {
	}

	public AbstractFeaturesNode getParent() {
		return this.parent;
	}

	protected void setParent(AbstractFeaturesNode p) {
		this.parent = p;
	}

	public void addChild(AbstractFeaturesNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChildAtIndex(int index, AbstractFeaturesNode child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(AbstractFeaturesNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public AbstractFeaturesNode[] getChildren() {
		return (AbstractFeaturesNode[]) children.toArray(new AbstractFeaturesNode[children.size()]);
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
