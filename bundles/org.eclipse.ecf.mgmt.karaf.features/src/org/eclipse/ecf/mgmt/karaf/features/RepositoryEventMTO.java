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

public class RepositoryEventMTO implements Serializable {

	private static final long serialVersionUID = 2614368711506498018L;
	public static final int ADDED = 0x01;
	public static final int REMOVED = 0x02;
	
	private final int type;
	private final boolean replay;
	private final RepositoryMTO repositoryMTO;
	
	public RepositoryEventMTO(int type, RepositoryMTO feature, boolean replay) {
		this.type = type;
		this.repositoryMTO = feature;
		this.replay = replay;
	}
	
	public int getType() {
		return type;
	}

	public boolean isReplay() {
		return replay;
	}

	public RepositoryMTO getRepositoryMTO() {
		return repositoryMTO;
	}

	@Override
	public String toString() {
		return "RepositoryEventMTO [type=" + type + ", replay=" + replay + ", repositoryMTO=" + repositoryMTO + "]";
	}

}
