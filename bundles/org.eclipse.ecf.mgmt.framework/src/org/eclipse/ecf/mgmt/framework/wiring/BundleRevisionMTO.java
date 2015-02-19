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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.ecf.mgmt.framework.resource.ResourceMTO;
import org.osgi.framework.wiring.dto.BundleRevisionDTO;

public class BundleRevisionMTO extends ResourceMTO {

	private static final long serialVersionUID = -6185986569418852373L;
	private final String symbolicName;
	private final int type;
	private final long bundle;

	public static BundleRevisionMTO[] createMTOs(Set<BundleRevisionDTO> dtos) {
		List<BundleRevisionMTO> results = new ArrayList<BundleRevisionMTO>(dtos.size());
		for (BundleRevisionDTO dto : dtos)
			results.add(createMTO(dto));
		return results.toArray(new BundleRevisionMTO[results.size()]);
	}

	public static BundleRevisionMTO[] createMTOs(BundleRevisionDTO[] dtos) {
		List<BundleRevisionMTO> results = new ArrayList<BundleRevisionMTO>(dtos.length);
		for (BundleRevisionDTO dto : dtos)
			results.add(createMTO(dto));
		return results.toArray(new BundleRevisionMTO[results.size()]);
	}

	public static BundleRevisionMTO createMTO(BundleRevisionDTO dto) {
		return new BundleRevisionMTO(dto);
	}

	BundleRevisionMTO(BundleRevisionDTO dto) {
		super(dto);
		this.symbolicName = dto.symbolicName;
		this.type = dto.type;
		this.bundle = dto.bundle;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public int getType() {
		return type;
	}

	public long getBundle() {
		return bundle;
	}

	@Override
	public String toString() {
		return "BundleRevisionMTO [symbolicName=" + symbolicName + ", type=" + type + ", bundle=" + bundle + ", id="
				+ getId() + ", capabilities=" + Arrays.toString(getCapabilities()) + ", requirements="
				+ Arrays.toString(getRequirements()) + "]";
	}

}
