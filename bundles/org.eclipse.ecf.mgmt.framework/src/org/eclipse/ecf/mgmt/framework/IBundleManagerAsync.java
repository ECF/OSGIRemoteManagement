/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.mgmt.framework.startlevel.BundleStartLevelMTO;
import org.osgi.framework.BundleException;

public interface IBundleManagerAsync {

	CompletableFuture<BundleMTO[]> getBundlesAsync();

	CompletableFuture<BundleMTO> getBundleAsync(long bundleId);

	CompletableFuture<BundleMTO[]> getBundlesAsync(String symbolicId);

	CompletableFuture<IStatus> startAsync(long bundleId);

	CompletableFuture<IStatus> startAsync(long bundleId, int options);

	CompletableFuture<IStatus> stopAsync(long bundleId);

	CompletableFuture<IStatus> stopAsync(long bundleId, int options);

	CompletableFuture<BundleStartLevelMTO> getBundleStartLevelAsync(long bundleId);

	CompletableFuture<Void> setBundleStartlevelAsync(long bundleId, int startLevel);

	CompletableFuture<BundleMTO> installBundleAsync(String url) throws BundleException;

	CompletableFuture<IStatus> uninstallBundleAsync(long bundleId);

	CompletableFuture<IStatus> updateBundleAsync(long bundleId);

	CompletableFuture<IStatus> updateBundleAsync(long bundleId, String url);

}
