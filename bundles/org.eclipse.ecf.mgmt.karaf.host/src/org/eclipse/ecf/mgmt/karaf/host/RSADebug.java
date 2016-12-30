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

import org.eclipse.ecf.osgi.services.remoteserviceadmin.DebugRemoteServiceAdminListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

@Component(immediate=true)
public class RSADebug extends DebugRemoteServiceAdminListener implements RemoteServiceAdminListener {

}
