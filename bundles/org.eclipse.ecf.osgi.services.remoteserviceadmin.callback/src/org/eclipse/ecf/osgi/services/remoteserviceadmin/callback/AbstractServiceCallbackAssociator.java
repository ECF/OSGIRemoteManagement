/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin.callback;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;

public class AbstractServiceCallbackAssociator {

	public static final String ECF_RSA_PROP_PREFIX = "ecf.x.rsa.";
	
	private RemoteServiceAdmin rsa;
	private IContainerManager containerManager;
	private BundleContext context;
	
	protected IContainerManager getContainerManager() {
		return this.containerManager;
	}
	
	protected void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = rsa;
	}

	protected void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.rsa = null;
	}

	public RemoteServiceAdmin getRSA() {
		return rsa;
	}

	protected void bindContainerManager(IContainerManager cm) {
		this.containerManager = cm;
	}

	protected void unbindContainerManager(IContainerManager cm) {
		this.containerManager = null;
	}

	public ContainerTypeDescription getContainerTypeDescription(ID containerID) {
		IContainerManager cm = getContainerManager();
		return (cm != null)?cm.getContainerTypeDescription(containerID):null;
	}
	
	public IContainer getContainerConnectedToID(ID id) {
		for (IContainer c : getContainerManager().getAllContainers()) {
			ID targetID = c.getConnectedID();
			if (targetID != null && targetID.equals(id))
				return c;
		}
		return null;
	}

	protected void logException(String string, Throwable e) {
		System.out.println(string);
		if (e != null)
			e.printStackTrace();
	}

    protected void activate(BundleContext context) throws Exception {
    	this.context = context;
    }
    
    protected void deactivate() {
    	this.context = null;
    }
    
    protected BundleContext getContext() {
    	return this.context;
    }
    
	protected String getCallbackPackageVersion(Class<?> clazz, String packageName) {
		Version v = getVersionForPackage(FrameworkUtil.getBundle(clazz), packageName);
		return (v != null) ? v.toString() : null;
	}

	protected String getPackageName(String className) {
		int lastDotIndex = className.lastIndexOf("."); //$NON-NLS-1$
		if (lastDotIndex == -1)
			return ""; //$NON-NLS-1$
		return className.substring(0, lastDotIndex);
	}

	protected Version getVersionForPackage(final Bundle providingBundle, String packageName) {
		Version result = null;
		BundleRevision providingBundleRevision = AccessController.doPrivileged(new PrivilegedAction<BundleRevision>() {
			public BundleRevision run() {
				return providingBundle.adapt(BundleRevision.class);
			}
		});
		if (providingBundleRevision == null)
			return null;
		List<BundleCapability> providerCapabilities = providingBundleRevision
				.getDeclaredCapabilities(BundleRevision.PACKAGE_NAMESPACE);
		for (BundleCapability c : providerCapabilities) {
			result = getVersionForMatchingCapability(packageName, c);
			if (result != null)
				return result;
		}
		return result;
	}

	protected Class<?> findInterface(List<String> intfNames,Class<?> clazz) {
		for(Class<?> cls: clazz.getInterfaces()) 
			if (intfNames.contains(cls.getName()))
				return cls;
		return null;
	}
	
	protected Class<?> findMatchingInterface(List<String> intfNames, Class<?> root) {
		Class<?> cls = root;
		while (cls != null) {
			Class<?> intf = findInterface(intfNames,cls);
			if (intf != null)
				return intf;
			cls = cls.getSuperclass();
		}
		return null;
	}

	protected Version getVersionForMatchingCapability(String packageName, BundleCapability capability) {
		// If it's a package namespace (Import-Package)
		Map<String, Object> attributes = capability.getAttributes();
		// Then we get the package attribute
		String p = (String) attributes.get(BundleRevision.PACKAGE_NAMESPACE);
		// And compare it to the package name
		if (p != null && packageName.equals(p))
			return (Version) attributes.get(Constants.VERSION_ATTRIBUTE);
		return null;
	}

}
