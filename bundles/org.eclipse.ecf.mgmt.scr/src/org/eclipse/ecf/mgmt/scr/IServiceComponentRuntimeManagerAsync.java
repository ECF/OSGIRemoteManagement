/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;

/**
 * Service component runtime manager service interface.
 * 
 */
public interface IServiceComponentRuntimeManagerAsync {

	CompletableFuture<ComponentConfigurationMTO[]> getComponentConfigurationsAsync(ComponentId componentId);

	CompletableFuture<ComponentDescriptionMTO> getComponentDescriptionAsync(ComponentId componentId);

	CompletableFuture<ComponentDescriptionMTO[]> getComponentDescriptionsAsync(long[] bundleIds);

	CompletableFuture<Boolean> isComponentEnabledAsync(ComponentId componentId);

	CompletableFuture<IStatus> enableComponentAsync(ComponentId componentId);

	CompletableFuture<IStatus> disableComponentAsync(ComponentId componentId);

}
