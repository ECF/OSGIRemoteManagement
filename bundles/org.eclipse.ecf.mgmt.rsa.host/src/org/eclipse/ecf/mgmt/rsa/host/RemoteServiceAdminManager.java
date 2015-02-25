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
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class RemoteServiceAdminManager extends AbstractManager implements IRemoteServiceAdminManager,
		RemoteServiceAdminListener {

	private RemoteServiceAdmin remoteServiceAdmin;
	private List<RemoteServiceAdminEvent> adminEvents;

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
		if (!(event instanceof RemoteServiceAdminEvent))
			return;
		adminEvents.add((RemoteServiceAdminEvent) event);
	}

	public void activate(BundleContext context) throws Exception {
		super.activate(context);
		adminEvents = Collections.synchronizedList(new ArrayList<RemoteServiceAdminEvent>());
	}

	public void deactivate() throws Exception {
		adminEvents.clear();
		super.deactivate();
	}

	
	@Override
	public RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents(int[] typeFilters) {
		List<RemoteServiceAdminEvent> events = new ArrayList<RemoteServiceAdminEvent>();
		synchronized (adminEvents) {
			events.addAll(adminEvents);
		}
		events.removeIf(e -> {
			if (typeFilters == null) return false;
			else {
				boolean typeFound = false;
				for(int f: typeFilters) 
					if (e.getType() == f) typeFound = true;
				return typeFound;
			}
		});
		List<RemoteServiceAdminEventMTO> results = new ArrayList<RemoteServiceAdminEventMTO>();
			for(RemoteServiceAdminEvent e: events) 
				results.add(new RemoteServiceAdminEventMTO((RemoteServiceAdmin.RemoteServiceAdminEvent) e));
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
		List<ExportReferenceMTO> results = new ArrayList<ExportReferenceMTO>();
		List<RemoteServiceAdminEvent> events = getEvents(true);
		for(RemoteServiceAdminEvent e: events) 
			results.add(new ExportReferenceMTO((EndpointDescription) e.getExportReference().getExportedEndpoint()));
		return results.toArray(new ExportReferenceMTO[results.size()]);
	}

	@Override
	public ImportReferenceMTO[] getImportedEndpoints() {
		List<ImportReferenceMTO> results = new ArrayList<ImportReferenceMTO>();
		List<RemoteServiceAdminEvent> events = getEvents(false);
		for(RemoteServiceAdminEvent e: events) 
			results.add(new ImportReferenceMTO((EndpointDescription) e.getImportReference().getImportedEndpoint()));
		return results.toArray(new ImportReferenceMTO[results.size()]);
	}

	@Override
	public ExportRegistrationMTO[] registerService(ServiceReferenceMTO serviceReference,
			Map<String, ?> overridingProperties) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(serviceReference.getId());
		if (sr == null) return null;
		
		Collection<ExportRegistration> ers = remoteServiceAdmin.exportService(sr, overridingProperties);
		
		List<ExportRegistrationMTO> results = new ArrayList<ExportRegistrationMTO>();
		for(ExportRegistration er: ers) {
			Throwable t = er.getException();
			results.add((t == null)?new ExportRegistrationMTO(t):new ExportRegistrationMTO((org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) er.getExportReference().getExportedEndpoint()));
		}
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
		if (srs !=  null)
		for(ServiceReference sr: srs) {
			Long svcId = (Long) sr.getProperty(Constants.SERVICE_ID);
			if (serviceId == svcId.longValue()) return sr;
		}
		return null;
	}

	@Override
	public EndpointDescriptionMTO update(ExportReferenceMTO exportReference, Map<String, ?> properties) {
		@SuppressWarnings("rawtypes")
		ServiceReference sr = findServiceReference(exportReference.getExportedService());
		if (sr == null) return null;
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close(ExportReferenceMTO exportReference) {
		// TODO Auto-generated method stub
	}

	@Override
	public ImportRegistrationMTO importService(EndpointDescriptionMTO endpointDescription) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateImport(EndpointDescriptionMTO endpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(ImportReferenceMTO importReference) {
		// TODO Auto-generated method stub

	}

	@Override
	public RemoteServiceAdminEventMTO[] getRemoteServiceAdminEvents() {
		return getRemoteServiceAdminEvents(null);
	}

}
