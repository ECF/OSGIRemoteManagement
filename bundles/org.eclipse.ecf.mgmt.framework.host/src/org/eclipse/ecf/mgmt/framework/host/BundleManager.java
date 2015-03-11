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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.BundleInstallException;
import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.IBundleManager;
import org.eclipse.ecf.mgmt.framework.startlevel.BundleStartLevelMTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;

public class BundleManager extends AbstractManager implements IBundleManager {

	private static final Function<Bundle, BundleMTO> mapper = b -> {
		return BundleMTO.createMTO(b);
	};

	@Override
	public BundleMTO[] getBundles() {
		List<BundleMTO> results = selectAndMap(getAllBundles(), null, mapper);
		return results.toArray(new BundleMTO[results.size()]);
	}

	@Override
	public BundleMTO getBundle(long bundleId) {
		return BundleMTO.createMTO(getBundle0(bundleId));
	}

	@Override
	public BundleMTO[] getBundles(final String symbolicName) {
		List<BundleMTO> results = selectAndMap(getAllBundles(), b -> {
			return b.getSymbolicName().equals(symbolicName);
		}, mapper);
		return results.toArray(new BundleMTO[results.size()]);
	}

	private IStatus startstop(final long bundleId, int options, boolean start) {
		Bundle bundle = getBundle0(bundleId);
		if (bundle == null)
			return createErrorStatus("Cannot find bundle with bundleId="
					+ bundleId, new NullPointerException());
		try {
			if (start)
				bundle.start(options);
			else
				bundle.stop(options);
			return new SerializableStatus(Status.OK_STATUS);
		} catch (BundleException e) {
			return createErrorStatus(
					"Exception starting " + bundle.getSymbolicName() + " version " + bundle.getVersion().toString(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public IStatus start(long bundleId) {
		return startstop(bundleId, 0, true);
	}

	@Override
	public IStatus stop(long bundleId) {
		return startstop(bundleId, 0, false);
	}

	@Override
	public IStatus start(long bundleId, int options) {
		return startstop(bundleId, options, true);
	}

	@Override
	public IStatus stop(long bundleId, int options) {
		return startstop(bundleId, options, false);
	}

	@Override
	public BundleStartLevelMTO getBundleStartLevel(long bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : new BundleStartLevelMTO(
				b.adapt(BundleStartLevelDTO.class));
	}

	@Override
	public void setBundleStartlevel(long bundleId, int startLevel) {
		Bundle b = getBundle0(bundleId);
		if (b == null)
			return;
		b.adapt(BundleStartLevel.class).setStartLevel(startLevel);
	}

	@Override
	public BundleMTO installBundle(String url) throws BundleInstallException {
		try {
			return BundleMTO.createMTO(getContext().installBundle(url));
		} catch (BundleException e) {
			logError("Cannot install bundle with url=" + url, e);
			throw new BundleInstallException("Cannot install bundle with url="
					+ url);
		}
	}

	@Override
	public IStatus uninstallBundle(long bundleId) {
		Bundle b = getBundle0(bundleId);
		if (b == null)
			return createErrorStatus("Bundle with id=" + bundleId
					+ " not found to uninstall", new NullPointerException());
		try {
			b.uninstall();
			return SerializableStatus.OK_STATUS;
		} catch (BundleException e) {
			return createErrorStatus("Could not uninstall bundle=" + bundleId,
					e);
		}
	}

	public IStatus updateBundle(long bundleId) {
		Bundle b = getBundle0(bundleId);
		if (b == null)
			return createErrorStatus("Bundle with id=" + bundleId
					+ " not found to update", new NullPointerException());
		try {
			b.update();
			return SerializableStatus.OK_STATUS;
		} catch (BundleException e) {
			return createErrorStatus("Cannot update bundle=" + bundleId, e);
		}
	}

	public IStatus updateBundle(long bundleId, String urlString) {
		Bundle b = getBundle0(bundleId);
		if (b == null)
			return createErrorStatus("Bundle with id=" + bundleId
					+ " and urlString=" + urlString + " not found to update",
					new NullPointerException());
		try {
			b.update(new URL(urlString).openStream());
			return SerializableStatus.OK_STATUS;
		} catch (BundleException e) {
			return createErrorStatus(
					"Cannot update bundle with id=" + bundleId, e);
		} catch (IOException e) {
			return createErrorStatus("Cannot read from url=" + urlString
					+ " to load bundleId=" + bundleId, e);
		}
	}

}
