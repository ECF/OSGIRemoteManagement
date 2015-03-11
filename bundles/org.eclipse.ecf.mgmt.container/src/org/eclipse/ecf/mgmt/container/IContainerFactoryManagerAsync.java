/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.container;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.ecf.mgmt.identity.IDMTO;

public interface IContainerFactoryManagerAsync {

	CompletableFuture<ContainerMTO[]> getContainers();

	CompletableFuture<ContainerMTO> getContainerAsync(IDMTO containerID);

	CompletableFuture<ContainerTypeDescriptionMTO[]> getContainerTypeDescriptionsAsync();

	CompletableFuture<ContainerTypeDescriptionMTO> getContainerTypeDescriptionAsync(
			String descriptionName);

	CompletableFuture<ContainerMTO> createContainerAsync(
			ContainerTypeDescriptionMTO desc, Object[] args)
			throws ContainerCreateException;

	CompletableFuture<ContainerMTO> createContainerAsync(
			ContainerTypeDescriptionMTO desc, IDMTO id)
			throws ContainerCreateException;

	CompletableFuture<ContainerMTO> createContainerAsync(
			ContainerTypeDescriptionMTO desc, Map<String, ?> args)
			throws ContainerCreateException;

}
