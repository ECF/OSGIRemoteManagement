/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.identity.host;

import java.net.URI;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IIDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.identity.IDMTO;
import org.eclipse.ecf.mgmt.identity.IIdentityFactoryManager;
import org.eclipse.ecf.mgmt.identity.NamespaceMTO;

public class IdentityFactoryManager extends AbstractManager implements IIdentityFactoryManager {

	private IIDFactory idFactory;

	void bindIDFactory(IIDFactory idFactory) {
		this.idFactory = idFactory;
	}

	void unbindIDFactory(IIDFactory idFactory) {
		this.idFactory = null;
	}

	@SuppressWarnings("rawtypes")
	public static final String[][] convertClassArrayToNameArray(Class[][] clazzes) {
		String[][] results = new String[clazzes.length][];
		for (int i = 0; i < clazzes.length; i++) {
			results[i] = new String[clazzes[i].length];
			for (int j = 0; j < clazzes[i].length; i++)
				results[i][j] = clazzes[i][j].getName();
		}
		return results;
	}

	public static NamespaceMTO createNamespaceMTO(Namespace ns) {
		return ns == null ? null : new NamespaceMTO(ns.getName(), ns.getDescription(), ns.getScheme(),
				ns.getSupportedSchemes(), convertClassArrayToNameArray(ns.getSupportedParameterTypes()));
	}

	public static IDMTO createIDMTO(ID id) {
		return id == null ? null : new IDMTO(createNamespaceMTO(id.getNamespace()), id.getName(), id.toExternalForm());
	}

	public static ID createID(IDMTO mto) {
		if (mto == null) return null;
		IIDFactory f = IDFactory.getDefault();
		Namespace ns = f.getNamespaceByName(mto.getNamespace().getName());
		return (ns == null)?null:f.createID(ns, mto.getName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public NamespaceMTO[] getNamespaces() {
		List<NamespaceMTO> results = selectAndMap(idFactory.getNamespaces(),null,n -> {
			return createNamespaceMTO((Namespace) n);
		});
		return results.toArray(new NamespaceMTO[results.size()]);
	}

	@Override
	public NamespaceMTO getNamespace(String name) {
		Namespace ns = idFactory.getNamespaceByName(name);
		return (ns == null) ? null : createNamespaceMTO(ns);
	}

	IDMTO create0(ID id) {
		try {
			return createIDMTO(id);
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createID(String namespaceName, Object[] args) {
		Namespace ns = idFactory.getNamespaceByName(namespaceName);
		if (ns == null)
			return null;
		return create0(idFactory.createID(ns, args));
	}

	@Override
	public IDMTO createStringID(String id) {
		return create0(idFactory.createStringID(id));
	}

	@Override
	public IDMTO createLongID(long id) {
		return create0(idFactory.createLongID(id));
	}

	@Override
	public IDMTO createGUID(int byteLength) {
		return create0(idFactory.createGUID(byteLength));
	}

	@Override
	public IDMTO createGUID() {
		return create0(idFactory.createGUID());
	}

	@Override
	public IDMTO createURIID(URI uri) {
		return create0(idFactory.createID(URIID.class.getName(), new Object[] { uri }));
	}

}
