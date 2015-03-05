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

import org.eclipse.core.runtime.IStatus;

public interface IAppManager {
	AppMTO[] getApps();
	
	AppMTO getApp(String appId);

	AppInstanceMTO[] getRunningApps();
	
	AppInstanceMTO getRunningApp(String appInstanceId);
	
	IStatus start(String appId, @SuppressWarnings("rawtypes") Map appArgs);

	IStatus stop(String appInstanceId);

	IStatus lock(String appId);

	IStatus unlock(String appId);
}
