package org.eclipse.ecf.mgmt.rsa.eclipse.ui.model;

import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManagerAsync;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.AbstractRSANode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ExportedServicesRootNode;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.model.ImportedEndpointsRootNode;

public class RSAManagerNode extends AbstractRSANode {

	private final IRemoteServiceReference managerRef;
	private final IRemoteServiceAdminManagerAsync rsaManager;
	private final ExportedServicesRootNode exportedServicesRoot;
	private final ImportedEndpointsRootNode importedEndpointsRoot;

	public RSAManagerNode(IRemoteServiceReference managerRef, IRemoteServiceAdminManagerAsync rsaManager) {
		this.managerRef = managerRef;
		this.rsaManager = rsaManager;
		exportedServicesRoot = new ExportedServicesRootNode("Exported Services");
		addChild(exportedServicesRoot);
		importedEndpointsRoot = new ImportedEndpointsRootNode("Imported Endpoints");
		addChild(importedEndpointsRoot);
	}

	public IRemoteServiceAdminManagerAsync getRemoteServiceAdminManager() {
		return this.rsaManager;
	}

	public IRemoteServiceReference getRemoteServiceAdminManagerRef() {
		return this.managerRef;
	}

	public ExportedServicesRootNode getExportedServicesRootNode() {
		return exportedServicesRoot;
	}

	public ImportedEndpointsRootNode getImportedEndpointsRootNode() {
		return importedEndpointsRoot;
	}

	public String getManagerContainer() {
		return this.managerRef.getID().getContainerID().getName();
	}

}
