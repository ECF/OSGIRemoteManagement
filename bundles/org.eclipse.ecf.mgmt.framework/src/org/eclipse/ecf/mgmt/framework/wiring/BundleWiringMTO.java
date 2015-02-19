/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.wiring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.ecf.mgmt.framework.resource.WireMTO;
import org.eclipse.ecf.mgmt.framework.resource.WiringMTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.dto.BundleWireDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO;
import org.osgi.framework.wiring.dto.BundleWiringDTO.NodeDTO;
import org.osgi.resource.dto.WireDTO;

public class BundleWiringMTO implements Serializable {

	private static final long serialVersionUID = 5685946064502260512L;
	private long bundle;
	private int root;
	private NodeMTO[] nodes;
	private BundleRevisionMTO[] resources;

	public static BundleWiringMTO[] createMTOs(Bundle b) {
		BundleWiringDTO[] dtos = b.adapt(BundleWiringDTO[].class);
		List<BundleWiringMTO> results = new ArrayList<BundleWiringMTO>(dtos.length);
		for (BundleWiringDTO dto : dtos)
			results.add(new BundleWiringMTO(dto));
		return results.toArray(new BundleWiringMTO[results.size()]);
	}

	public static BundleWiringMTO createMTO(BundleWiringDTO dto) {
		return new BundleWiringMTO(dto);
	}

	BundleWiringMTO(BundleWiringDTO dto) {
		this.bundle = dto.bundle;
		this.root = dto.root;
		this.nodes = createNodeMTOs(dto.nodes);
		this.resources = BundleRevisionMTO.createMTOs(dto.resources);
	}

	public long getBundle() {
		return bundle;
	}

	public int getRoot() {
		return root;
	}

	public NodeMTO[] getNodes() {
		return nodes;
	}

	public BundleRevisionMTO[] getResources() {
		return resources;
	}

	@Override
	public String toString() {
		return "BundleWiringMTO [bundle=" + bundle + ", root=" + root + ", nodes=" + Arrays.toString(nodes)
				+ ", resources=" + Arrays.toString(resources) + "]";
	}

	public static NodeMTO[] createNodeMTOs(Set<NodeDTO> dtos) {
		List<NodeMTO> results = new ArrayList<NodeMTO>(dtos.size());
		for (NodeDTO dto : dtos)
			results.add(new NodeMTO(dto));
		return results.toArray(new NodeMTO[results.size()]);
	}

	public static class NodeMTO extends WiringMTO {
		private static final long serialVersionUID = 2290401090833912940L;
		private boolean inUse;
		private boolean current;

		public NodeMTO(NodeDTO dto) {
			super(dto);
			this.inUse = dto.inUse;
			this.current = dto.current;
		}

		public boolean isInUse() {
			return inUse;
		}

		public boolean isCurrent() {
			return current;
		}

		@Override
		public String toString() {
			return "NodeMTO [inUse=" + inUse + ", current=" + current + ", id=" + getId() + ", capabilities="
					+ Arrays.toString(getCapabilities()) + ", requirements=" + Arrays.toString(getRequirements())
					+ ", providedWires=" + Arrays.toString(getProvidedWires()) + ", requiredWires="
					+ Arrays.toString(getRequiredWires()) + ", resource=" + getResource() + "]";
		}

		@Override
		protected WireMTO[] createMTOs(List<WireDTO> dtos) {
			List<BundleWireMTO> results = new ArrayList<BundleWireMTO>(dtos.size());
			for (WireDTO dto : dtos)
				results.add(new BundleWireMTO((BundleWireDTO) dto));
			return results.toArray(new BundleWireMTO[results.size()]);
		}

	}
}
