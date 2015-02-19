package org.eclipse.ecf.mgmt.framework.host;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ecf.mgmt.framework.wiring.BundleRevisionMTO;
import org.eclipse.ecf.mgmt.framework.wiring.BundleWiringMTO;
import org.eclipse.ecf.mgmt.framework.wiring.IWiringManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;

public class WiringManager extends AbstractManager implements IWiringManager {

	@Override
	public BundleWiringMTO getBundleWiring(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : new BundleWiringMTO(
				b.adapt(BundleWiringDTO.class));
	}

	@Override
	public BundleWiringMTO[] getInUseBundleWirings(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleWiringMTO.createMTOs(b);
	}

	@Override
	public BundleRevisionMTO getBundleRevision(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : new BundleRevisionMTO(
				b.adapt(BundleRevisionDTO.class));
	}

	@Override
	public BundleRevisionMTO[] getBundleRevisions(int bundleId) {
		Bundle b = getBundle0(bundleId);
		return (b == null) ? null : BundleRevisionMTO.createMTOs(b
				.adapt(BundleRevisionDTO[].class));
	}

	@Override
	public BundleRevisionMTO[] getBundleRevisions(final String symbolicName) {
		List<BundleRevisionMTO> results = new ArrayList<BundleRevisionMTO>();
		if (symbolicName != null) {
			Bundle[] bundles = selectBundles(new BundleSelector() {
				@Override
				public boolean select(Bundle b) {
					return symbolicName.equals(b.getSymbolicName());
				}
			});
			for (Bundle b : bundles)
				results.add(createBundleRevisionMTO(b));
		}
		return results.toArray(new BundleRevisionMTO[results.size()]);
	}

}
