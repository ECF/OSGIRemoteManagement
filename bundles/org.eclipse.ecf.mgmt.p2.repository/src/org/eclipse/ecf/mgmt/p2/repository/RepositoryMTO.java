package org.eclipse.ecf.mgmt.p2.repository;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

public class RepositoryMTO implements Serializable {

	private static final long serialVersionUID = 3700183070780417687L;
	private final URI location;
	private final String name;
	private final String description;
	private final String type;
	private final String provider;
	private final String version;
	private final Map<String, ?> properties;
	private boolean modifiable;

	public RepositoryMTO(URI location, String name, String description,
			String type, String provider, String version,
			Map<String, ?> properties, boolean modifiable) {
		this.location = location;
		this.name = name;
		this.description = description;
		this.type = type;
		this.provider = provider;
		this.version = version;
		this.properties = properties;
		this.modifiable = modifiable;
	}

	public String getDescription() {
		return description;
	}

	public URI getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public Map<String, ?> getProperties() {
		return properties;
	}

	public String getProvider() {
		return provider;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("RepositoryMTO[description=");
		buffer.append(description);
		buffer.append(", location=");
		buffer.append(location);
		buffer.append(", modifiable=");
		buffer.append(modifiable);
		buffer.append(", name=");
		buffer.append(name);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", provider=");
		buffer.append(provider);
		buffer.append(", type=");
		buffer.append(type);
		buffer.append(", version=");
		buffer.append(version);
		buffer.append("]");
		return buffer.toString();
	}

}
