package com.github.thenestruo.msx.msxbiostools.support;

import com.github.thenestruo.msx.msxbiostools.utils.Msx;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public abstract class MsxBiosViewer implements Viewer {

	/**
	 * @param bios the byte array
	 * @return true if the byte array looks like an MSX BIOS image
	 */
	public boolean canView(byte[] bios) {

		return (bios != null)
				&& (bios.length == 0x8000)
				&& (bios[Msx.CHKRAM   ] == Z80.DI)
				&& (bios[Msx.CHKRAM +1] == Z80.JP)
				&& (bios[Msx.SYNCHR   ] == Z80.JP)
				&& (bios[Msx.RDSLT    ] == Z80.JP)
				&& (bios[Msx.CHRGTR   ] == Z80.JP);
	}
}
