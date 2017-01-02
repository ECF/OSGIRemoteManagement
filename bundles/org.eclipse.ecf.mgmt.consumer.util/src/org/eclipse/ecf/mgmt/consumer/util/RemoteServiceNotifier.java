/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.consumer.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.identity.ID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate=true)
public class RemoteServiceNotifier implements IRemoteServiceNotifier {

	private Map<ID, List<RemoteServiceHolder<?>>> map;
	private Map<IRemoteServiceListener, Class<?>> listeners;
	private Object lock = new Object();

	@Activate
	protected void activate() throws Exception {
		this.map = new HashMap<ID, List<RemoteServiceHolder<?>>>();
		this.listeners = new HashMap<IRemoteServiceListener, Class<?>>();
	}

	@Deactivate
	protected void deactivate() {
		if (map != null) {
			map.clear();
			map = null;
		}
		if (listeners != null) {
			listeners.clear();
			listeners = null;
		}
	}

	public <T> void addServiceHolder(RemoteServiceHolder<T> holder) {
		Map<IRemoteServiceListener, Class<?>> lcopy = null;
		ID remoteID = holder.getRemoteContainerID();
		synchronized (lock) {
			List<RemoteServiceHolder<?>> holderList = null;
			for (ID id : map.keySet()) {
				if (remoteID.equals(id)) {
					holderList = map.get(id);
					break;
				}
			}
			if (holderList == null)
				holderList = new ArrayList<RemoteServiceHolder<?>>();
			holderList.add(holder);
			map.put(remoteID, holderList);
			lcopy = new HashMap<IRemoteServiceListener, Class<?>>(listeners);
		}
		fireRemoteServicesEvent(lcopy, RemoteServiceEvent.ADDED, holder);
	}

	public <T> boolean addServiceHolder(Class<T> clazz, T service) {
		if (RemoteServiceHolder.isRemoteServiceProxy(service)) {
			addServiceHolder(new RemoteServiceHolder<T>(clazz, service));
			return true;
		}
		return false;
	}

	public <T> boolean removeServiceHolder(RemoteServiceHolder<T> holder) {
		Map<IRemoteServiceListener, Class<?>> lcopy = null;
		ID remoteID = holder.getRemoteContainerID();
		boolean removed = false;
		synchronized (lock) {
			List<RemoteServiceHolder<?>> holderList = null;
			for (Iterator<ID> i = map.keySet().iterator(); i.hasNext();) {
				ID next = i.next();
				if (remoteID.equals(next))
					holderList = map.get(next);
				if (holderList != null) {
					holderList.remove(holder);
					removed = true;
				}
				if (holderList.size() == 0)
					i.remove();
				holderList = new ArrayList<RemoteServiceHolder<?>>();
				lcopy = new HashMap<IRemoteServiceListener, Class<?>>(listeners);
			}
		}
		if (removed)
			fireRemoteServicesEvent(lcopy, RemoteServiceEvent.REMOVED, holder);
		return removed;
	}

	public <T> boolean removeServiceHolder(Class<T> clazz, T service) {
		if (RemoteServiceHolder.isRemoteServiceProxy(service))
			return removeServiceHolder(new RemoteServiceHolder<T>(clazz, service));
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<RemoteServiceHolder<T>> addListener(IRemoteServiceListener l, Class<T> clazz) {
		Collection<RemoteServiceHolder<T>> results = new ArrayList<RemoteServiceHolder<T>>();
		synchronized (lock) {
			listeners.put(l, clazz);
			for (List<RemoteServiceHolder<?>> hl : map.values())
				for (RemoteServiceHolder<?> rsh : hl)
					if (rsh.getServiceClass().equals(clazz))
						results.add((RemoteServiceHolder<T>) rsh);
		}
		return results;
	}

	public void removeListener(IRemoteServiceListener l) {
		synchronized (lock) {
			listeners.remove(l);
		}
	}

	protected void logException(String message, Throwable t) {
		if (message != null)
			System.out.println(message);
		if (t != null)
			t.printStackTrace();
	}

	protected <T> void fireRemoteServicesEvent(Map<IRemoteServiceListener, Class<?>> lcopy, final int type,
			final RemoteServiceHolder<T> holder) {
		Class<T> clazz = holder.getServiceClass();
		for (final IRemoteServiceListener l : lcopy.keySet())
			if (clazz.equals(lcopy.get(l)))
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable exception) {
						logException("Exception in IRemoteServiceListener", exception);
					}
					public void run() throws Exception {
						l.handleEvent(new RemoteServiceEvent(type, holder));
					}
				});
	}

}
