package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryKeyboardType extends MsxBiosViewer {

	public static final CountryKeyboardType INSTANCE = new CountryKeyboardType();

	@Override
	public String getDescription() {
		return "Keyboard type";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid2 = bios[Msx.MSXID2];
		return    (msxid2 & (byte) 0x0f) == (byte) 0x00 ? "Jap"
				: (msxid2 & (byte) 0x0f) == (byte) 0x01 ? "Int"
				: (msxid2 & (byte) 0x0f) == (byte) 0x02 ? "Fre"
				: (msxid2 & (byte) 0x0f) == (byte) 0x03 ? "UK"
				: (msxid2 & (byte) 0x0f) == (byte) 0x04 ? "Ger"
				: (msxid2 & (byte) 0x0f) == (byte) 0x05 ? "Rus"
				: (msxid2 & (byte) 0x0f) == (byte) 0x06 ? "Spa"
				: String.format("unknown (%02x)", msxid2 & (byte) 0x0f);
	}
}
