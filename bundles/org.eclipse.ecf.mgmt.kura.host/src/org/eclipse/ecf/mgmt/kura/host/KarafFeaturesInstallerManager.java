package org.eclipse.ecf.mgmt.kura.host;

import org.apache.karaf.features.FeaturesService;
import org.eclipse.ecf.mgmt.karaf.features.FeaturesInstaller;
import org.eclipse.ecf.mgmt.karaf.features.FeaturesListenerAsync;
import org.eclipse.ecf.mgmt.karaf.features.host.FeaturesInstallerHost;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate=true)
public class KarafFeaturesInstallerManager extends FeaturesInstallerHost implements FeaturesInstaller {

	@Activate
	protected void activate(BundleContext ctxt) throws Exception {
		super.activate(ctxt);
	}	
	
	@Deactivate
	protected void deactivate() throws Exception {
		super.deactivate();
	}
	
	@Reference
	protected void bindFeaturesService(FeaturesService featuresService) {
		super.bindFeaturesService(featuresService);
	}
	
	@Override
	protected void unbindFeaturesService(FeaturesService featuresService) {
		super.unbindFeaturesService(featuresService);
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE,policy=ReferencePolicy.DYNAMIC)
	void bindFeaturesListener(FeaturesListenerAsync fl) {
		super.addFeaturesListener(fl);
	}

	void unbindFeaturesListener(FeaturesListenerAsync fl) {
		super.removeFeaturesListener(fl);
	}

}
