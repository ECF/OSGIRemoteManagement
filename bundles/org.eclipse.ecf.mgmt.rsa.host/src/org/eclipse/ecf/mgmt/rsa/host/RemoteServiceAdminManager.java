/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.rsa.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.ecf.mgmt.framework.ServiceReferenceMTO;
import org.eclipse.ecf.mgmt.framework.host.AbstractManager;
import org.eclipse.ecf.mgmt.rsa.EndpointDescriptionMTO;
import org.eclipse.ecf.mgmt.rsa.ExportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ExportRegistrationMTO;
import org.eclipse.ecf.mgmt.rsa.IRemoteServiceAdminManager;
import org.eclipse.ecf.mgmt.rsa.ImportReferenceMTO;
import org.eclipse.ecf.mgmt.rsa.ImportRegistrationMTO;
import org.eclipse.ecf.mgmt.rsa.RemoteServiceAdminEventMTO;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class RemoteServiceAdminManager extends AbstractManager
		implements IRemoteServiceAdminManager, RemoteServiceAdminListener {

	private RemoteServiceAdmin remoteServiceAdmin;
	private List<RemoteServiceAdmin.RemoteServiceAdminEvent> adminEvents;

	protected void bindRemoteServiceAdmin(org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa) {
		this.remoteServiceAdmin = (RemoteServiceAdmin) rsa;
	}

	protected void unbindRemoteServiceAdmin(org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa) {
		this.remoteServiceAdmin = null;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		if (!(event instanceof RemoteServiceAdmin.RemoteServiceAdminEvent))
			return;
		adminEvents.add((RemoteServiceAdmin.RemoteServiceAdminEvent) event);
	}

	protected void activate(BundleContext context) throws Exception {
		super.activate(context);
		adminEvents = Collections.synchronizedList(new ArrayList<RemoteServiceAdmin.RemoteServiceAdminEvent>());
	}

	protected void deactivate() throws Exception {
		adminEvents.clear();
		super.deactivate();
	}

	protected RemoteServiceAdminEventMTO createEventMTO(RemoteServiceAdmin.RemoteServiceAdminEvent e) {
		EndpointDescription ed = e.getEndpointDescription();
		return new RemoteServiceAdminEventMTO(e.getType(), e.getSource().getBundleId(), e.getContainerID(),
				(ed == null) ? 0L : ed.getRemoteServiceId(), (ed == null) ? 0L : ed.getServiceId(),
				(ed == null) ? null : ed.getProperties(), e.getException());
	}

	protected ExportReferenceMTO createExportReferenceMTO(EndpointDescription ed) {
		return new ExportReferenceMTO(ed.getContainerID(), ed.getRemoteServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	protected ImportReferenceMTO createImportReferenceMTO(EndpointDescription ed) {
		return new ImportReferenceMTO(ed.getContainerID(), ed.getRemoteServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	protected ExportRegistrationMTO createExportRegistrationMTO(ExportRegistration er) {
		Throwable t = er.getException();
		if (t != null)
			return new ExportRegistrationMTO(t);
		EndpointDescription ed = (EndpointDescription) er.getExportReference().getExportedEndpoint();
		return new ExportRegistrationMTO(ed.getConnectTargetID(), ed.getServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	protected ImportRegistrationMTO createImportRegistrationMTO(ImportRegistration ir) {
		Throwable t = ir.getException();
		if (t != null)
			return new ImportRegistrationMTO(t);
		EndpointDescription ed = (EndpointDescription) ir.getImportReference().getImportedEndpoint();
		return new ImportRegistrationMTO(ed.getConnectTargetID(), ed.getServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	protected List<RemoteServiceAdminEvent> getEvents(boolean export) {
		return adminEvents.stream().filter(e -> {
			return (e.getType() == RemoteServiceAdminEvent.EXPORT_REGISTRATION && e.getException() == null
					&& ((export && e.getExportReference() != null) || (!export && e.getImportReference() != null)));
		}).collect(Collectors.toList());
	}

	@Override
	public RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents(int[] typeFilters) {
		List<RemoteServiceAdmin.RemoteServiceAdminEvent> events = new ArrayList<RemoteServiceAdmin.RemoteServiceAdminEvent>();
		synchronized (adminEvents) {
			events.addAll(adminEvents);
		}
		List<RemoteServiceAdminEventMTO> results = events.stream().filter(e -> {
			if (typeFilters == null)
				return false;
			else {
				boolean typeFound = false;
				for (int f : typeFilters)
					if (e.getType() == f)
						typeFound = true;
				return typeFound;
			}
		}).map(e -> {
			return createEventMTO(e);
		}).collect(Collectors.toList());
		return results.toArray(new RemoteServiceAdminEventMTO[results.size()]);
	}

	@Override
	public ExportRegistrationMTO[] getExportedServices() {
		List<ExportRegistrationMTO> results = this.remoteServiceAdmin.getExportedRegistrations().stream().map(ereg -> {
			return createExportRegistrationMTO(ereg);
		}).collect(Collectors.toList());
		return results.toArray(new ExportRegistrationMTO[results.size()]);
	}

	@Override
	public ImportRegistrationMTO[] getImportedEndpoints() {
		List<ImportRegistrationMTO> results = this.remoteServiceAdmin.getImportedRegistrations().stream().map(ireg -> {
			return createImportRegistrationMTO(ireg);
		}).collect(Collectors.toList());
		return results.toArray(new ImportRegistrationMTO[results.size()]);
	}

	@Override
	public ExportRegistrationMTO[] exportService(ServiceReferenceMTO serviceReference,
			Map<String, ?> overridingProperties) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(serviceReference.getId());
		if (sr == null)
			return null;

		Collection<ExportRegistration> ers = remoteServiceAdmin.exportService(sr, overridingProperties);

		List<ExportRegistrationMTO> results = ers.stream().map(er -> {
			return createExportRegistrationMTO(er);
		}).collect(Collectors.toList());

		return results.toArray(new ExportRegistrationMTO[results.size()]);
	}

	@SuppressWarnings("rawtypes")
	private ServiceReference findServiceReference(long serviceId) {
		ServiceReference[] srs = null;
		try {
			srs = getContext().getAllServiceReferences(null, null);
		} catch (InvalidSyntaxException e) {
			// should not happen
		}
		if (srs != null)
			for (ServiceReference sr : srs) {
				Long svcId = (Long) sr.getProperty(Constants.SERVICE_ID);
				if (serviceId == svcId.longValue())
					return sr;
			}
		return null;
	}

	protected org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportRegistration findExportRegistration(
			ServiceReference<?> sr) {
		List<org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ExportRegistration> results = select(
				remoteServiceAdmin.getExportedRegistrations(), er -> {
					ExportReference exRef = (ExportReference) er.getExportReference();
					if (exRef == null)
						return false;
					return sr.equals(exRef.getExportedService());
				});
		return results.size() == 0 ? null : results.get(0);
	}

	protected org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration findImportRegistration(
			EndpointDescription ed) {
		List<org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration> results = select(
				remoteServiceAdmin.getImportedRegistrations(), ir -> {
					ImportReference iRef = (ImportReference) ir.getImportReference();
					if (iRef == null)
						return false;
					return ed.equals(iRef.getImportedEndpoint());
				});
		return results.size() == 0 ? null : results.get(0);
	}

	protected org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration findImportRegistration(
			ServiceReference<?> sr) {
		List<org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration> results = select(
				remoteServiceAdmin.getImportedRegistrations(), ir -> {
					ImportReference iRef = (ImportReference) ir.getImportReference();
					if (iRef == null)
						return false;
					return sr.equals(iRef.getImportedService());
				});
		return results.size() == 0 ? null : results.get(0);
	}

	@Override
	public EndpointDescriptionMTO updateExport(ExportReferenceMTO exportReference, Map<String, ?> properties) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(exportReference.getExportedService());
		if (sr == null)
			return null;
		RemoteServiceAdmin.ExportRegistration exportRegistration = findExportRegistration(sr);
		if (exportRegistration == null)
			return null;
		EndpointDescription updated = (EndpointDescription) exportRegistration.update(properties);
		if (updated == null)
			return null;
		return new EndpointDescriptionMTO(updated.getProperties());
	}

	@Override
	public boolean closeExport(ExportReferenceMTO exportReference) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(exportReference.getExportedService());
		if (sr == null)
			return false;
		RemoteServiceAdmin.ExportRegistration exportRegistration = findExportRegistration(sr);
		if (exportRegistration == null)
			return false;
		exportRegistration.close();
		return true;
	}

	@Override
	public ImportRegistrationMTO importService(EndpointDescriptionMTO endpointDescription) {
		ImportRegistration ir = remoteServiceAdmin
				.importService(new EndpointDescription(endpointDescription.getProperties()));
		return (ir == null) ? null : createImportRegistrationMTO(ir);
	}

	@Override
	public boolean updateImport(EndpointDescriptionMTO endpoint) {
		EndpointDescription updateEd = new EndpointDescription(endpoint.getProperties());
		RemoteServiceAdmin.ImportRegistration importRegistration = findImportRegistration(updateEd);
		if (importRegistration == null)
			return false;
		return importRegistration.update(updateEd);
	}

	@Override
	public boolean closeImport(ImportReferenceMTO importReference) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(importReference.getImportedService());
		if (sr == null)
			return false;
		RemoteServiceAdmin.ImportRegistration importRegistration = findImportRegistration(sr);
		if (importRegistration == null)
			return false;
		importRegistration.close();
		return true;
	}

	@Override
	public RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents() {
		return getRemoteServiceAdminEvents(null);
	}

}
