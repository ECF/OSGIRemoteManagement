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

import java.io.Serializable;

import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;

public class ConfigurationManagerEvent implements Serializable {

	private static final long serialVersionUID = 1088875826594718882L;
	private final int type;
	private final String factoryPid;
	private final String pid;
	private final ServiceReferenceMTO reference;
	
	public ConfigurationManagerEvent(int type, String factoryPid, String pid, ServiceReferenceMTO ref) {
		this.type = type;
		this.factoryPid = factoryPid;
		this.pid = pid;
		this.reference = ref;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getType() {
		return type;
	}

	public String getFactoryPid() {
		return factoryPid;
	}

	public String getPid() {
		return pid;
	}

	public ServiceReferenceMTO getReference() {
		return reference;
	}

	@Override
	public String toString() {
		return "ConfigurationManagerEvent [type=" + type + ", factoryPid=" + factoryPid + ", pid=" + pid
				+ ", reference=" + reference + "]";
	}
	
}
