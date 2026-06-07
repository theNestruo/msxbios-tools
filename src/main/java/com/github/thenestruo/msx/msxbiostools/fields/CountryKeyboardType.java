package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryKeyboardType extends MsxBiosViewer {

	public static final CountryKeyboardType INSTANCE = new CountryKeyboardType();

	@Override
	public String getKey() {
		return "keyboard";
	}

	@Override
	public String getHeader() {
		return "Keyboard type";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid2 = bios[Msx.MSXID2];
		return    (msxid2 & (byte) 0x0f) == (byte) 0x00 ? "Jap keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x01 ? "Int keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x02 ? "Fre keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x03 ? "UK keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x04 ? "Ger keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x05 ? "Rus keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x06 ? "Spa keyboard"
				: (msxid2 & (byte) 0x0f) == (byte) 0x07 ? "Swe keyboard"
				: "unknown keyboard (%02x)".formatted(msxid2 & (byte) 0x0f);
	}
}
