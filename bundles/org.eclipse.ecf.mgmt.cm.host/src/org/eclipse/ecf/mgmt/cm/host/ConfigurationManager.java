/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.cm.host;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.cm.ConfigurationMTO;
import org.eclipse.ecf.mgmt.cm.ConfigurationManagerEvent;
import org.eclipse.ecf.mgmt.cm.IConfigurationManager;
import org.eclipse.ecf.mgmt.cm.IConfigurationManagerListener;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;

public class ConfigurationManager extends AbstractManager implements IConfigurationManager, ConfigurationListener {

	private ConfigurationAdmin configAdmin;

	protected void bindConfigurationAdmin(ConfigurationAdmin ca) {
		this.configAdmin = ca;
	}

	protected void unbindConfigurationAdmin(ConfigurationAdmin ca) {
		this.configAdmin = null;
	}

	protected ConfigurationMTO createConfigurationMTO(Configuration c) {
		return (c == null) ? null
				: new ConfigurationMTO(c.getBundleLocation(), c.getChangeCount(), c.getFactoryPid(), c.getPid(),
						c.getProperties());
	}

	@Override
	public ConfigurationMTO createFactoryConfiguration(String factoryPid) throws Exception {
		return createConfigurationMTO(this.configAdmin.createFactoryConfiguration(factoryPid));
	}

	@Override
	public ConfigurationMTO createFactoryConfiguration(String factoryPid, String location) throws Exception {
		return createConfigurationMTO(this.configAdmin.createFactoryConfiguration(factoryPid, location));
	}

	@Override
	public ConfigurationMTO getConfiguration(String pid) throws Exception {
		return createConfigurationMTO(this.configAdmin.getConfiguration(pid));
	}

	@Override
	public ConfigurationMTO getConfiguration(String pid, String location) throws Exception {
		return createConfigurationMTO(this.configAdmin.getConfiguration(pid, location));
	}

	@Override
	public ConfigurationMTO[] listConfigurations(String filter) throws Exception {
		Configuration[] configs = this.configAdmin.listConfigurations(filter);
		if (configs == null)
			return null;
		List<ConfigurationMTO> results = new ArrayList<ConfigurationMTO>();
		for (Configuration c : configs)
			results.add(createConfigurationMTO(c));
		return results.toArray(new ConfigurationMTO[results.size()]);
	}

	@Override
	public IStatus update(String id) {
		return update(id,null);
	}

	@Override
	public IStatus update(String id, Dictionary<String, ?> properties) {
		if (id == null)
			return createErrorStatus("id must not be null");
		try {
			Configuration c = this.configAdmin.getConfiguration(id);
			if (c == null)
				return createErrorStatus("cannot get configuration for id=" + id);
			if (properties == null)
				c.update();
			else
				c.update(properties);
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not update configuration with id=" + id, e);
		}
	}

	@Override
	public IStatus delete(String id) {
		try {
			Configuration c = this.configAdmin.getConfiguration(id);
			if (c == null)
				return createErrorStatus("cannot get configuration for id=" + id);
			c.delete();
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not delete configuraion with id="+id,e);
		}
	}

	protected List<IConfigurationManagerListener> listeners = new ArrayList<IConfigurationManagerListener>();
	
	protected boolean addConfigurationManagerListener(IConfigurationManagerListener async) {
		synchronized (listeners) {
			return listeners.add(async);
		}
	}
	
	protected boolean removeConfigurationManagerListener(IConfigurationManagerListener async) {
		synchronized (listeners) {
			return listeners.remove(async);
		}
	}
	
	@Override
	public void configurationEvent(ConfigurationEvent event) {
		List<IConfigurationManagerListener> notify = null;
		synchronized (listeners) {
			notify = new ArrayList<IConfigurationManagerListener>(listeners);
		}
		for(IConfigurationManagerListener cml: notify) 
			cml.handleEvent(new ConfigurationManagerEvent(event.getType(),event.getFactoryPid(),event.getPid(),ServiceReferenceMTO.createMTO(event.getReference())));
	}

}
