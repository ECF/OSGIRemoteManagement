package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.IRemoteServiceNotifier;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallEventHandler;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManagerAsync;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ICallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.IImportableServiceCallbackAssociator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate = true)
public class RemoteKarafFeaturesInstaller {

	private static RemoteKarafFeaturesInstaller instance;

	public static RemoteKarafFeaturesInstaller getInstance() {
		return instance;
	}

	public RemoteKarafFeaturesInstaller() {
		instance = this;
	}
	
	private IImportableServiceCallbackAssociator importer;

	@Reference
	void bindCallbackAssociator(IImportableServiceCallbackAssociator ca) {
		this.importer = ca;
	}
	void unbindCallbackAssociator(IImportableServiceCallbackAssociator ca) {
		this.importer = null;
	}
	
	private IRemoteServiceNotifier notifier;
	
	@Reference
	void bindNotifier(IRemoteServiceNotifier n) {
		this.notifier = n;
	}
	
	void unbindNotifier(IRemoteServiceNotifier n) {
		this.notifier = null;
	}
	
	public IRemoteServiceNotifier getNotifier() {
		return this.notifier;
	}
	

	@Activate
	public void activate(BundleContext context) throws Exception {
		this.importer.associateCallbackRegistrar(FeatureInstallManagerAsync.class, new ICallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(FeatureInstallEventHandler.class, new KarafFeaturesListener(importReference), null);
			}});
	}

	@Deactivate
	public void deactivate() {
		this.importer.unassociateCallbackRegistrar(FeatureInstallManagerAsync.class);
		this.importer = null;
		this.notifier = null;
		instance = null;
	}

	
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	void bindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		this.notifier.addServiceHolder(FeatureInstallManagerAsync.class, fi);
	}

	void unbindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		this.notifier.removeServiceHolder(FeatureInstallManagerAsync.class, fi);
	}

	public IContainer getContainerForID(ID containerID) {
		return importer == null?null:importer.getContainerConnectedToID(containerID);
	}

}
