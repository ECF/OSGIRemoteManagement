/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.host;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.ecf.core.status.SerializableMultiStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.p2.CopyrightMTO;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;
import org.eclipse.ecf.mgmt.p2.LicenseMTO;
import org.eclipse.ecf.mgmt.p2.VersionedId;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.ICopyright;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.ILicense;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

public class AbstractP2Manager extends AbstractManager {

	protected IProvisioningAgent agent;

	protected void bindProvisioningAgent(IProvisioningAgent agent) {
		this.agent = agent;
	}

	protected void unbindProvisioningAgent(IProvisioningAgent agent) {
		this.agent = null;
	}

	protected InstallableUnitMTO createInstallableUnitMTO(IInstallableUnit iu) {
		Version v = iu.getVersion();
		ICopyright copyright = iu.getCopyright();
		Collection<ILicense> licenses = iu.getLicenses();
		return new InstallableUnitMTO(new VersionedId(iu.getId(),
				(v == null) ? null : v.toString()), iu.getProperties(),
				iu.isSingleton(), iu.isResolved(), licenses == null ? null
						: createLicenses(iu.getLicenses()),
				copyright == null ? null : new CopyrightMTO(copyright
						.getLocation(), copyright.getBody()));
	}

	protected LicenseMTO[] createLicenses(Collection<ILicense> ls) {
		List<LicenseMTO> results = selectAndMap(
				new ArrayList<ILicense>(ls),
				null,
				l -> {
					return new LicenseMTO(l.getLocation(), l.getBody(), l
							.getUUID());
				});
		return results.toArray(new LicenseMTO[results.size()]);
	}

	protected InstallableUnitMTO[] getInstallableUnitsMTO(
			IInstallableUnit[] units) {
		if (units == null)
			return null;
		List<InstallableUnitMTO> results = selectAndMap(Arrays.asList(units),
				null, un -> {
					return createInstallableUnitMTO(un);
				});
		return results.toArray(new InstallableUnitMTO[results.size()]);
	}

	protected IStatus serializeStatus(IStatus status) {
		if (status == null)
			return null;
		if (status.isMultiStatus())
			return new SerializableMultiStatus(new MultiStatus(
					status.getPlugin(), status.getCode(), status.getChildren(),
					status.getMessage(), status.getException()));
		else
			return new SerializableStatus(status);
	}

	protected IProfileRegistry getProfileRegistry() {
		return (IProfileRegistry) agent
				.getService(IProfileRegistry.SERVICE_NAME);
	}

	protected IMetadataRepositoryManager getMetadataRepositoryManager() {
		return (IMetadataRepositoryManager) agent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
	}

	protected IArtifactRepositoryManager getArtifactRepositoryManager() {
		return (IArtifactRepositoryManager) agent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
	}

}
