package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class CountryDateFormat extends MsxBiosViewer {

	public static final CountryDateFormat INSTANCE = new CountryDateFormat();

	@Override
	public String getKey() {
		return "dateformat";
	}

	@Override
	public String getHeader() {
		return "Date format";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid1 = bios[Msx.MSXID1];
		return    (msxid1 & (byte) 0x70) == (byte) 0x00 ? "Y-M-D"
				: (msxid1 & (byte) 0x70) == (byte) 0x10 ? "M-D-Y"
				: (msxid1 & (byte) 0x70) == (byte) 0x20 ? "D-M-Y"
				: String.format("unknown (%02x)", msxid1 & (byte) 0x70);
	}
}
