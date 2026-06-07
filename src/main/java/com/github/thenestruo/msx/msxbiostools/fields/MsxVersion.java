package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class MsxVersion extends MsxBiosViewer {

	public static final MsxVersion INSTANCE = new MsxVersion();

	@Override
	public String getKey() {
		return "msx";
	}

	@Override
	public String getHeader() {
		return "MSX version";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid3 = bios[Msx.MSXID3];
		return    msxid3 == (byte) 0x00 ? "MSX 1"
				: msxid3 == (byte) 0x01 ? "MSX 2"
				: msxid3 == (byte) 0x02 ? "MSX 2+"
				: msxid3 == (byte) 0x03 ? "MSX turbo R"
				: "unknown MSX version (%02x)".formatted(msxid3);
	}
}
