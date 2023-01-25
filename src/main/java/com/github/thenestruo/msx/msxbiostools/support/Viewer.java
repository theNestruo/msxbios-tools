package com.github.thenestruo.msx.msxbiostools.support;

public interface Viewer {

	String getKey();

	default String getHeader() {
		return this.getKey();
	}

	boolean canView(byte[] bios);

	String getValue(byte[] bios);
}
