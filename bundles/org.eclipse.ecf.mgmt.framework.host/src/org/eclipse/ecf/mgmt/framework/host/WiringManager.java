package org.eclipse.ecf.mgmt.framework.host;

import org.eclipse.ecf.mgmt.framework.wiring.BundleRevisionMTO;
import org.eclipse.ecf.mgmt.framework.wiring.BundleWiringMTO;
import org.eclipse.ecf.mgmt.framework.wiring.IWiringManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;

public class WiringManager extends AbstractManager implements IWiringManager {

	private Bundle getBundle(int bundleId) {
		return getContext().getBundle(bundleId);
	}

	@Override
	public BundleWiringMTO getBundleWiring(int bundleId) {
		Bundle b = getBundle(bundleId);
		return (b == null) ? null : new BundleWiringMTO(
				b.adapt(BundleWiringDTO.class));
	}

	@Override
	public BundleWiringMTO[] getInUseWirings(int bundleId) {
		Bundle b = getBundle(bundleId);
		return (b == null) ? null : BundleWiringMTO.createMTOs(b);
	}

	@Override
	public BundleRevisionMTO getBundleRevision(int bundleId) {
		Bundle b = getBundle(bundleId);
		return (b == null) ? null : new BundleRevisionMTO(
				b.adapt(BundleRevisionDTO.class));
	}

	@Override
	public BundleRevisionMTO[] getBundleRevisions(int bundleId) {
		Bundle b = getBundle(bundleId);
		return (b == null) ? null : BundleRevisionMTO.createMTOs(b
				.adapt(BundleRevisionDTO[].class));
	}

}
