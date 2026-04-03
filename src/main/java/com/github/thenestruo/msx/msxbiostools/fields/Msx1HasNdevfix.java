package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.Msx1BiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class Msx1HasNdevfix extends Msx1BiosViewer {

	public static final Msx1HasNdevfix INSTANCE = new Msx1HasNdevfix();

	@Override
	public String getKey() {
		return "NDEVFIX";
	}

	@Override
	public String getHeader() {
		return "Has NDEVFIX?";
	}

	@Override
	public String getValue(final byte[] bios) {

		if (Z80.checkLdDeNn(bios, 0x5600, Msx.PROCNM)) {
			return "does not have NDEVFIX";
		}

		final Integer subroutineAddress = Z80.getCallAddress(bios, 0x5600);
		if ((subroutineAddress == null) || (subroutineAddress >= 0x8000)) {
			return String.format("does not have NDEVFIX (%s)", Memory.toHex(bios, 0x5600, 3));
		}

		if (Z80.checkLdDeNn(bios, subroutineAddress, Msx.PROCNM)) {
			return subroutineAddress == 0x7fb7 ? "has NDEVFIX" : "has NDEVFIX*";
		}

		return String.format("unknown NDEVFIX status (%s)", Memory.toHex(bios, subroutineAddress, 3));
	}
}
