/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.wiring;

public interface IWiringManager {

	public BundleWiringMTO getBundleWiring(int bundleId);

	public BundleWiringMTO[] getInUseBundleWirings(int bundleId);

	public BundleRevisionMTO getBundleRevision(int bundleId);

	public BundleRevisionMTO[] getBundleRevisions(int bundleId);

	public BundleRevisionMTO[] getBundleRevisions(String symbolicName);

}
