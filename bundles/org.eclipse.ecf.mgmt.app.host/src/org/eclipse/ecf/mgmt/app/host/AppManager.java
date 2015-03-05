package org.eclipse.ecf.mgmt.app.host;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.status.SerializableStatus;
import org.eclipse.ecf.mgmt.app.AppInstanceMTO;
import org.eclipse.ecf.mgmt.app.AppMTO;
import org.eclipse.ecf.mgmt.app.IAppManager;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.util.tracker.ServiceTracker;

public class AppManager extends AbstractManager implements IAppManager {

	private ServiceTracker<ApplicationDescriptor, ApplicationDescriptor> appDescTracker;
	private ServiceTracker<ApplicationHandle, ApplicationHandle> appInstTracker;

	@Override
	public void activate(BundleContext context) throws Exception {
		super.activate(context);
		appDescTracker = new ServiceTracker<ApplicationDescriptor, ApplicationDescriptor>(context,
				ApplicationDescriptor.class, null);
		appDescTracker.open();
		appInstTracker = new ServiceTracker<ApplicationHandle, ApplicationHandle>(context, ApplicationHandle.class,
				null);
		appInstTracker.open();
	}

	@Override
	public void deactivate() throws Exception {
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
	private AppMTO createAppMTO(ApplicationDescriptor appDescriptor) {
		return new AppMTO(appDescriptor.getApplicationId(), (Map<String, ?>) appDescriptor.getProperties(null));
	}

	private AppMTO createAppMTO(ServiceReference<ApplicationDescriptor> appDescSR) {
		ApplicationDescriptor appDescriptor = getContext().getService(appDescSR);
		AppMTO result = (appDescriptor != null) ? createAppMTO(appDescriptor) : null;
		getContext().ungetService(appDescSR);
		return result;
	}

	private AppInstanceMTO createAppInstanceMTO(ServiceReference<ApplicationHandle> appInstSR) {
		ApplicationHandle appInstanceHandle = getContext().getService(appInstSR);
		AppInstanceMTO result = (appInstanceHandle != null) ? new AppInstanceMTO(appInstanceHandle.getInstanceId(),
				appInstanceHandle.getState(), createAppMTO(appInstanceHandle.getApplicationDescriptor())) : null;
		getContext().ungetService(appInstSR);
		return result;
	}

	List<ServiceReference<ApplicationDescriptor>> getAppSRs(String appId) {
		ServiceReference<ApplicationDescriptor>[] appSRs = appDescTracker.getServiceReferences();
		return (appSRs == null) ? Collections.emptyList() : Arrays.asList(appSRs).stream().filter(p -> {
			return appId == null || appId.equals(p.getProperty(Constants.SERVICE_PID));
		}).collect(Collectors.toList());
	}

	List<ServiceReference<ApplicationHandle>> getAppInstSRs(String appInstId) {
		ServiceReference<ApplicationHandle>[] appSRs = appInstTracker.getServiceReferences();
		return (appSRs == null) ? Collections.emptyList() : Arrays.asList(appSRs).stream().filter(p -> {
			return appInstId == null || appInstId.equals(p.getProperty(Constants.SERVICE_PID));
		}).collect(Collectors.toList());
	}

	@Override
	public AppMTO[] getApps() {
		List<AppMTO> results = getAppSRs(null).stream().map(sr -> {
			return createAppMTO(sr);
		}).collect(Collectors.toList());
		return results.toArray(new AppMTO[results.size()]);
	}

	@Override
	public AppMTO getApp(String appId) {
		List<ServiceReference<ApplicationDescriptor>> results = getAppSRs(appId);
		return results.size() > 0 ? createAppMTO(results.get(0)) : null;
	}

	@Override
	public AppInstanceMTO[] getRunningApps() {
		List<AppInstanceMTO> results = getAppInstSRs(null).stream().map(sr -> {
			return createAppInstanceMTO(sr);
		}).collect(Collectors.toList());
		return results.toArray(new AppInstanceMTO[results.size()]);
	}

	@Override
	public AppInstanceMTO getRunningApp(String appInstanceId) {
		List<ServiceReference<ApplicationHandle>> results = getAppInstSRs(appInstanceId);
		return results.size() > 0 ? createAppInstanceMTO(results.get(0)) : null;
	}

	@Override
	public IStatus start(String appId, @SuppressWarnings("rawtypes") Map appArgs) {
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
	public IStatus stop(String appInstanceId) {
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
	public IStatus lock(String appId) {
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
	public IStatus unlock(String appId) {
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
