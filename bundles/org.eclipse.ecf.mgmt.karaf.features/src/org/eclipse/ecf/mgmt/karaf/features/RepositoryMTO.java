/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;

public class RepositoryMTO implements Serializable {

	private static final long serialVersionUID = -1329062711860942778L;

	private String name;
	private URI uri;
	private URI[] repositories;
	private URI[] resourceRepositories;
	private FeatureMTO[] featureMTOs;
	
	public RepositoryMTO(String name, URI uri, URI[] repos, URI[] resourceRepos, FeatureMTO[] features) {
		this.name = name;
		this.uri = uri;
		this.repositories = repos;
		this.resourceRepositories = resourceRepos;
		this.featureMTOs = features;
	}
	
	String getName() {
		return name;
	}
	URI getURI() {
		return uri;
	}
	public URI getUri() {
		return uri;
	}
	public URI[] getRepositories() {
		return repositories;
	}
	public URI[] getResourceRepositories() {
		return resourceRepositories;
	}
	
	public FeatureMTO[] getFeatures() {
		return featureMTOs;
	}

	@Override
	public String toString() {
		return "RepositoryMTO [name=" + name + ", uri=" + uri + ", repositories=" + Arrays.toString(repositories)
				+ ", resourceRepositories=" + Arrays.toString(resourceRepositories) + ", featureMTOs="
				+ Arrays.toString(featureMTOs) + "]";
	}
	
}
