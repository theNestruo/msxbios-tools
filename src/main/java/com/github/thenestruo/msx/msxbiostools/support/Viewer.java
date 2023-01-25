package com.github.thenestruo.msx.msxbiostools.support;

public interface Viewer {

	String getDescription();

	boolean canView(byte[] bios);

	String getValue(byte[] bios);
}
