package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class Delay extends Msx1BiosViewer {

	public static final Delay INSTANCE = new Delay();

	@Override
	public String getDescription() {
		return "Initial delay";
	}

	@Override
	public boolean canView(byte[] bios) {

		return super.canView(bios)
				&& Memory.check(bios, 0x7d0b, Z80.LD_B_N);
	}

	@Override
	public String getValue(byte[] bios) {

		final Byte value = Z80.getLdBValue(bios, 0x7d0b);
		return value != null
				? Byte.toString(value)
				: String.format("unknown (%s)", Memory.toHex(bios, 0x7d0b, 2));
	}

}
