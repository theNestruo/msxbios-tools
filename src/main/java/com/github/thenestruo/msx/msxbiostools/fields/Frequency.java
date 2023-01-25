package com.github.thenestruo.msx.msxbiostools.fields;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class Frequency extends MsxBiosViewer implements Patcher {

	public static final Frequency INSTANCE = new Frequency();

	private static final byte[] PLAY_STATEMENT_TABLE_14400 = new byte[]{ (byte) 0x40, (byte) 0x00, (byte) 0x45, (byte) 0x14 };

	private static final byte[] PLAY_STATEMENT_TABLE_12000 = new byte[]{ (byte) 0x00, (byte) 0x00, (byte) 0x45, (byte) 0x12 };

	@Override
	public String getDescription() {
		return "Frequency";
	}

	@Override
	public String getValue(byte[] bios) {

		final byte msxid1 = bios[Msx.MSXID1];
		final int frequency = (msxid1 & (byte) 0x80) == (byte) 0x00 ? 60
				: 50;
		final int playStatementTable =
				  Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_14400) ? 14400
				: Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_12000) ? 12000
				: 0;

		return    playStatementTable == 14400 ? (frequency == 60 ? "60Hz" : "50Hz (wrong PLAY statement table)" )
				: playStatementTable == 12000 ? (frequency == 50 ? "50Hz" : "60Hz (wrong PLAY statement table)" )
				: String.format("%dHz (unknown PLAY statement table: %s)", frequency, Memory.toHex(bios, 0x7754, 4));
	}

	@Override
	public String getKey() {
		return "FREQUENCY";
	}

	@Override
	public boolean canPatch(byte[] bios) {

		return canView(bios)
				&& (Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_14400)
					|| Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_12000));
	}

	@Override
	public void patchValue(byte[] bios, String newValue) {

		switch (newValue) {
		case "60":
			bios[Msx.MSXID1] = (byte) (bios[Msx.MSXID1] & (byte) 0x7f);
			for (int i = 0, j = 0x7754; i < 4; i++, j++) {
				bios[j] = PLAY_STATEMENT_TABLE_14400[i];
			}
			return;

		case "50":
			bios[Msx.MSXID1] = (byte) (bios[Msx.MSXID1] | (byte) 0x80);
			for (int i = 0, j = 0x7754; i < 4; i++, j++) {
				bios[j] = PLAY_STATEMENT_TABLE_12000[i];
			}
			return;

		default:
			throw new IllegalArgumentException("Invalid value: " + newValue);
		}
	}
}
