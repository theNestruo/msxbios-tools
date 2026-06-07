package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class Delay extends Msx1BiosViewer implements Patcher {

	public static final Delay INSTANCE = new Delay();

	public static final String KEY = "delay";
	public static final String PATCH_HELP = "Patch initial delay: 1..6";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getHeader() {
		return "Initial delay";
	}

	@Override
	public String getPatchHelp() {
		return PATCH_HELP;
	}

	@Override
	public String getValue(final byte[] bios) {

		if (!Memory.check(bios, 0x7d0b, Z80.LD_B_N)) {
			return null;
		}

		final Byte value = Z80.getLdBValue(bios, 0x7d0b);
		return value != null
				? Byte.toString(value)
				: "unknown delay (%s)".formatted(Memory.toHex(bios, 0x7d0b, 2));
	}

	@Override
	public void patchValue(final byte[] bios, final String newValue) {

		final byte newValueByte = Byte.parseByte(newValue);
		if ((newValueByte < (byte) 1) || (newValueByte > (byte) 6)) {
			throw new IllegalArgumentException("Invalid delay value: " + newValue);
		}

		bios[0x7d0c] = newValueByte;
	}
}
