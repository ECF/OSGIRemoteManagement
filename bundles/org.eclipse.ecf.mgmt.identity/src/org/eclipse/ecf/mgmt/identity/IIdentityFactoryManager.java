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

public interface IIdentityFactoryManager {

	NamespaceMTO[] getNamespaces();

	NamespaceMTO getNamespace(String name);

	public IDMTO createID(String namespaceName, Object[] args);

	public IDMTO createStringID(String id);

	public IDMTO createLongID(long id);

	public IDMTO createGUID(int byteLength);

	public IDMTO createGUID();

	public IDMTO createURIID(URI uri);

}
