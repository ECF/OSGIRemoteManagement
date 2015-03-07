/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.sharedobject;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.identity.IDMTO;

public interface ISharedObjectManagerAsync {

	CompletableFuture<SharedObjectMTO[]> getSharedObjectsAsync(IDMTO containerID);

	CompletableFuture<IStatus> createSharedObjectAsync(IDMTO containerID,
			IDMTO sharedObjectID, String sharedObjectClassName,
			Map<String,?> properties);

	CompletableFuture<IStatus> destroySharedObjectAsync(IDMTO containerID,
			IDMTO sharedObject);


}
