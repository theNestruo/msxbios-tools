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
	public String getHelp() {
		return "Border color: 0-15";
	}

	@Override
	public boolean canView(byte[] bios) {

		if (!super.canView(bios)) {
			return false;
		}
		byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		return (value >= (byte) 0x00) && (value <= (byte) 0x0f);
	}

	@Override
	public String getValue(byte[] bios) {

		byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		int iValue = Byte.toUnsignedInt(value);
		return (value >= (byte) 0x00) && (value <= (byte) 0x0f)
				? String.format("COLOR ,,%d", iValue)
				: String.format("unknown (%02x)", value);
	}

	@Override
	public void patchValue(byte[] bios, String newValue) {

		byte newValueByte = Byte.parseByte(newValue);
		if ((newValueByte < (byte) 0) || (newValueByte > (byte) 15)) {
			throw new IllegalArgumentException("Invalid value: " + newValue);
		}

		bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27] = newValueByte;
	}
}
