package com.github.thenestruo.msx.msxbiostools.fields;

import java.util.zip.CRC32;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;

public class Crc32 extends MsxBiosViewer {

	public static final Crc32 INSTANCE = new Crc32();

	@Override
	public String getKey() {
		return "crc32";
	}

	@Override
	public String getValue(byte[] bios) {

		CRC32 crc32Builder = new CRC32();
		crc32Builder.reset();
		crc32Builder.update(bios);
		return String.format("%08x", crc32Builder.getValue());
	}
}
