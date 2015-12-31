/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.consumer.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceProxy;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;

public class RemoteServiceHolder {

	private Class<?> rsClass;
	private Object serviceInstance;
	
	public boolean equals(Object o) {
		if (!(o instanceof RemoteServiceHolder)) return false;
		RemoteServiceHolder other = (RemoteServiceHolder) o;
		return this.rsClass.equals(other.rsClass) && this.serviceInstance.equals(other.serviceInstance);
	}
	
	public int hashCode() {
		return this.serviceInstance.hashCode() ^ this.getClass().hashCode();
	}
	
	public RemoteServiceHolder(Class<?> clazz, Object service) {
		this.rsClass = clazz;
		Assert.isTrue(isRemoteServiceProxy(service));
		this.serviceInstance = service;
	}
	
	public static boolean isRemoteServiceProxy(Object service) {
		return (service instanceof IRemoteServiceProxy);
	}
	
	public Class<?> getServiceClass() {
		return rsClass;
	}
	
	public Object getRemoteService() {
		return serviceInstance;
	}
	
	public IRemoteServiceReference getRemoteServiceReference() {
		return ((IRemoteServiceProxy) getRemoteService()).getRemoteServiceReference();
	}
	
	public boolean isActive() {
		return getRemoteServiceReference().isActive();
	}
	
	public ID getLocalContainerID() {
		return getRemoteServiceReference().getContainerID();
	}
	public IRemoteServiceID getRemoteServiceID() {
		return getRemoteServiceReference().getID();
	}
	
	public ID getRemoteContainerID() {
		return getRemoteServiceID().getContainerID();
	}
	
	public long getRemoteServiceId() {
		return getRemoteServiceID().getContainerRelativeID();
	}
}
