package org.eclipse.ecf.remoteservice.ui.serviceview.model;

public class BundleIdNode extends AbstractServicesNode {

	private final long bundleId;

	public BundleIdNode(long bundleId) {
		this.bundleId = bundleId;
	}

	public long getBundleId() {
		return this.bundleId;
	}
}
