/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.scr;

import org.eclipse.core.runtime.IStatus;

/**
 * Service component runtime manager service interface.
 * 
 */
public interface ISCRManager {

	public ComponentMTO[] getComponents(long bundleId);

	public ComponentMTO getComponent(long componentId);

	public ComponentMTO[] getComponents();

	public IStatus enable(long id);

	public IStatus disable(long id);
}
