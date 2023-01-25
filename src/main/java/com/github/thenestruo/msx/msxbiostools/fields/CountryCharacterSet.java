package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryCharacterSet extends MsxBiosViewer {

	public static final CountryCharacterSet INSTANCE = new CountryCharacterSet();

	@Override
	public String getKey() {
		return "charset";
	}

	@Override
	public String getHeader() {
		return "Character set";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid1 = bios[Msx.MSXID1];
		return    (msxid1 & (byte) 0x0f) == (byte) 0x00 ? "Jap"
				: (msxid1 & (byte) 0x0f) == (byte) 0x01 ? "Int"
				: (msxid1 & (byte) 0x0f) == (byte) 0x02 ? "Kor"
				: String.format("unknown (%02x)", msxid1 & (byte) 0x0f);
	}
}
