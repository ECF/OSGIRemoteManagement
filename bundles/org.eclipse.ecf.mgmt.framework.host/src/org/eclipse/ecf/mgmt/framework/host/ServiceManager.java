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

import java.util.List;
import java.util.function.Function;

import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.osgi.framework.dto.ServiceReferenceDTO;

public class ServiceManager extends AbstractManager implements IServiceManager {

	private static final Function<ServiceReferenceDTO, ServiceReferenceMTO> srmapper = srd -> {
		return ServiceReferenceMTO.createMTO(srd);
	};

	@Override
	public ServiceReferenceMTO[] getServiceReferences() {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, null, srmapper);
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

	@Override
	public ServiceReferenceMTO getServiceReference(final long serviceId) {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, srd -> {
			return srd.id == serviceId;
		}, srmapper);
		return results.size() > 0 ? results.get(0) : null;
	}

	@Override
	public ServiceReferenceMTO[] getServiceReferences(final long bundleId) {
		List<ServiceReferenceMTO> results = selectAndMap(getFrameworkDTO().services, srd -> {
			return srd.bundle == bundleId;
		}, srmapper);
		return results.toArray(new ServiceReferenceMTO[results.size()]);
	}

}
