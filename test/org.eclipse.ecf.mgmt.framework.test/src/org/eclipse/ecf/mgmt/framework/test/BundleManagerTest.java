package org.eclipse.ecf.mgmt.framework.test;

import java.util.Arrays;

import org.eclipse.ecf.mgmt.framework.BundleMTO;
import org.eclipse.ecf.mgmt.framework.IBundleManager;

import junit.framework.TestCase;

public class BundleManagerTest extends TestCase {

	private IBundleManager bm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bm = Activator.getBundleManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		bm = null;
	}

	public void testGetBundles() throws Exception {
		for (BundleMTO b : bm.getBundles())
			System.out.println(b);
	}

	public void testGetBundle() throws Exception {
		System.out.println("bundle 0: " + bm.getBundle(0));
	}

	public void testGetSymbolicId() {
		System.out.println("bundles with symbolic id: "
				+ Arrays.asList(bm
						.getBundles("org.eclipse.ecf.mgmt.framework.test")));
	}
}
