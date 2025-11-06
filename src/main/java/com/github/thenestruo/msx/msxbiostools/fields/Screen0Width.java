package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class Screen0Width extends MsxBiosViewer implements Patcher {

	public static final Screen0Width INSTANCE = new Screen0Width();

	@Override
	public String getKey() {
		return "WIDTH";
	}

	@Override
	public String getPatchHelp() {
		return "Patch WIDTH: 1..40";
	}

	@Override
	public String getValue(final byte[] bios) {

		final byte value = bios[Msx.LINL40 - Msx.RDPRIM + 0x7f27];
		final int iValue = Byte.toUnsignedInt(value);
		return (iValue > 0 && value <= 80)
				? String.format("WIDTH %d", iValue)
				: String.format("unknown WIDTH (%02x)", value);
	}

	@Override
	public void patchValue(final byte[] bios, final String newValue) {

		final byte newValueByte = Byte.parseByte(newValue);
		if ((newValueByte < (byte) 1) || (newValueByte > (byte) 40)) {
			throw new IllegalArgumentException("Invalid WIDTH value: " + newValue);
		}

		bios[Msx.LINL40 - Msx.RDPRIM + 0x7f27] = newValueByte;
	}
}
