package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosPatcher;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class BorderColor extends Msx1BiosPatcher {

	public static final BorderColor INSTANCE = new BorderColor();

	@Override
	public String getKey() {
		return "BDRCLR";
	}

	@Override
	public String getPatchHelp() {
		return "Patch BDRCLR: 0..15";
	}

	@Override
	public String getValue(final byte[] bios) {

		final byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		if ((value < (byte) 0x00) || (value > (byte) 0x0f)) {
			// Invalid value
			return null;
		}

		final int iValue = Byte.toUnsignedInt(value);
		return "COLOR ,,%d".formatted(iValue);
	}

	@Override
	public void patchValue(final byte[] bios, final String newValue) {

		final byte newValueByte = Byte.parseByte(newValue);
		if ((newValueByte < (byte) 0x00) || (newValueByte > (byte) 0x0f)) {
			throw new IllegalArgumentException("Invalid BDRCLR value: " + newValue);
		}

		bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27] = newValueByte;
	}
}
