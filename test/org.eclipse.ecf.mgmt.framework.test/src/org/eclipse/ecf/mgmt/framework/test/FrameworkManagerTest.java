package org.eclipse.ecf.mgmt.framework.test;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.ecf.mgmt.framework.FrameworkMTO;
import org.eclipse.ecf.mgmt.framework.IFrameworkManager;

public class FrameworkManagerTest extends TestCase {

	private IFrameworkManager fm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fm = Activator.getFrameworkManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		fm = null;
	}

	public void testFrameworkMTO() throws Exception {
		FrameworkMTO frameworkMTO = fm.getFramework();
		System.out.println("frameworkMTO.bundles="+Arrays.asList(frameworkMTO.getBundles()));
		System.out.println("frameworkMTO.services="+Arrays.asList(frameworkMTO.getServiceReferences()));
		System.out.println("frameworkMTO.properties="+frameworkMTO.getProperties());
	}

}
