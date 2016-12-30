package org.eclipse.ecf.mgmt.karaf.features.eclipse.ui;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.mgmt.consumer.util.RemoteServiceComponent;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManagerAsync;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallEventHandler;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ICallbackRegistrar;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.callback.ServiceImporterCallbackExporter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate = true)
public class RemoteKarafFeaturesInstaller extends RemoteServiceComponent implements RemoteServiceAdminListener {

	private IContainerManager containerManager;
	private ServiceImporterCallbackExporter importer;
	private static RemoteKarafFeaturesInstaller instance;

	public static RemoteKarafFeaturesInstaller getInstance() {
		return instance;
	}

	public RemoteKarafFeaturesInstaller() {
		super();
		this.importer = new ServiceImporterCallbackExporter();
		instance = this;
	}
	
	@Activate
	public void activate(BundleContext context) throws Exception {
		super.activate();
		this.importer.activate(context);
		this.importer.addImportedServiceCallback(FeatureInstallManagerAsync.class, new ICallbackRegistrar() {
			@Override
			public ServiceRegistration<?> registerCallback(ImportReference importReference) throws Exception {
				return context.registerService(FeatureInstallEventHandler.class, new KarafFeaturesListener(importReference), null);
			}});
	}

	@Deactivate
	public void deactivate() {
		this.importer.removeImportedServiceCallback(FeatureInstallManagerAsync.class);
		this.importer.deactivate();
		super.deactivate();
		instance = null;
	}

	
	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	void bindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		addServiceHolder(FeatureInstallManagerAsync.class, fi);
	}

	void unbindKarafFeaturesInstaller(FeatureInstallManagerAsync fi) {
		removeServiceHolder(FeatureInstallManagerAsync.class, fi);
	}

	@Reference
	void bindContainerManager(IContainerManager c) {
		this.importer.bindContainerManager(c);
		this.containerManager = c;
	}

	void unbindContainerManager(IContainerManager c) {
		this.importer.unbindContainerManager(c);
		this.containerManager = null;
	}

	@Reference
	void bindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.bindRemoteServiceAdmin(rsa);
	}

	void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
		this.importer.unbindRemoteServiceAdmin(rsa);
	}

	public IContainer getContainerForID(ID containerID) {
		IContainerManager cm = this.containerManager;
		if (cm != null)
			for (IContainer c : this.containerManager.getAllContainers()) {
				ID targetID = c.getConnectedID();
				if (targetID != null && targetID.equals(containerID))
					return c;
			}
		return null;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		this.importer.remoteAdminEvent(event);
	}
}
