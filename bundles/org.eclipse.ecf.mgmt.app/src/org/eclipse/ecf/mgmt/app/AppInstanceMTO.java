/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.app;

import java.io.Serializable;

public class AppInstanceMTO implements Serializable {

	private static final long serialVersionUID = -6573093046411186607L;
	private final String id;
	private final String state;
	private final AppMTO app;

	public AppInstanceMTO(String id, String state, AppMTO app) {
		this.id = id;
		this.state = state;
		this.app = app;
	}

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public AppMTO getApp() {
		return app;
	}

	@Override
	public String toString() {
		return "AppInstanceMTO [id=" + id + ", state=" + state + ", app=" + app + "]";
	}

}
