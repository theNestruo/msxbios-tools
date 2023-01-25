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
	public String getValue(byte[] bios) {

		if (Z80.checkLdDeNn(bios, 0x5600, Msx.PROCNM)) {
			return "no";
		}

		final Integer subroutineAddress = Z80.getCallAddress(bios, 0x5600);
		if (subroutineAddress == null) {
			return String.format("no (%s)", Memory.toHex(bios, 0x5600, 3));
		}

		if (Z80.checkLdDeNn(bios, subroutineAddress, Msx.PROCNM)) {
			return subroutineAddress == 0x7fb7 ? "yes" : "yes*";
		}

		return String.format("yes? (%s)", Memory.toHex(bios, subroutineAddress, 3));
	}
}
