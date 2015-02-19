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

import org.osgi.framework.startlevel.dto.BundleStartLevelDTO;

public class BundleStartLevelMTO implements Serializable {

	private static final long serialVersionUID = 8845908471993928996L;
	private final long bundle;
	private final int startLevel;
	private final boolean activationPolicyUsed;
	private final boolean persistentlyStarted;

	public BundleStartLevelMTO(BundleStartLevelDTO dto) {
		this.bundle = dto.bundle;
		this.startLevel = dto.startLevel;
		this.activationPolicyUsed = dto.activationPolicyUsed;
		this.persistentlyStarted = dto.persistentlyStarted;
	}

	public BundleStartLevelMTO(long bundleId, int startLevel,
			boolean activationPolicyUsed, boolean persistentlyStarted) {
		this.bundle = bundleId;
		this.startLevel = startLevel;
		this.activationPolicyUsed = activationPolicyUsed;
		this.persistentlyStarted = persistentlyStarted;
	}

	public long getBundle() {
		return bundle;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public boolean isActivationPolicyUsed() {
		return activationPolicyUsed;
	}

	public boolean isPersistentlyStarted() {
		return persistentlyStarted;
	}

	@Override
	public String toString() {
		return "BundleStartLevelMTO [bundle=" + bundle + ", startLevel="
				+ startLevel + ", activationPolicyUsed=" + activationPolicyUsed
				+ ", persistentlyStarted=" + persistentlyStarted + "]";
	}

}
