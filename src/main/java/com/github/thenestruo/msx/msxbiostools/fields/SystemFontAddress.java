package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class SystemFontAddress extends MsxBiosViewer {

	public static final SystemFontAddress INSTANCE = new SystemFontAddress();

	@Override
	public String getKey() {
		return "CGTABL";
	}

	@Override
	public String getValue(byte[] bios) {

		return String.format("CGTABL at %04x", Memory.get16bits(bios, Msx.CGTABL));
	}

}
