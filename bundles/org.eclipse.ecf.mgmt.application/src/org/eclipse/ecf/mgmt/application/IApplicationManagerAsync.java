/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.application;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;

public interface IApplicationManagerAsync {
	CompletableFuture<ApplicationMTO[]> getApplicationsAsync();
	
	CompletableFuture<ApplicationMTO> getApplicationAsync(String applicationId);

	CompletableFuture<ApplicationInstanceMTO[]> getRunningApplicationsAsync();
	
	CompletableFuture<ApplicationInstanceMTO> getRunningApplicationAsync(String applicationInstanceId);
	
	CompletableFuture<IStatus> startApplicationAsync(String applicationId, @SuppressWarnings("rawtypes") Map appArgs);

	CompletableFuture<IStatus> stopApplicationAsync(String applicationInstanceId);

	CompletableFuture<IStatus> lockApplicationAsync(String applicationId);

	CompletableFuture<IStatus> unlockApplicationAsync(String applicationId);
}
