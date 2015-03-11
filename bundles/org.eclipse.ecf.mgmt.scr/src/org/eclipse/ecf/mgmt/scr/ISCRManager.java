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

	ComponentMTO[] getComponents(long bundleId);

	ComponentMTO getComponent(long componentId);

	ComponentMTO[] getComponents();

	IStatus enable(long id);

	IStatus disable(long id);

}
