/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.identity;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public interface IIdentityFactoryManagerAsync {

	CompletableFuture<NamespaceMTO[]> getNamespacesAsync();

	CompletableFuture<NamespaceMTO> getNamespaceAsync(String name);

	CompletableFuture<IDMTO> createIDAsync(String namespaceName, Object[] args);

	CompletableFuture<IDMTO> createStringIDAsync(String id);

	CompletableFuture<IDMTO> createLongIDAsync(long id);

	CompletableFuture<IDMTO> createGUIDAsync(int byteLength);

	CompletableFuture<IDMTO> createGUIDAsync();

	CompletableFuture<IDMTO> createURIIDAsync(URI uri);

}
