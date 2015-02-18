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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class BundleManager extends AbstractManager implements IBundleManager {

	@Override
	public BundleMTO[] getBundles() {
		return selectBundleMTOs(null);
	}

	@Override
	public BundleMTO getBundle(long bundleId) {
		Bundle b = getContext().getBundle(bundleId);
		return (b == null) ? null : createBundleMTO(b);
	}

	@Override
	public BundleMTO[] getBundles(final String symbolicName) {
		return selectBundleMTOs(new BundleSelector() {
			public boolean select(Bundle b) {
				return (b.getSymbolicName().equals(symbolicName));
			}
		});
	}

	private IStatus startstop(final long bundleId, boolean start) {
		Bundle bundle = selectBundle(new BundleSelector() {
			@Override
			public boolean select(Bundle b) {
				return b.getBundleId() == bundleId;
			}
		});
		if (bundle == null)
			return createErrorStatus("Cannot find bundle with bundleId="
					+ bundleId, new NullPointerException());

		try {
			if (start)
				bundle.start();
			else
				bundle.stop();
			return new SerializableStatus(Status.OK_STATUS);
		} catch (BundleException e) {
			return createErrorStatus(
					"Exception starting " + bundle.getSymbolicName() + " version " + bundle.getVersion().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public IStatus start(long bundleId) {
		return startstop(bundleId, true);
	}

	@Override
	public IStatus stop(long bundleId) {
		return startstop(bundleId, false);
	}

}
