package com.github.thenestruo.msx.msxbiostools.fields;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.CRC32;

import org.tinylog.Logger;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosPatcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class SystemFont extends Msx1BiosPatcher {

	public static final SystemFont INSTANCE = new SystemFont();

	public static final String KEY = "font";
	public static final String PATCH_HELP = "Patch font: <input file>";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getPatchHelp() {
		return PATCH_HELP;
	}

	@Override
	public String getHeader() {
		return "System font";
	}

	@Override
	public String getValue(final byte[] bios) {

		final int cgtabl = Memory.get16bits(bios, Msx.CGTABL);

		final CRC32 crc32Builder = new CRC32();
		crc32Builder.reset();
		crc32Builder.update(Arrays.copyOfRange(bios, cgtabl, cgtabl + 0x0800));
		final long systemFontCrc32 = crc32Builder.getValue();

		return    systemFontCrc32 == 0x1f8f9709L ? "Japanese font (MSX2+)"
				: systemFontCrc32 == 0x4a576136L ? "Japanese font (C-BIOS)"
				: systemFontCrc32 == 0x896e9448L ? "Japanese font (Nikko PC-70100)"
				: systemFontCrc32 == 0xdc17e52fL ? "Japanese font"
				: systemFontCrc32 == 0xb6a01b07L ? "International font"
				: systemFontCrc32 == 0xc81e7760L ? "International font (DIN)"
				: systemFontCrc32 == 0xcce9bec4L ? "International font (C-BIOS)"
				: systemFontCrc32 == 0x7ac42370L ? "Korean font"
				//
				: systemFontCrc32 == 0x1b47913eL ? "Brazilian font"
				: systemFontCrc32 == 0x68f7ddabL ? "Brazilian font (HotBit 1.1)"
				: systemFontCrc32 == 0x7421782fL ? "Brazilian font (Expert 1.1)"
				: systemFontCrc32 == 0xa0571623L ? "Brazilian font (Expert Turbo)"
				: systemFontCrc32 == 0xef64e6c7L ? "Brazilian font (Expert 1.0)"
				: systemFontCrc32 == 0xf06e5273L ? "Brazilian font (C-BIOS)"
				: systemFontCrc32 == 0xfd9a9b37L ? "Brazilian font (HotBit 1.2)"
				: systemFontCrc32 == 0xe15baad4L ? "Danish/Norwegian font"
				: systemFontCrc32 == 0x6a96416fL ? "Polish font"
				: systemFontCrc32 == 0x37c99bb6L ? "Russian font"
				//
				: "unknown font (CRC32:%08x)".formatted(systemFontCrc32);
	}

	@Override
	public void patchValue(final byte[] bios, final String fontFilename) {

		final Path fontPath = Path.of(fontFilename);
		if (!Files.isReadable(fontPath)) {
			Logger.warn("Cannot patch {}: {} is not readable", KEY, fontFilename);
			return;
		}

		final byte[] font;
		try (final InputStream is = Files.newInputStream(fontPath)) {
			font = is.readNBytes(0x0800);
		} catch (IOException e) {
			Logger.warn("Cannot patch {}: {}:", KEY, fontFilename, e);
			return;
		}

		final int cgtabl = Memory.get16bits(bios, Msx.CGTABL);
		for (int from = 0x0000, to = cgtabl; from < 0x0800; from++, to++) {
			bios[to] = font[from];
		}
	}
}
