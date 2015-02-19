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
import org.eclipse.ecf.mgmt.framework.startlevel.FrameworkStartLevelMTO;

public interface IFrameworkManagerAsync {

	CompletableFuture<FrameworkMTO> getFrameworkAsync();

	CompletableFuture<FrameworkStartLevelMTO> getStartLevelAsync();

	CompletableFuture<IStatus> setStartLevelAsync(int startLevel);

	CompletableFuture<Void> setInitialBundleStartLevelAsync(int initialBundleStartLevel);

}
