package org.eclipse.ecf.mgmt.framework.test;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.ecf.mgmt.framework.IServiceManager;
import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;

public class ServiceManagerTest extends TestCase {

	private IServiceManager sm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sm = Activator.getServiceManager();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sm = null;
	}

	public void testGetServices() throws Exception {
		for (ServiceReferenceMTO srm : sm.getServiceReferences())
			System.out.println(srm);
	}

	public void testGetService() throws Exception {
		System.out.println("service 1: " + sm.getServiceReference(1));
	}

	public void testGetBundleServices() {
		System.out.println("services with bundle id=0: "
				+ Arrays.asList(sm.getServiceReferences(0)));
	}
}
