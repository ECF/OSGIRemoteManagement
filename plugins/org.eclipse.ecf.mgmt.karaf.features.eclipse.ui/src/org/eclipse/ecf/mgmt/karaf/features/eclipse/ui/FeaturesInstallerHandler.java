/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.ecf.mgmt.karaf.features.FeatureEventMTO;
import org.eclipse.ecf.mgmt.karaf.features.RepositoryEventMTO;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;

public interface FeaturesInstallerHandler {

	public void handleFeatureEvent(IRemoteServiceID rsID, FeatureEventMTO bundleEvent);

	public void handleRepoEvent(IRemoteServiceID rsID, RepositoryEventMTO repoEvent);
}
