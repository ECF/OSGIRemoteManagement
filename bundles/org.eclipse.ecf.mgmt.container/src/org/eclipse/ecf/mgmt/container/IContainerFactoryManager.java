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

import org.eclipse.ecf.mgmt.identity.IDMTO;

public interface IContainerFactoryManager {

	ContainerMTO[] getContainers();

	ContainerMTO getContainer(IDMTO containerID);

	ContainerTypeDescriptionMTO[] getContainerTypeDescriptions();
	
	ContainerTypeDescriptionMTO getContainerTypeDescription(String descriptionName);
	
	ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, Object[] args) throws ContainerCreateException;
	
	ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, IDMTO id) throws ContainerCreateException;

	ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, Map<String,?> args) throws ContainerCreateException;

}
