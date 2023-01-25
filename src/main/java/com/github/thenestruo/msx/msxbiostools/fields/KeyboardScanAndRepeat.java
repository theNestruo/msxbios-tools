package com.github.thenestruo.msx.msxbiostools.fields;

import org.apache.commons.lang3.ObjectUtils;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class KeyboardScanAndRepeat extends MsxBiosViewer {

	public static final KeyboardScanAndRepeat INSTANCE = new KeyboardScanAndRepeat();

	@Override
	public String getDescription() {
		return "Keyboard scan";
	}

	@Override
	public boolean canView(byte[] bios) {

		return super.canView(bios)
				&& Memory.check(bios, 0x0c96, Z80.LD_HL_INDIRECT_N)
				&& Memory.check(bios, 0x0cf0, Z80.LD_HL_INDIRECT_N)
				&& Memory.check(bios, 0x0d49, Z80.LD_A_N);
	}

	@Override
	public String getValue(byte[] bios) {

		final Byte scncntResetValue = Z80.getLdHlIndirectValue(bios, 0x0c96);
		final Byte repcntResetValue = Z80.getLdHlIndirectValue(bios, 0x0cf0);
		final Byte repcntInitialValue = Z80.getLdAValue(bios, 0x0d49);
		return ObjectUtils.allNotNull(scncntResetValue, repcntInitialValue, repcntInitialValue)
				? String.format("Every %s frame(s) (repetition: %d/%d)", scncntResetValue, repcntInitialValue, repcntResetValue)
				: String.format("unknown (%s %s %s)",
						Memory.toHex(bios, 0x0c96, 2),
						Memory.toHex(bios, 0x0cf0, 2),
						Memory.toHex(bios, 0x0d49, 2));
	}
}
