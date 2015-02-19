package org.eclipse.ecf.mgmt.framework.startlevel;

import java.io.Serializable;

import org.osgi.framework.startlevel.dto.FrameworkStartLevelDTO;

public class FrameworkStartLevelMTO implements Serializable {

	private static final long serialVersionUID = -3980645399276634876L;
	private final int startLevel;
	private final int initialBundleStartLevel;

	public FrameworkStartLevelMTO(FrameworkStartLevelDTO dto) {
		this.startLevel = dto.startLevel;
		this.initialBundleStartLevel = dto.initialBundleStartLevel;
	}

	public FrameworkStartLevelMTO(int startLevel, int initialBundleStartLevel) {
		this.startLevel = startLevel;
		this.initialBundleStartLevel = initialBundleStartLevel;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public int getInitialBundleStartLevel() {
		return initialBundleStartLevel;
	}

	@Override
	public String toString() {
		return "FrameworkStartLevelMTO [startLevel=" + startLevel
				+ ", initialBundleStartLevel=" + initialBundleStartLevel + "]";
	}

}
