/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.cm;

import java.util.Dictionary;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;

public interface IConfigurationManagerAsync {

	CompletableFuture<ConfigurationMTO> createFactoryConfigurationAsync(String factoryPid);
	CompletableFuture<ConfigurationMTO> createFactoryConfigurationAsync(String factoryPid, String location);
	CompletableFuture<ConfigurationMTO> getConfigurationAsync(String pid);
	CompletableFuture<ConfigurationMTO> getConfigurationAsync(String pid, String location);
	CompletableFuture<ConfigurationMTO[]> listConfigurationsAsync(String filter);
	
	CompletableFuture<IStatus> updateAsync(String id);
	CompletableFuture<IStatus> updateAsync(String id, Dictionary<String,?> properties);
	
	CompletableFuture<IStatus> deleteAsync(String id);
	
}
