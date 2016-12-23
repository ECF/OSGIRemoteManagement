/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features;

import java.io.Serializable;

public class FeatureEventMTO implements Serializable {

	private static final long serialVersionUID = 1537609967220546691L;
	public static final int INSTALLED = 0x01;
	public static final int UNINSTALLED = 0x02;
	
	private final int type;
	private final boolean replay;
	private final FeatureMTO featureMTO;
	
	public FeatureEventMTO(int type, FeatureMTO feature, boolean replay) {
		this.type = type;
		this.featureMTO = feature;
		this.replay = replay;
	}
	
	public int getType() {
		return type;
	}

	public boolean isReplay() {
		return replay;
	}

	public FeatureMTO getFeatureMTO() {
		return featureMTO;
	}

	@Override
	public String toString() {
		return "FeatureEventMTO [type=" + type + ", replay=" + replay + ", featureMTO=" + featureMTO + "]";
	}


}
