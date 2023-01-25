package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryBasicVersion extends MsxBiosViewer {

	public static final CountryBasicVersion INSTANCE = new CountryBasicVersion();

	@Override
	public String getDescription() {
		return "BASIC version";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid2 = bios[Msx.MSXID2];
		return    (msxid2 & (byte) 0xf0) == (byte) 0x00 ? "Jap"
				: (msxid2 & (byte) 0xf0) == (byte) 0x10 ? "Int"
				: String.format("unknown (%02x)", msxid2 & (byte) 0xf0);
	}
}
