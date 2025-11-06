package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class BorderColor extends MsxBiosViewer implements Patcher {

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
	public boolean canView(final byte[] bios) {

		if (!super.canView(bios)) {
			return false;
		}
		final byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		return (value >= (byte) 0x00) && (value <= (byte) 0x0f);
	}

	@Override
	public String getValue(final byte[] bios) {

		final byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		final int iValue = Byte.toUnsignedInt(value);
		return (value >= (byte) 0x00) && (value <= (byte) 0x0f)
				? String.format("COLOR ,,%d", iValue)
				: String.format("unknown BDRCLR (%02x)", value);
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
