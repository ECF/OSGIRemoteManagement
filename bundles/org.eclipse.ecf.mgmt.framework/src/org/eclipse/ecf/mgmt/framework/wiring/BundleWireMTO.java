/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.wiring;

import java.io.Serializable;

import org.eclipse.ecf.mgmt.framework.resource.WireMTO;
import org.osgi.framework.wiring.dto.BundleWireDTO;

public class BundleWireMTO extends WireMTO implements Serializable {

	private static final long serialVersionUID = 1405525788655454464L;
	private final int providerWiring;
	private final int requirerWiring;

	public BundleWireMTO(BundleWireDTO dto) {
		super(dto);
		this.providerWiring = dto.providerWiring;
		this.requirerWiring = dto.requirerWiring;
	}

	public int getProviderWiring() {
		return providerWiring;
	}

	public int getRequirerWiring() {
		return requirerWiring;
	}

	@Override
	public String toString() {
		return "BundleWireMTO [providerWiring=" + providerWiring
				+ ", requirerWiring=" + requirerWiring + ", capability="
				+ getCapability() + ", requirement=" + getRequirement()
				+ ", provider=" + getProvider() + ", requirer="
				+ getRequirer() + "]";
	}

}
