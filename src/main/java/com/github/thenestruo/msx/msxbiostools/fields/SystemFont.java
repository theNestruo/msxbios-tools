package com.github.thenestruo.msx.msxbiostools.fields;

import java.util.zip.CRC32;

import org.apache.commons.lang3.ArrayUtils;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class SystemFont extends MsxBiosViewer {

	public static final SystemFont INSTANCE = new SystemFont();

	@Override
	public String getKey() {
		return "font";
	}

	@Override
	public String getHeader() {
		return "System font";
	}

	@Override
	public String getValue(byte[] bios) {

		final int cgtabl = Memory.get16bits(bios, Msx.CGTABL);

		CRC32 crc32Builder = new CRC32();
		crc32Builder.reset();
		crc32Builder.update(ArrayUtils.subarray(bios, cgtabl, cgtabl + 0x0800));
		final long systemFontCrc32 = crc32Builder.getValue();

		return    systemFontCrc32 == 0xdc17e52fL ? "Japanese"
				: systemFontCrc32 == 0x1f8f9709L ? "Japanese (MSX2+)"
				: systemFontCrc32 == 0x4a576136L ? "Japanese (C-BIOS)"
				: systemFontCrc32 == 0xb6a01b07L ? "International"
				: systemFontCrc32 == 0xc81e7760L ? "International (DIN)"
				: systemFontCrc32 == 0x1b47913eL ? "International (Brazilian)"
				: systemFontCrc32 == 0xcce9bec4L ? "International (C-BIOS)"
				: systemFontCrc32 == 0x7ac42370L ? "Korean"
				: systemFontCrc32 == 0x37c99bb6L ? "Russian"
				: systemFontCrc32 == 0xef64e6c7L ? "Brazilian (Expert 1.0)"
				: systemFontCrc32 == 0x7421782fL ? "Brazilian (Expert 1.1)"
				: systemFontCrc32 == 0xa0571623L ? "Brazilian (Expert Turbo)"
				: systemFontCrc32 == 0x68f7ddabL ? "Brazilian (HotBit 1.1)"
				: systemFontCrc32 == 0xfd9a9b37L ? "Brazilian (HotBit 1.2)"
				: systemFontCrc32 == 0xf06e5273L ? "Brazilian (C-BIOS)"
				: systemFontCrc32 == 0x6a96416fL ? "Polish"
				: String.format("unknown (CRC32:%08x)", systemFontCrc32);
	}
}
