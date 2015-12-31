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
