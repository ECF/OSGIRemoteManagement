/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.registry;

public interface IExtensionRegistryManager {
	public String[] getExtensionPointIds();

	ExtensionPointMTO getExtensionPoint(String extensionPointId);

	ExtensionPointMTO[] getExtensionPointsForContributor(String contributorId);

	ExtensionPointMTO[] getExtensionPoints();

	ExtensionMTO getExtension(String extensionPointId, String extensionId);

	ExtensionMTO[] getExtensionsForContributor(String contributorId);

	ExtensionMTO[] getExtensions(String extensionPointId);

	ConfigurationElementMTO[] getConfigurationElements(String extensionPointId);

}
