/*******************************************************************************
 * Copyright (c) 2017 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.metatype;

import java.util.concurrent.CompletableFuture;

public interface IMetaTypeManagerAsync {

	CompletableFuture<MetaTypeInformationMTO> getMetaTypeInformationAsync(long bundleId);
	
	CompletableFuture<ObjectClassDefinitionMTO> getObjectClassDefinitionAsync(long bundleId, String ocdId, int filter);
	
	CompletableFuture<byte[]> getIconBytesAsync(long bundleId, String ocdId, int size);
}
