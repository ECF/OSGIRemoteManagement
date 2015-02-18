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

import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.osgi.framework.Bundle;

public class BundleManager extends AbstractManager implements IBundleManager {

	@Override
	public BundleMTO[] getBundles() {
		return findBundleMTOs(null);
	}

	@Override
	public BundleMTO getBundle(long bundleId) {
		Bundle b = getContext().getBundle(bundleId);
		return (b == null) ? null : createBundleMTO(b);
	}

	@Override
	public BundleMTO[] getBundles(final String symbolicName) {
		return findBundleMTOs(new BundleSelector() {
			public boolean select(Bundle b) {
				return (b.getSymbolicName().equals(symbolicName));
			}
		});
	}

}
