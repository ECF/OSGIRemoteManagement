package org.eclipse.ecf.mgmt.framework.host;

import org.eclipse.ecf.mgmt.framework.wiring.BundleWiringMTO;
import org.eclipse.ecf.mgmt.framework.wiring.IWiringManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.dto.BundleWiringDTO;

public class WiringManager extends AbstractManager implements IWiringManager {

	@Override
	public BundleWiringMTO getBundleWiring(int bundleId) {
		Bundle b = getContext().getBundle(bundleId);
		return (b == null) ? null : new BundleWiringMTO(
				b.adapt(BundleWiringDTO.class));
	}

	@Override
	public BundleWiringMTO[] getInUseWirings(int bundleId) {
		Bundle b = getContext().getBundle(bundleId);
		return (b == null) ? null : BundleWiringMTO.createMTOs(b);
	}

}
