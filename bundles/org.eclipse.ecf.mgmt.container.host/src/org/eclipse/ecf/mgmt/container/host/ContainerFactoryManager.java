/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.container.host;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.mgmt.container.ContainerCreateException;
import org.eclipse.ecf.mgmt.container.ContainerMTO;
import org.eclipse.ecf.mgmt.container.ContainerTypeDescriptionMTO;
import org.eclipse.ecf.mgmt.container.IContainerFactoryManager;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.identity.IDMTO;
import org.eclipse.ecf.mgmt.identity.NamespaceMTO;
import org.eclipse.ecf.mgmt.identity.host.IdentityFactoryManager;

public class ContainerFactoryManager extends AbstractManager implements IContainerFactoryManager {

	private IContainerManager containerManager;
	private IIDFactory idFactory;

	void bindContainerManager(IContainerManager containerManager) {
		this.containerManager = containerManager;
	}

	void unbindContainerManager(IContainerManager unbindContainerManager) {
		this.containerManager = null;
	}

	void bindIDFactory(IIDFactory idFactory) {
		this.idFactory = idFactory;
	}

	void unbindIDFactory(IIDFactory idFactory) {
		this.idFactory = null;
	}

	ContainerTypeDescriptionMTO createMTO(ContainerTypeDescription ctd) {
		return ctd == null ? null : new ContainerTypeDescriptionMTO(ctd.getName(), ctd.getDescription(),
				ctd.isHidden(), ctd.isServer(), ctd.getSupportedAdapterTypes(),
				IdentityFactoryManager.convertClassArrayToNameArray(ctd.getSupportedParameterTypes()),
				ctd.getSupportedIntents(), ctd.getSupportedConfigs());
	}

	ContainerMTO createMTO(IContainer container) {
		if (container == null)
			return null;
		ID containerID = container.getID();
		ContainerTypeDescription ctd = containerManager.getContainerTypeDescription(containerID);
		IDMTO containerIDMTO = IdentityFactoryManager.createIDMTO(containerID);
		IDMTO connectedIDMTO = IdentityFactoryManager.createIDMTO(container.getConnectedID());
		NamespaceMTO nsMTO = IdentityFactoryManager.createNamespaceMTO(container.getConnectNamespace());
		return new ContainerMTO(containerIDMTO, connectedIDMTO, nsMTO, createMTO(ctd), container.getClass().getName());
	}

	ContainerMTO[] createMTOs(IContainer[] containers) {
		List<ContainerMTO> results = new ArrayList<ContainerMTO>(containers.length);
		for (int i = 0; i < containers.length; i++)
			results.add(createMTO(containers[i]));
		return results.toArray(new ContainerMTO[results.size()]);
	}

	@Override
	public ContainerMTO[] getContainers() {
		return createMTOs(containerManager.getAllContainers());
	}

	ID createID(IDMTO mto) {
		try {
			return idFactory.createID(mto.getNamespace().getName(), mto.getName());
		} catch (IDCreateException e) {
			return null;
		}
	}

	@Override
	public ContainerMTO getContainer(IDMTO containerID) {
		ID id = createID(containerID);
		return id == null ? null : createMTO(containerManager.getContainer(id));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ContainerTypeDescriptionMTO[] getContainerTypeDescriptions() {
		List ctds = containerManager.getContainerFactory().getDescriptions();
		List<ContainerTypeDescriptionMTO> results = new ArrayList<ContainerTypeDescriptionMTO>(ctds.size());
		for (Iterator i = ctds.iterator(); i.hasNext();)
			results.add(createMTO((ContainerTypeDescription) i.next()));
		return results.toArray(new ContainerTypeDescriptionMTO[results.size()]);
	}

	ContainerTypeDescription getDescription(String descriptionName) {
		return containerManager.getContainerFactory().getDescriptionByName(descriptionName);
	}

	@Override
	public ContainerTypeDescriptionMTO getContainerTypeDescription(String descriptionName) {
		return createMTO(getDescription(descriptionName));
	}

	@Override
	public ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, Object[] args)
			throws ContainerCreateException {
		ContainerTypeDescription d = getDescription(desc.getName());
		if (d == null)
			throw new ContainerCreateException("Container type description with name=" + desc.getName() + " not found");
		try {
			return createMTO(containerManager.getContainerFactory().createContainer(d, args));
		} catch (org.eclipse.ecf.core.ContainerCreateException e) {
			throw new ContainerCreateException("Could not create container with name=" + desc.getName(), e);
		}
	}

	@Override
	public ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, IDMTO id) throws ContainerCreateException {
		return createContainer(desc, new Object[] { createID(id) });
	}

	@Override
	public ContainerMTO createContainer(ContainerTypeDescriptionMTO desc, Map<String, ?> args)
			throws ContainerCreateException {
		return createContainer(desc, new Object[] { args });
	}

}
