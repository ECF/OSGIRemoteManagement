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

import org.eclipse.core.runtime.IStatus;

public interface IConfigurationManager {

	ConfigurationMTO createFactoryConfiguration(String factoryPid) throws Exception;
	ConfigurationMTO createFactoryConfiguration(String factoryPid, String location) throws Exception;
	ConfigurationMTO getConfiguration(String pid) throws Exception;
	ConfigurationMTO getConfiguration(String pid, String location) throws Exception;
	ConfigurationMTO[] listConfigurations(String filter) throws Exception;
	
	IStatus update(String id);
	IStatus update(String id, Dictionary<String,?> properties);
	
	IStatus delete(String id);
	
}
