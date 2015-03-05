/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.app;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;

public interface IAppManagerAsync {
	CompletableFuture<AppMTO[]> getAppsAsync();
	
	CompletableFuture<AppMTO> getAppAsync(String appId);

	CompletableFuture<AppInstanceMTO[]> getRunningAppsAsync();
	
	CompletableFuture<AppInstanceMTO> getRunningAppAsync(String appInstanceId);
	
	CompletableFuture<IStatus> startAsync(String appId, @SuppressWarnings("rawtypes") Map appArgs);

	CompletableFuture<IStatus> stopAsync(String appInstanceId);

	CompletableFuture<IStatus> lockAsync(String appId);

	CompletableFuture<IStatus> unlockAsync(String appId);
}
