package org.eclipse.ecf.mgmt.identity;

import org.eclipse.ecf.mgmt.PropertiesUtil;

public class IDCreateException extends RuntimeException {

	private static final long serialVersionUID = 2861221573666918411L;

	public IDCreateException(String message) {
		super(message);
	}

	public IDCreateException(Throwable cause) {
		super(PropertiesUtil.isSerializable(cause) ? cause : new Throwable(cause.getMessage()));
	}

	public IDCreateException(String message, Throwable cause) {
		super(message, PropertiesUtil.isSerializable(cause) ? cause : new Throwable(cause.getMessage()));
	}

}
