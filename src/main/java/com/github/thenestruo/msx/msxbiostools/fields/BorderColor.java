package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class BorderColor extends MsxBiosViewer {

	public static final BorderColor INSTANCE = new BorderColor();

	@Override
	public String getDescription() {
		return "Border color";
	}

	@Override
	public String getValue(byte[] bios) {

		byte value = bios[Msx.BDRCLR - Msx.RDPRIM + 0x7f27];
		int iValue = Byte.toUnsignedInt(value);
		return (iValue > 0 && value <= 80)
				? String.format("COLOR ,,%d", iValue)
				: String.format("unknown (%02x)", value);
	}
}
