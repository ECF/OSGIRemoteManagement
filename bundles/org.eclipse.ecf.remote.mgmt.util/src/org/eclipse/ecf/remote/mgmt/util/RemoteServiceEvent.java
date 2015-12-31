/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remote.mgmt.util;

public class RemoteServiceEvent {

	public static final int ADDED = 1;
	public static final int REMOVED = 2;
	
	private final int eventType;
	private final RemoteServiceHolder holder;
	
	public RemoteServiceEvent(int type, RemoteServiceHolder holder) {
		this.eventType = type;
		this.holder = holder;
	}
	
	public int getType() {
		return this.eventType;
	}
	
	public RemoteServiceHolder getRemoteServiceHolder() {
		return this.holder;
	}
}
