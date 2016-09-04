/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.bundleview;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundleNode;
import org.eclipse.ecf.remoteservice.ui.bundleview.model.BundlesRootNode;
import org.eclipse.ecf.remoteservice.ui.internal.bundleview.Activator;
import org.eclipse.jface.viewers.TreeViewer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.framework.dto.FrameworkDTO;

/**
 * @since 3.3
 */
public class BundlesView extends AbstractBundlesView {

	public static final String ID_VIEW = "org.eclipse.ecf.remoteservice.ui.bundleview.BundleView"; //$NON-NLS-1$

	public BundlesView() {
	}

	private BundleContext getLocalBundleContext() {
		return Activator.getDefault().getBundle().getBundleContext();
	}

	@Override
	public void dispose() {
		BundleContext ctxt = getLocalBundleContext();
		if (ctxt != null)
			ctxt.removeBundleListener(bundleListener);
		super.dispose();
	}

	private List<BundleDTO> getBundleDTOs(BundleContext ctxt) {
		return ctxt.getBundle(0).adapt(FrameworkDTO.class).bundles;
	}

	private BundleDTO getBundleDTO(BundleContext ctxt, Bundle b) {
		long bId = b.getBundleId();
		for (BundleDTO bdto : getBundleDTOs(ctxt))
			if (bId == bdto.id)
				return bdto;
		return null;
	}

	private BundleListener bundleListener = new BundleListener() {

		@Override
		public void bundleChanged(BundleEvent event) {
			final TreeViewer v = getTreeViewer();
			if (v == null)
				return;
			v.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					TreeViewer tv = getTreeViewer();
					if (tv == null)
						return;
					BundleContext ctxt = getLocalBundleContext();
					if (ctxt == null)
						return;
					Bundle bundle = event.getBundle();
					BundleDTO bDTO = getBundleDTO(ctxt, bundle);
					switch (event.getType()) {
					// add
					case BundleEvent.INSTALLED:
						if (bDTO != null)
							getBundlesRoot().addChild(createBundleNode(bDTO, bundle));
						break;
					case BundleEvent.UNINSTALLED:
						BundleNode bn = findBundleNode(bundle.getBundleId());
						if (bn != null)
							getBundlesRoot().removeChild(bn);
						break;
					default:
						BundleNode node = findBundleNode(bundle.getBundleId());
						if (node != null) {
							BundlesRootNode brn = getBundlesRoot();
							brn.removeChild(node);
							if (bDTO != null)
								brn.addChild(createBundleNode(bDTO, bundle));
						}
						break;
					}
					tv.setExpandedState(getBundlesRoot(), true);
					tv.refresh();
				}
			});
		}

	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<String, String> convertHeadersToMap(Dictionary<String, String> headers) {
		Map result = new HashMap();
		for (Enumeration e = headers.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = headers.get(key);
			result.put(key, value);
		}
		return result;
	}

	private Bundle getBundle(long bId) {
		BundleContext localContext = getLocalBundleContext();
		if (localContext != null)
			for (Bundle b : localContext.getBundles())
				if (b.getBundleId() == bId)
					return b;
		return null;
	}

	private BundleNode createBundleNode(BundleDTO bDTO, Bundle b) {
		return createBundleNode(bDTO.id, bDTO.lastModified, bDTO.state, bDTO.symbolicName, bDTO.version,
				convertHeadersToMap(b.getHeaders()), b.getLocation());
	}

	protected void initializeBundles() {
		final BundleContext ctxt = getLocalBundleContext();
		ctxt.addBundleListener(bundleListener);

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<BundleNode> bnds = new ArrayList<BundleNode>();
				for (BundleDTO bDTO : getBundleDTOs(ctxt))
					bnds.add(createBundleNode(bDTO, getBundle(bDTO.id)));
				addBundleNodes(bnds);
			}
		}).start();
	}

	private void startOrStopBundles(BundleNode[] bns, boolean start) {
		for (BundleNode bn : bns) {
			Bundle b = getBundle(bn.getId());
			if (b != null)
				try {
					if (start)
						b.start();
					else
						b.stop();
				} catch (Exception e) {
					// XXX todo
					e.printStackTrace();
				}
		}
	}

	@Override
	protected void stopBundlesAction(BundleNode[] bns) {
		startOrStopBundles(bns, false);
	}

	@Override
	protected void startBundlesAction(BundleNode[] bns) {
		startOrStopBundles(bns, true);
	}

}
