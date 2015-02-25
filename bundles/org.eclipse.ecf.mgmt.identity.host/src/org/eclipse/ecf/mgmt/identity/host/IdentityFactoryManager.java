package org.eclipse.ecf.mgmt.identity.host;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
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
	static final String[][] convertClassArrayToNameArray(Class[][] clazzes) {
		String[][] results = new String[clazzes.length][];
		for (int i = 0; i < clazzes.length; i++) {
			results[i] = new String[clazzes[i].length];
			for (int j = 0; j < clazzes[i].length; i++)
				results[i][j] = clazzes[i][j].getName();
		}
		return results;
	}

	NamespaceMTO createNamespaceMTO(Namespace ns) {
		return new NamespaceMTO(ns.getName(), ns.getDescription(), ns.getScheme(), ns.getSupportedSchemes(),
				convertClassArrayToNameArray(ns.getSupportedParameterTypes()));
	}

	IDMTO createIDMTO(ID id) {
		return new IDMTO(createNamespaceMTO(id.getNamespace()), id.getName(), id.toExternalForm());
	}

	@Override
	public NamespaceMTO[] getNamespaces() {
		@SuppressWarnings("rawtypes")
		List namespaces = idFactory.getNamespaces();
		List<NamespaceMTO> results = new ArrayList<NamespaceMTO>();
		for (@SuppressWarnings("rawtypes")
		Iterator i = namespaces.iterator(); i.hasNext();)
			results.add(createNamespaceMTO((Namespace) i.next()));
		return results.toArray(new NamespaceMTO[results.size()]);
	}

	@Override
	public NamespaceMTO getNamespace(String name) {
		Namespace ns = idFactory.getNamespaceByName(name);
		return (ns == null) ? null : createNamespaceMTO(ns);
	}

	@Override
	public IDMTO createID(String namespaceName, Object[] args) {
		Namespace ns = idFactory.getNamespaceByName(namespaceName);
		if (ns == null)
			return null;
		try {
			return createIDMTO(idFactory.createID(ns, args));
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createStringID(String id) {
		try {
			return createIDMTO(idFactory.createStringID(id));
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createLongID(long id) {
		try {
			return createIDMTO(idFactory.createLongID(id));
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createGUID(int byteLength) {
		try {
			return createIDMTO(idFactory.createGUID(byteLength));
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createGUID() {
		try {
			return createIDMTO(idFactory.createGUID());
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public IDMTO createURIID(URI uri) {

		try {
			return createIDMTO(idFactory.createID(URIID.class.getName(), new Object[] { uri }));
		} catch (org.eclipse.ecf.core.identity.IDCreateException e) {
			throw new org.eclipse.ecf.mgmt.identity.IDCreateException(e.getMessage(), e.getCause());
		}
	}

}
