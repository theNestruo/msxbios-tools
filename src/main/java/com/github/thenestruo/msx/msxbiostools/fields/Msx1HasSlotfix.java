package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class Msx1HasSlotfix extends Msx1BiosViewer {

	public static final Msx1HasSlotfix INSTANCE = new Msx1HasSlotfix();

	@Override
	public String getKey() {
		return "SLOTFIX";
	}

	@Override
	public String getHeader() {
		return "Has SLOTFIX?";
	}

	@Override
	public String getValue(byte[] bios) {

		if (Memory.check(bios, 0x016f, (byte) 0x00, (byte) 0x00, (byte) 0x00)) {
			return "does not have SLOTFIX";
		}
		if (Z80.checkCallTo(bios, 0x016f, 0x01ad)) {
			return "has SLOTFIX";
		}
		return "unknown SLOTFIX status (%s)".formatted(Memory.toHex(bios, 0x016f, 3));
	}
}
