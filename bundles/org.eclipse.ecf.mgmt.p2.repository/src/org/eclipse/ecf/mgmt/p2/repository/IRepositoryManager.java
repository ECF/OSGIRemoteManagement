/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.p2.repository;

import java.net.URI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.p2.InstallableUnitMTO;

/**
 * Repository manager service interface.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 */
public interface IRepositoryManager {

	URI[] getKnownMetadataRepositories();

	URI[] getKnownArtifactRepositories();

	IStatus addArtifactRepository(URI location);

	IStatus addMetadataRepository(URI location);

	IStatus removeArtifactRepository(URI location);

	IStatus removeMetadataRepository(URI location);

	IStatus addRepository(URI location);

	IStatus removeRepository(URI location);

	IStatus refreshArtifactRepository(URI location);

	IStatus refreshMetadataRepository(URI location);

	IStatus refreshRepository(URI location);

	RepositoryMTO[] getArtifactRepositories();

	RepositoryMTO getArtifactRepository(URI location);

	RepositoryMTO[] getMetadataRepositories();

	RepositoryMTO getMetadataRepository(URI location);

	InstallableUnitMTO[] getInstallableFeatures(URI location);
}
