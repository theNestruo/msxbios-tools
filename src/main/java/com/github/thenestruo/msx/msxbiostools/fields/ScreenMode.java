package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class ScreenMode extends Msx1BiosViewer implements Patcher {

	public static final ScreenMode INSTANCE = new ScreenMode();

	@Override
	public String getKey() {
		return "SCREEN";
	}

	@Override
	public String getPatchHelp() {
		return "Patch SCREEN: 0 (SCREEN 0/INITXT), 1 (SCREEN 1/INIT32)";
	}

	@Override
	public String getValue(final byte[] bios) {

		if (!Memory.check(bios, 0x7d2e, Z80.CALL)) {
			return null;
		}

		final Integer screenModeAddress = Z80.getCallAddress(bios, 0x7d2e);
		return    screenModeAddress == null ? "unknown SCREEN (%s)".formatted(Memory.toHex(bios, 0x7d2e, 3))
				: screenModeAddress == Msx.INITXT ? "SCREEN 0 (INITXT)"
				: screenModeAddress == Msx.INIT32 ? "SCREEN 1 (INIT32)"
				: "unknown SCREEN address (%04x)".formatted(screenModeAddress);
	}

	@Override
	public void patchValue(final byte[] bios, final String newValue) {

		switch (newValue) {
		case "0":
			Memory.set16bits(bios, 0x7d2f, Msx.INITXT);
			return;

		case "1":
			Memory.set16bits(bios, 0x7d2f, Msx.INIT32);
			return;

		default:
			throw new IllegalArgumentException("Invalid SCREEN value: " + newValue);
		}
	}
}
