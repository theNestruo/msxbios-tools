package com.github.thenestruo.msx.msxbiostools.fields;

import org.apache.commons.lang3.ObjectUtils;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Z80;

public class KeyboardScanAndRepeat extends MsxBiosViewer implements Patcher {

	public static final KeyboardScanAndRepeat INSTANCE = new KeyboardScanAndRepeat();

	@Override
	public String getKey() {
		return "SCNCNT";
	}

	@Override
	public String getHeader() {
		return "Keyboard scan and repeat count";
	}

	@Override
	public String getHelp() {
		return "Keyboard scan and repeat count: 1 (1 39/3), 2 (2 20/1), 3 (3 13/1), F9P (1 32/2)";
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

	@Override
	public void patchValue(byte[] bios, String newValue) {

		switch (newValue) {
		// Some MSX 1 50Hz (less common)
		case "1":
			bios[0x0c97] = 1;
			bios[0x0cf1] = 3;
			bios[0x0d4a] = 39;
			return;

		// Most Sony MSX 2/2+ 60Hz
		case "2":
			bios[0x0c97] = 2;
			bios[0x0cf1] = 1;
			bios[0x0d4a] = 20;
			return;

		// Most MSX 1
		case "3":
			bios[0x0c97] = 3;
			bios[0x0cf1] = 1;
			bios[0x0d4a] = 13;
			return;

		// Sony HB-F9P (MSX 2)
		case "F9P":
		case "f9p":
			bios[0x0c97] = 1;
			bios[0x0cf1] = 2;
			bios[0x0d4a] = 32;
			return;

		default:
			throw new IllegalArgumentException("Invalid value: " + newValue);
		}
	}
}
