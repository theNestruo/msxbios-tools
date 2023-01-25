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
	public String getHeader() {
		return "Screen 0 width";
	}

	@Override
	public String getHelp() {
		return "Screen 0 width: 1-40";
	}

	@Override
	public String getValue(byte[] bios) {

		byte value = bios[Msx.LINL40 - Msx.RDPRIM + 0x7f27];
		int iValue = Byte.toUnsignedInt(value);
		return (iValue > 0 && value <= 80)
				? String.format("WIDTH %d", iValue)
				: String.format("unknown (%02x)", value);
	}

	@Override
	public void patchValue(byte[] bios, String newValue) {

		byte newValueByte = Byte.parseByte(newValue);
		if ((newValueByte < (byte) 1) || (newValueByte > (byte) 40)) {
			throw new IllegalArgumentException("Invalid value: " + newValue);
		}

		bios[Msx.LINL40 - Msx.RDPRIM + 0x7f27] = newValueByte;
	}
}
