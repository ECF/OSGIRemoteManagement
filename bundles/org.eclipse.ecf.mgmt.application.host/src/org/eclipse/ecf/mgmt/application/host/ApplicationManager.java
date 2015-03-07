/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.application.host;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.application.ApplicationInstanceMTO;
import org.eclipse.ecf.mgmt.application.ApplicationMTO;
import org.eclipse.ecf.mgmt.application.IApplicationManager;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

public class ApplicationManager extends AbstractManager implements IApplicationManager {

	private ServiceTracker<ApplicationDescriptor, ApplicationDescriptor> appDescTracker;
	private ServiceTracker<ApplicationHandle, ApplicationHandle> appInstTracker;

	@Override
	protected void activate(BundleContext context) throws Exception {
		super.activate(context);
		appDescTracker = new ServiceTracker<ApplicationDescriptor, ApplicationDescriptor>(context,
				ApplicationDescriptor.class, null);
		appDescTracker.open();
		appInstTracker = new ServiceTracker<ApplicationHandle, ApplicationHandle>(context, ApplicationHandle.class,
				null);
		appInstTracker.open();
	}

	@Override
	protected void deactivate() throws Exception {
		if (appDescTracker != null) {
			appDescTracker.close();
			appDescTracker = null;
		}
		if (appInstTracker != null) {
			appInstTracker.close();
			appInstTracker = null;
		}
		super.deactivate();
	}

	@SuppressWarnings("unchecked")
	protected ApplicationMTO createAppMTO(ApplicationDescriptor appDescriptor) {
		return new ApplicationMTO(appDescriptor.getApplicationId(), (Map<String, ?>) appDescriptor.getProperties(null));
	}

	protected ApplicationMTO createAppMTO(ServiceReference<ApplicationDescriptor> appDescSR) {
		ApplicationDescriptor appDescriptor = getContext().getService(appDescSR);
		ApplicationMTO result = (appDescriptor != null) ? createAppMTO(appDescriptor) : null;
		getContext().ungetService(appDescSR);
		return result;
	}

	protected ApplicationInstanceMTO createAppInstanceMTO(ServiceReference<ApplicationHandle> appInstSR) {
		ApplicationHandle appInstanceHandle = getContext().getService(appInstSR);
		ApplicationInstanceMTO result = (appInstanceHandle != null) ? new ApplicationInstanceMTO(appInstanceHandle.getInstanceId(),
				appInstanceHandle.getState(), createAppMTO(appInstanceHandle.getApplicationDescriptor())) : null;
		getContext().ungetService(appInstSR);
		return result;
	}

	protected List<ServiceReference<ApplicationDescriptor>> getAppSRs(String appId) {
		ServiceReference<ApplicationDescriptor>[] appSRs = appDescTracker.getServiceReferences();
		return (appSRs == null) ? Collections.emptyList() : select(Arrays.asList(appSRs),p -> {
			return appId == null || appId.equals(p.getProperty(Constants.SERVICE_PID));
		});
	}

	protected List<ServiceReference<ApplicationHandle>> getAppInstSRs(String appInstId) {
		ServiceReference<ApplicationHandle>[] appSRs = appInstTracker.getServiceReferences();
		return (appSRs == null) ? Collections.emptyList() : select(Arrays.asList(appSRs),p -> {
			return appInstId == null || appInstId.equals(p.getProperty(Constants.SERVICE_PID));
		});
	}

	@Override
	public ApplicationMTO[] getApplications() {
		List<ApplicationMTO> results = getAppSRs(null).stream().map(sr -> {
			return createAppMTO(sr);
		}).collect(Collectors.toList());
		return results.toArray(new ApplicationMTO[results.size()]);
	}

	@Override
	public ApplicationMTO getApplication(String appId) {
		List<ServiceReference<ApplicationDescriptor>> results = getAppSRs(appId);
		return results.size() > 0 ? createAppMTO(results.get(0)) : null;
	}

	@Override
	public ApplicationInstanceMTO[] getRunningApplications() {
		List<ApplicationInstanceMTO> results = getAppInstSRs(null).stream().map(sr -> {
			return createAppInstanceMTO(sr);
		}).collect(Collectors.toList());
		return results.toArray(new ApplicationInstanceMTO[results.size()]);
	}

	@Override
	public ApplicationInstanceMTO getRunningApplication(String appInstanceId) {
		List<ServiceReference<ApplicationHandle>> results = getAppInstSRs(appInstanceId);
		return results.size() > 0 ? createAppInstanceMTO(results.get(0)) : null;
	}

	@Override
	public IStatus startApplication(String appId, @SuppressWarnings("rawtypes") Map appArgs) {
		List<ServiceReference<ApplicationDescriptor>> results = getAppSRs(appId);
		if (results.size() == 0)
			return createErrorStatus("Could not find appId=" + appId + " to start");
		ServiceReference<ApplicationDescriptor> sr = results.get(0);
		ApplicationDescriptor ad = getContext().getService(sr);
		if (ad == null)
			return createErrorStatus("Could not get application descriptor for appId=" + appId);
		try {
			ad.launch(appArgs);
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not launch appId=" + appId);
		} finally {
			getContext().ungetService(sr);
		}
	}

	@Override
	public IStatus stopApplication(String appInstanceId) {
		List<ServiceReference<ApplicationHandle>> results = getAppInstSRs(appInstanceId);
		if (results.size() == 0)
			return createErrorStatus("Could not find appInstanceId=" + appInstanceId + " to stop");
		ServiceReference<ApplicationHandle> sr = results.get(0);
		ApplicationHandle ah = getContext().getService(sr);
		if (ah == null)
			return createErrorStatus("Could not get application handle for appInstanceId=" + appInstanceId);
		try {
			ah.destroy();
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not launch appInstanceId=" + appInstanceId);
		} finally {
			getContext().ungetService(sr);
		}
	}

	@Override
	public IStatus lockApplication(String appId) {
		List<ServiceReference<ApplicationDescriptor>> results = getAppSRs(appId);
		if (results.size() == 0)
			return createErrorStatus("Could not find appId=" + appId + " to start");
		ServiceReference<ApplicationDescriptor> sr = results.get(0);
		ApplicationDescriptor ad = getContext().getService(sr);
		if (ad == null)
			return createErrorStatus("Could not get application descriptor for appId=" + appId);
		try {
			ad.lock();
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not launch appId=" + appId);
		} finally {
			getContext().ungetService(sr);
		}
	}

	@Override
	public IStatus unlockApplication(String appId) {
		List<ServiceReference<ApplicationDescriptor>> results = getAppSRs(appId);
		if (results.size() == 0)
			return createErrorStatus("Could not find appId=" + appId + " to start");
		ServiceReference<ApplicationDescriptor> sr = results.get(0);
		ApplicationDescriptor ad = getContext().getService(sr);
		if (ad == null)
			return createErrorStatus("Could not get application descriptor for appId=" + appId);
		try {
			ad.unlock();
			return SerializableStatus.OK_STATUS;
		} catch (Exception e) {
			return createErrorStatus("Could not launch appId=" + appId);
		} finally {
			getContext().ungetService(sr);
		}
	}

}
