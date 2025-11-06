package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryBasicVersion extends MsxBiosViewer {

	public static final CountryBasicVersion INSTANCE = new CountryBasicVersion();

	@Override
	public String getKey() {
		return "BASIC";
	}

	@Override
	public String getHeader() {
		return "BASIC version";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid2 = bios[Msx.MSXID2];
		return    (msxid2 & (byte) 0xf0) == (byte) 0x00 ? "Jap BASIC"
				: (msxid2 & (byte) 0xf0) == (byte) 0x10 ? "Int BASIC"
				: String.format("unknown BASIC (%02x)", (byte) (msxid2 & (byte) 0xf0));
	}
}
