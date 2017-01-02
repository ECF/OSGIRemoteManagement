package org.eclipse.ecf.mgmt.consumer.util;

import java.util.Collection;

public interface IRemoteServiceNotifier {

	<T> void addServiceHolder(RemoteServiceHolder<T> holder);
	<T> boolean addServiceHolder(Class<T> clazz, T service);
	<T> boolean removeServiceHolder(RemoteServiceHolder<T> holder);
	<T> boolean removeServiceHolder(Class<T> clazz, T service);
	
	<T> Collection<RemoteServiceHolder<T>> addListener(IRemoteServiceListener l, Class<T> clazz);
	void removeListener(IRemoteServiceListener l);
}
