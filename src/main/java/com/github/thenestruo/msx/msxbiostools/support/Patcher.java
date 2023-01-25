package com.github.thenestruo.msx.msxbiostools.support;

public interface Patcher extends Viewer {

	String getKey();

	boolean canPatch(byte[] bios);

	void patchValue(byte[] bios, String newValue);
}
