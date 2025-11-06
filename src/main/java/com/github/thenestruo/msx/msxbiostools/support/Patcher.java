package com.github.thenestruo.msx.msxbiostools.support;

public interface Patcher extends Viewer {

	default String getPatchHelp() {
		return this.getHeader();
	}

	default boolean canPatch(final byte[] bios) {
		return this.canView(bios);
	}

	void patchValue(byte[] bios, String newValue);
}
