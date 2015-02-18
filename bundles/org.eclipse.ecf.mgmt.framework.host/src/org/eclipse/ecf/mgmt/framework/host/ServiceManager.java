/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.host;

import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

public class ServiceManager extends AbstractManager implements IServiceManager {

	@Override
	public ServiceReferenceMTO[] getServiceReferences() {
		return selectServiceReferenceMTOs(null);
	}

	@Override
	public ServiceReferenceMTO getServiceReference(final long serviceId) {
		ServiceReferenceMTO[] mtos = selectServiceReferenceMTOs(new ServiceReferenceDTOSelector() {
			@Override
			public boolean select(ServiceReferenceDTO srd) {
				return srd.id == serviceId;
			}
		});
		return (mtos.length == 1) ? mtos[0] : null;
	}

	@Override
	public ServiceReferenceMTO[] getServiceReferences(final long bundleId) {
		ServiceReferenceMTO[] mtos = selectServiceReferenceMTOs(new ServiceReferenceDTOSelector() {
			@Override
			public boolean select(ServiceReferenceDTO srd) {
				return srd.bundle == bundleId;
			}
		});
		return mtos;
	}

}
