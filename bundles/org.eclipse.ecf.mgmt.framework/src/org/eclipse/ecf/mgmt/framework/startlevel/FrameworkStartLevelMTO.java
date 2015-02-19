/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.startlevel;

import java.io.Serializable;

import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;

public class FrameworkStartLevelMTO implements Serializable {

	private static final long serialVersionUID = -3980645399276634876L;
	private final int startLevel;
	private final int initialBundleStartLevel;

	public FrameworkStartLevelMTO(FrameworkStartLevelDTO dto) {
		this.startLevel = dto.startLevel;
		this.initialBundleStartLevel = dto.initialBundleStartLevel;
	}

	public FrameworkStartLevelMTO(int startLevel, int initialBundleStartLevel) {
		this.startLevel = startLevel;
		this.initialBundleStartLevel = initialBundleStartLevel;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public int getInitialBundleStartLevel() {
		return initialBundleStartLevel;
	}

	@Override
	public String toString() {
		return "FrameworkStartLevelMTO [startLevel=" + startLevel
				+ ", initialBundleStartLevel=" + initialBundleStartLevel + "]";
	}

}
