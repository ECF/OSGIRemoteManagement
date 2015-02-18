package org.eclipse.ecf.mgmt.framework.test;

import junit.framework.TestCase;

import org.eclipse.ecf.mgmt.framework.wiring.BundleWiringMTO;
import org.eclipse.ecf.mgmt.framework.wiring.IWiringManager;

public class WiringManagerTest extends TestCase {

	private IWiringManager wm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		wm = Activator.getWiringManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		wm = null;
	}

	public void testBundleWiring() throws Exception {
		System.out.println("bundle wiring for id=0: "+wm.getBundleWiring(0));
	}
	
	public void testAllBundleWiring() throws Exception {
		BundleWiringMTO[] allWirings = wm.getInUseWirings(0);
		for(BundleWiringMTO mto: allWirings)
			System.out.println("bundle wiring: "+mto);
	}

}
