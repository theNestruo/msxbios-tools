package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;

/**
 * FunctionKeys
 */
public class FunctionKeys extends MsxBiosViewer {

	public static final FunctionKeys INSTANCE = new FunctionKeys();

	private static final int BASE_ADDRESS = 0x13A9;
	private static final int KEYS_COUNT = 10;
	private static final int KEY_LENGTH = 16;

	@Override
	public String getKey() {
		return "KEY";
	}

	@Override
	public String getHeader() {
		return "Function keys";
	}

	@Override
	public String getValue(byte[] bios) {

		final StringBuilder value = new StringBuilder();
		for (int i = 0; i < KEYS_COUNT * KEY_LENGTH; i++) {
			final byte c = bios[BASE_ADDRESS + i];
			if ((c >= 0x20) & (c < 0x7f)) {
				value.append((char) c);
			} else {
				value.append('[').append(Integer.toHexString((int) c)).append(']');
			}
		}
		return value.toString();
	}
}
