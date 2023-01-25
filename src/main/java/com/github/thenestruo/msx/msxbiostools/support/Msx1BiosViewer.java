package com.github.thenestruo.msx.msxbiostools.support;

import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public abstract class Msx1BiosViewer extends MsxBiosViewer {

	/**
	 * @param bios the byte array
	 * @return true if the byte array looks like an MSX 1 BIOS image
	 */
	public boolean canView(byte[] bios) {

		return super.canView(bios)
				&& (bios[Msx.MSXID3] == (byte) 0x00); // 0 = MSX 1
	}
}
