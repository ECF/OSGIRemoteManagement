/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.host;

import org.apache.karaf.features.FeaturesService;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallManager;
import org.eclipse.ecf.mgmt.karaf.features.FeatureInstallEventHandlerAsync;
import org.eclipse.ecf.mgmt.karaf.features.host.FeatureInstallManagerHost;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(immediate=true)
public class KarafFeatureInstallManager extends FeatureInstallManagerHost implements FeatureInstallManager {

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
	void bindFeaturesListener(FeatureInstallEventHandlerAsync fl) {
		super.addFeaturesListener(fl);
	}

	void unbindFeaturesListener(FeatureInstallEventHandlerAsync fl) {
		super.removeFeaturesListener(fl);
	}

}
