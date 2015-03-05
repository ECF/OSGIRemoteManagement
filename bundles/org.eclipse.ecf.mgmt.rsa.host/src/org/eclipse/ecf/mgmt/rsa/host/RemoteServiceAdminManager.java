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
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class RemoteServiceAdminManager extends AbstractManager implements IRemoteServiceAdminManager,
		RemoteServiceAdminListener {

	private RemoteServiceAdmin remoteServiceAdmin;
	private List<RemoteServiceAdmin.RemoteServiceAdminEvent> adminEvents;

	void bindRemoteServiceAdmin(org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa) {
		this.remoteServiceAdmin = (RemoteServiceAdmin) rsa;
	}

	void unbindRemoteServiceAdmin(org.osgi.service.remoteserviceadmin.RemoteServiceAdmin rsa) {
		this.remoteServiceAdmin = null;
	}

	@Override
	public void remoteAdminEvent(RemoteServiceAdminEvent event) {
		handleRemoteServiceAdminEvent(event);
	}

	private void handleRemoteServiceAdminEvent(RemoteServiceAdminEvent event) {
		if (!(event instanceof RemoteServiceAdmin.RemoteServiceAdminEvent))
			return;
		adminEvents.add((RemoteServiceAdmin.RemoteServiceAdminEvent) event);
	}

	public void activate(BundleContext context) throws Exception {
		super.activate(context);
		adminEvents = Collections.synchronizedList(new ArrayList<RemoteServiceAdmin.RemoteServiceAdminEvent>());
	}

	public void deactivate() throws Exception {
		adminEvents.clear();
		super.deactivate();
	}

	private RemoteServiceAdminEventMTO createEventMTO(RemoteServiceAdmin.RemoteServiceAdminEvent e) {
		EndpointDescription ed = e.getEndpointDescription();
		return new RemoteServiceAdminEventMTO(e.getType(), e.getSource().getBundleId(), e.getContainerID(),
				(ed == null) ? 0L : ed.getRemoteServiceId(), (ed == null) ? 0L : ed.getServiceId(), (ed == null) ? null
						: ed.getProperties(), e.getException());
	}

	private ExportReferenceMTO createExportReferenceMTO(EndpointDescription ed) {
		return new ExportReferenceMTO(ed.getContainerID(), ed.getRemoteServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	private ImportReferenceMTO createImportReferenceMTO(EndpointDescription ed) {
		return new ImportReferenceMTO(ed.getContainerID(), ed.getRemoteServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	private ExportRegistrationMTO createExportRegistrationMTO(ExportRegistration er) {
		Throwable t = er.getException();
		if (t != null)
			return new ExportRegistrationMTO(t);
		EndpointDescription ed = (EndpointDescription) er.getExportReference().getExportedEndpoint();
		return new ExportRegistrationMTO(ed.getConnectTargetID(), ed.getServiceId(), ed.getServiceId(),
				ed.getProperties());
	}

	private ImportRegistrationMTO createImportRegistrationMTO(ImportRegistration ir) {
		Throwable t = ir.getException();
		if (t != null)
			return new ImportRegistrationMTO(t);
		EndpointDescription ed = (EndpointDescription) ir.getImportReference().getImportedEndpoint();
		return new ImportRegistrationMTO(ed.getConnectTargetID(), ed.getServiceId(), ed.getServiceId(),
				ed.getProperties());
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

	List<RemoteServiceAdminEvent> getEvents(boolean export) {
		return adminEvents
				.stream()
				.filter(e -> {
					return (e.getType() == RemoteServiceAdminEvent.EXPORT_REGISTRATION && e.getException() == null && ((export && e
							.getExportReference() != null) || (!export && e.getImportReference() != null)));
				}).collect(Collectors.toList());
	}

	@Override
	public ExportReferenceMTO[] getExportedServices() {
		List<ExportReferenceMTO> results = getEvents(true).stream().map(e -> {
			return createExportReferenceMTO((EndpointDescription) e.getExportReference().getExportedEndpoint());
		}).collect(Collectors.toList());
		return results.toArray(new ExportReferenceMTO[results.size()]);
	}

	@Override
	public ImportReferenceMTO[] getImportedEndpoints() {
		List<ImportReferenceMTO> results = getEvents(false).stream().map(e -> {
			return createImportReferenceMTO((EndpointDescription) e.getExportReference().getExportedEndpoint());
		}).collect(Collectors.toList());
		return results.toArray(new ImportReferenceMTO[results.size()]);
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

	@Override
	public EndpointDescriptionMTO updateExport(ExportReferenceMTO exportReference, Map<String, ?> properties) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(exportReference.getExportedService());
		if (sr == null)
			return null;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean closeExport(ExportReferenceMTO exportReference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImportRegistrationMTO importService(EndpointDescriptionMTO endpointDescription) {
		ImportRegistration ir = remoteServiceAdmin.importService(new EndpointDescription(endpointDescription
				.getProperties()));
		return (ir == null) ? null : createImportRegistrationMTO(ir);
	}

	@Override
	public boolean updateImport(EndpointDescriptionMTO endpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closeImport(ImportReferenceMTO importReference) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents() {
		return getRemoteServiceAdminEvents(null);
	}

}
