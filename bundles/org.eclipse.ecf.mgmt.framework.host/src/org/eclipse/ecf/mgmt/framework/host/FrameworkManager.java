/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.host;

import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.framework.FrameworkMTO;
import org.eclipse.ecf.mgmt.framework.IFrameworkManager;
import org.eclipse.ecf.mgmt.framework.startlevel.FrameworkStartLevelMTO;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;

public class FrameworkManager extends AbstractManager implements IFrameworkManager {

	private static final long DEFAULT_SETSTARTLEVEL_TIMEOUT = new Long(System.getProperty(
			"org.eclipse.ecf.mgmt.framework.host.defaultSetStartLevelTimeout", "30000"));

	private long setStartLevelTimeout = DEFAULT_SETSTARTLEVEL_TIMEOUT;

	@Override
	public FrameworkMTO getFramework() {
		return createFrameworkMTO();
	}

	@Override
	public FrameworkStartLevelMTO getStartLevel() {
		return new FrameworkStartLevelMTO(getBundle0(0).adapt(FrameworkStartLevelDTO.class));
	}

	class ManagerFrameworkListener implements FrameworkListener {
		public boolean done = false;
		public IStatus status;

		@Override
		public void frameworkEvent(FrameworkEvent event) {
			synchronized (this) {
				this.status = (event.getType() == FrameworkEvent.ERROR) ? createErrorStatus(
						"Framework error on setStartLevel", event.getThrowable()) : SerializableStatus.OK_STATUS;
				done = true;
			}
		}
	}

	@Override
	public IStatus setStartLevel(int startLevel) {
		final FrameworkStartLevel fsl = getBundle0(0).adapt(FrameworkStartLevel.class);
		final ManagerFrameworkListener fl = new ManagerFrameworkListener();
		fsl.setStartLevel(startLevel, fl);
		long timeoutTime = System.currentTimeMillis() + setStartLevelTimeout;
		synchronized (fl) {
			while (!fl.done && (timeoutTime - System.currentTimeMillis() > 0))
				try {
					fl.wait(setStartLevelTimeout / 20);
				} catch (InterruptedException e) {
					fl.status = createErrorStatus("setStartLevel interrupted", e);
					fl.done = true;
				}
		}
		return (fl.done) ? fl.status : createErrorStatus("setStartLevel timeout after " + setStartLevelTimeout + " ms",
				new TimeoutException());
	}

	@Override
	public void setInitialBundleStartLevel(int initialBundleStartLevel) {
		getFrameworkBundle().adapt(FrameworkStartLevel.class).setInitialBundleStartLevel(initialBundleStartLevel);
	}

}
