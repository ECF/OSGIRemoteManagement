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

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface FeaturesInstaller {

    public static final int Option_NoFailOnFeatureNotFound = 0x0;
    public static final int Option_NoAutoRefreshManagedBundles = 0x1;
    public static final int Option_NoAutoRefreshUnmanagedBundles = 0x2;
    public static final int Option_NoAutoRefreshBundles = 0x3;
    public static final int Option_NoAutoStartBundles = 0x4;
    public static final int Option_NoAutoManageBundles = 0x5;
    public static final int Option_Verbose = 0x6;
    public static final int Option_Upgrade = 0x7;

    public static final int FeatureState_Uninstalled = 0x10;
    public static final int FeatureState_Installed = 0x11;
    public static final int FeatureState_Resolved = 0x12;
    public static final int FeatureState_Started = 0x13;

    void validateRepository(URI uri) throws Exception;

    void addRepository(URI uri) throws Exception;

    void addRepository(URI uri, boolean install) throws Exception;

    void removeRepository(URI uri) throws Exception;

    void removeRepository(URI uri, boolean uninstall) throws Exception;

    void restoreRepository(URI uri) throws Exception;

    RepositoryMTO[] listRequiredRepositories() throws Exception;

    RepositoryMTO[] listRepositories() throws Exception;

    RepositoryMTO getRepository(String repoName) throws Exception;

    RepositoryMTO getRepository(URI uri) throws Exception;

    String getRepositoryName(URI uri) throws Exception;

    void setResolutionOutputFile(String outputFile);

    void installFeature(String name) throws Exception;

    void installFeature(String name, Set<Integer> options) throws Exception;

    void installFeature(String name, String version) throws Exception;

    void installFeature(String name, String version, Set<Integer> options) throws Exception;

    void installFeatures(Set<String> features, Set<Integer> options) throws Exception;

    void installFeatures(Set<String> features, String region, Set<Integer> options) throws Exception;

    void addRequirements(Map<String, Set<String>> requirements, Set<Integer> options) throws Exception;

    void uninstallFeature(String name, Set<Integer> options) throws Exception;

    void uninstallFeature(String name) throws Exception;

    void uninstallFeature(String name, String version, Set<Integer> options) throws Exception;

    void uninstallFeature(String name, String version) throws Exception;

    void uninstallFeatures(Set<String> FeatureMTOs, Set<Integer> options) throws Exception;

    void uninstallFeatures(Set<String> features, String region, Set<Integer> options) throws Exception;

    void removeRequirements(Map<String, Set<String>> requirements, Set<Integer> options) throws Exception;

    void updateFeaturesState(Map<String, Map<String, Integer>> stateChanges, Set<Integer> options) throws Exception;

    FeatureMTO[] listFeatures() throws Exception;

    FeatureMTO[] listRequiredFeatures() throws Exception;

    FeatureMTO[] listInstalledFeatures() throws Exception;

    Map<String, Set<String>> listRequirements();

    boolean isRequired(FeatureMTO f);

    boolean isInstalled(FeatureMTO f);

    FeatureMTO getFeature(String name, String version) throws Exception;

    FeatureMTO getFeature(String name) throws Exception;

    FeatureMTO[] getFeatures(String name, String version) throws Exception;

    FeatureMTO[] getFeatures(String name) throws Exception;

    void refreshRepository(URI uri) throws Exception;

    URI getRepositoryUriFor(String name, String version);

    String[] getRepositoryNames();

    int getState(String featureId);
}
