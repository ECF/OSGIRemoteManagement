package org.eclipse.ecf.mgmt.p2.profile;

import java.io.Serializable;
import java.util.Map;

public class ProfileMTO implements Serializable {

	private static final long serialVersionUID = -5885606209963025744L;
	private String id;
	private final Map<String, String> properties;
	private final long timestamp;

	public ProfileMTO(String id, Map<String, String> properties, long timestamp) {
		this.id = id;
		this.properties = properties;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ProfileMTO[id=");
		buffer.append(id);
		buffer.append(", properties=");
		buffer.append(properties);
		buffer.append(", timestamp=");
		buffer.append(timestamp);
		buffer.append("]");
		return buffer.toString();
	}

}
