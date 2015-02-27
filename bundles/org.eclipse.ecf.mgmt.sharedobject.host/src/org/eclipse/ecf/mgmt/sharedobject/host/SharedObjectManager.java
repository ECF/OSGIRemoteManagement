/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.sharedobject.host;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.SharedObjectCreateException;
import org.eclipse.ecf.core.sharedobject.SharedObjectDescription;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.identity.IDMTO;
import org.eclipse.ecf.mgmt.identity.host.IdentityFactoryManager;
import org.eclipse.ecf.mgmt.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.mgmt.sharedobject.SharedObjectMTO;

public class SharedObjectManager extends AbstractManager implements ISharedObjectManager {

	private IContainerManager containerManager;

	void bindContainerManager(IContainerManager containerManager) {
		this.containerManager = containerManager;
	}

	void unbindContainerManager(IContainerManager unbindContainerManager) {
		this.containerManager = null;
	}

	private ISharedObjectContainer getSharedObjectContainer(IDMTO containerID) {
		if (containerID == null)
			return null;
		IContainer c = containerManager.getContainer(IdentityFactoryManager.createID(containerID));
		return (c == null) ? null : (ISharedObjectContainer) c.getAdapter(ISharedObjectContainer.class);
	}

	@Override
	public SharedObjectMTO[] getSharedObjects(IDMTO containerID) {
		List<SharedObjectMTO> results = null;
		ISharedObjectContainer soc = getSharedObjectContainer(containerID);
		if (soc != null) {
			org.eclipse.ecf.core.sharedobject.ISharedObjectManager som = soc.getSharedObjectManager();
			ID[] soids = som.getSharedObjectIDs();
			if (soids != null) {
				for (ID id : soids) {
					IDMTO idmto = IdentityFactoryManager.createIDMTO(id);
					ISharedObject so = som.getSharedObject(id);
					if (so != null) {
						if (results == null)
							results = new ArrayList<SharedObjectMTO>();
						results.add(createMTO(idmto, so));
					}
				}
			}
		}
		return results == null ? null : results.toArray(new SharedObjectMTO[results.size()]);
	}

	public static SharedObjectMTO createMTO(IDMTO id, ISharedObject so) {
		return new SharedObjectMTO(id, so.getClass().getName());
	}

	@Override
	public IStatus createSharedObject(IDMTO containerID, IDMTO sharedObjectID, String sharedObjectClassName,
			Map<String, ?> properties) {
		ISharedObjectContainer c = getSharedObjectContainer(containerID);
		if (c == null)
			return createErrorStatus("socontainer with ID=" + containerID + " not found");
		ID soid = IdentityFactoryManager.createID(sharedObjectID);
		if (soid == null)
			return createErrorStatus("cannot create soID for=" + sharedObjectID);
		try {
			c.getSharedObjectManager().createSharedObject(
					new SharedObjectDescription(sharedObjectClassName, soid, properties));
		} catch (SharedObjectCreateException e) {
			return createErrorStatus("Could not create shared object with id=" + sharedObjectID);
		}
		return SerializableStatus.OK_STATUS;
	}

	@Override
	public IStatus destroySharedObject(IDMTO containerID, IDMTO sharedObjectID) {
		ISharedObjectContainer c = getSharedObjectContainer(containerID);
		if (c == null)
			return createErrorStatus("socontainer with ID=" + containerID + " not found");
		ID soid = IdentityFactoryManager.createID(sharedObjectID);
		if (soid == null)
			return createErrorStatus("cannot create soID for=" + sharedObjectID);

		ISharedObject so = c.getSharedObjectManager().removeSharedObject(soid);
		return (so == null) ? createErrorStatus("SharedObject with id=" + sharedObjectID + " not found to remove")
				: SerializableStatus.OK_STATUS;
	}

}
