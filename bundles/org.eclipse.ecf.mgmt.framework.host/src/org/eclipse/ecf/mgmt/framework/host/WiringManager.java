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

import org.eclipse.ecf.mgmt.framework.wiring.BundleRevisionMTO;
import org.eclipse.ecf.mgmt.framework.wiring.BundleWiringMTO;
import org.eclipse.ecf.mgmt.framework.wiring.IWiringManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;

public class WiringManager extends AbstractManager implements IWiringManager {

	@Override
	public BundleWiringMTO getBundleWiring(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleWiringMTO.createMTO(b.adapt(BundleWiringDTO.class));
	}

	@Override
	public BundleWiringMTO[] getInUseBundleWirings(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleWiringMTO.createMTOs(b);
	}

	@Override
	public BundleRevisionMTO getBundleRevision(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleRevisionMTO.createMTO(b.adapt(BundleRevisionDTO.class));
	}

	@Override
	public BundleRevisionMTO[] getBundleRevisions(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleRevisionMTO.createMTOs(b.adapt(BundleRevisionDTO[].class));
	}

	@Override
	public BundleRevisionMTO[] getBundleRevisions(final String symbolicName) {
		if (symbolicName == null)
			return null;
		List<BundleRevisionMTO> results = selectAndMap(getAllBundles(), b -> {
			return symbolicName.equals(b.getSymbolicName());
		}, b -> {
			return BundleRevisionMTO.createMTO(b.adapt(BundleRevisionDTO.class));
		});
		return results.toArray(new BundleRevisionMTO[results.size()]);
	}

}
