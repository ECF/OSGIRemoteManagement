package org.eclipse.ecf.mgmt.identity;

import java.io.Serializable;

public class IDMTO implements Serializable {

	private static final long serialVersionUID = -629589443879094652L;

	private final String name;
	private final NamespaceMTO namespace;
	private final String externalForm;

	public IDMTO(NamespaceMTO namespace, String name, String externalForm) {
		this.namespace = namespace;
		this.name = name;
		this.externalForm = externalForm;
	}

	public String getName() {
		return name;
	}

	public NamespaceMTO getNamespace() {
		return namespace;
	}

	public String getExternalForm() {
		return externalForm;
	}

	@Override
	public String toString() {
		return "IDMTO [name=" + name + ", namespace=" + namespace + ", externalForm=" + externalForm + "]";
	}

}
