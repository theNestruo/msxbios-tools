package com.github.thenestruo.msx.msxbiostools.support;

public interface Patcher extends Viewer {

	default String getHelp() {
		return this.getHeader();
	}

	default boolean canPatch(byte[] bios) {
		return canView(bios);
	}

	void patchValue(byte[] bios, String newValue);
}
