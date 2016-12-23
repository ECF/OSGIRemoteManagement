/*******************************************************************************
 * Copyright Async(c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.karaf.features;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface FeaturesInstallerAsync {

    CompletableFuture<Void> validateRepositoryAsync(URI uri);

    CompletableFuture<Void> addRepositoryAsync(URI uri);

    CompletableFuture<Void> addRepositoryAsync(URI uri, boolean install);

    CompletableFuture<Void> removeRepositoryAsync(URI uri);

    CompletableFuture<Void> removeRepositoryAsync(URI uri, boolean uninstall);

    CompletableFuture<Void> restoreRepositoryAsync(URI uri);

    CompletableFuture<RepositoryMTO[]> listRequiredRepositoriesAsync();

    CompletableFuture<RepositoryMTO[]> listRepositoriesAsync();

    CompletableFuture<RepositoryMTO> getRepositoryAsync(String repoName);

    CompletableFuture<RepositoryMTO> getRepositoryAsync(URI uri);

    CompletableFuture<String> getRepositoryNameAsync(URI uri);

    CompletableFuture<Void> setResolutionOutputFileAsync(String outputFile);

    CompletableFuture<Void> installFeatureAsync(String name);

    CompletableFuture<Void> installFeatureAsync(String name, Set<Integer> options);

    CompletableFuture<Void> installFeatureAsync(String name, String version);

    CompletableFuture<Void> installFeatureAsync(String name, String version, Set<Integer> options);

    CompletableFuture<Void> installFeaturesAsync(Set<String> features, Set<Integer> options);

    CompletableFuture<Void> installFeaturesAsync(Set<String> features, String region, Set<Integer> options);

    CompletableFuture<Void> addRequirementsAsync(Map<String, Set<String>> requirements, Set<Integer> options);

    CompletableFuture<Void> uninstallFeatureAsync(String name, Set<Integer> options);

    CompletableFuture<Void> uninstallFeatureAsync(String name);

    CompletableFuture<Void> uninstallFeatureAsync(String name, String version, Set<Integer> options);

    CompletableFuture<Void> uninstallFeatureAsync(String name, String version);

    CompletableFuture<Void> uninstallFeaturesAsync(Set<String> FeatureMTOs, Set<Integer> options);

    CompletableFuture<Void> uninstallFeaturesAsync(Set<String> features, String region, Set<Integer> options);

    CompletableFuture<Void> removeRequirementsAsync(Map<String, Set<String>> requirements, Set<Integer> options);

    CompletableFuture<Void> updateFeaturesStateAsync(Map<String, Map<String, Integer>> stateChanges, Set<Integer> options);

    CompletableFuture<FeatureMTO[]> listFeaturesAsync();

    CompletableFuture<FeatureMTO[]> listRequiredFeaturesAsync();

    CompletableFuture<FeatureMTO[]> listInstalledFeaturesAsync();

    CompletableFuture<Map<String, Set<String>>> listRequirementsAsync();

    CompletableFuture<Boolean> isRequiredAsync(FeatureMTO f);

    CompletableFuture<Boolean> isInstalledAsync(FeatureMTO f);

    CompletableFuture<FeatureMTO> getFeatureAsync(String name, String version);

    CompletableFuture<FeatureMTO> getFeatureAsync(String name);

    CompletableFuture<FeatureMTO[]> getFeaturesAsync(String name, String version);

    CompletableFuture<FeatureMTO[]> getFeaturesAsync(String name);

    CompletableFuture<Void> refreshRepositoryAsync(URI uri);

    CompletableFuture<URI> getRepositoryUriForAsync(String name, String version);

    CompletableFuture<String[]> getRepositoryNamesAsync();

    CompletableFuture<Integer> getStateAsync(String featureId);
}
