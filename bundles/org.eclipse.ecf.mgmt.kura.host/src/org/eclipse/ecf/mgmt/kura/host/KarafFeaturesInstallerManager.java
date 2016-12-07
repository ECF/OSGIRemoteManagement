package org.eclipse.ecf.mgmt.kura.host;

import org.apache.karaf.features.FeaturesService;
import org.eclipse.ecf.mgmt.karaf.features.KarafFeaturesInstaller;
import org.eclipse.ecf.mgmt.karaf.features.host.KarafFeaturesInstallerHost;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate=true)
public class KarafFeaturesInstallerManager extends KarafFeaturesInstallerHost implements KarafFeaturesInstaller {

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
}
