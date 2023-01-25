package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class ScreenMode extends Msx1BiosViewer {

	public static final ScreenMode INSTANCE = new ScreenMode();

	@Override
	public String getDescription() {
		return "Screen mode";
	}

	@Override
	public boolean canView(byte[] bios) {

		return super.canView(bios)
				&& Memory.check(bios, 0x7d2e, Z80.CALL);
	}

	@Override
	public String getValue(byte[] bios) {

		final Integer screenModeAddress = Z80.getCallAddress(bios, 0x7d2e);
		return    screenModeAddress == null ? String.format("unknown (%s)", Memory.toHex(bios, 0x7d2e, 3))
				: screenModeAddress == Msx.INITXT ? "SCREEN 0 (INITXT)"
				: screenModeAddress == Msx.INIT32 ? "SCREEN 1 (INIT32)"
				: String.format("unknown (%04x)", screenModeAddress);
	}
}
