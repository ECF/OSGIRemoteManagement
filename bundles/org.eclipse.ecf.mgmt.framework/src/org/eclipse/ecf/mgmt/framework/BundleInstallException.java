package org.eclipse.ecf.mgmt.framework;

public class BundleInstallException extends RuntimeException {

	private static final long serialVersionUID = -4906841930169620674L;

	public BundleInstallException(String message, Throwable cause) {
		super(message, cause);
	}

	public BundleInstallException(String message) {
		super(message);
	}

	public BundleInstallException(Throwable cause) {
		super(cause);
	}

}
