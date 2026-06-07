package com.github.thenestruo.msx.msxbiostools.fields;

import com.github.thenestruo.msx.msxbiostools.support.MsxBiosViewer;
import com.github.thenestruo.msx.msxbiostools.support.Patcher;
import com.github.thenestruo.msx.msxbiostools.utils.Memory;
import com.github.thenestruo.msx.msxbiostools.utils.Msx;

public class Frequency extends MsxBiosViewer implements Patcher {

	public static final Frequency INSTANCE = new Frequency();

	public static final String KEY = "frequency";
	public static final String PATCH_HELP = "Patch frequency: 50, 60";

	private static final byte[] PLAY_STATEMENT_TABLE_14400 = new byte[]{ (byte) 0x40, (byte) 0x00, (byte) 0x45, (byte) 0x14 };
	private static final byte[] PLAY_STATEMENT_TABLE_12000 = new byte[]{ (byte) 0x00, (byte) 0x00, (byte) 0x45, (byte) 0x12 };

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getPatchHelp() {
		return PATCH_HELP;
	}

	@Override
	public String getValue(final byte[] bios) {

		final byte msxid1 = bios[Msx.MSXID1];
		final int frequency = (msxid1 & (byte) 0x80) == (byte) 0x00 ? 60
				: 50;
		final int playStatementTable =
				  Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_14400) ? 14400
				: Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_12000) ? 12000
				: 0;

		return    playStatementTable == 14400 ? (frequency == 60 ? "60Hz" : "50Hz (incorrect PLAY statement table)" )
				: playStatementTable == 12000 ? (frequency == 50 ? "50Hz" : "60Hz (incorrect PLAY statement table)" )
				: "%dHz (unknown PLAY statement table: %s)".formatted(frequency, Memory.toHex(bios, 0x7754, 4));
	}

	@Override
	public boolean canPatch(final byte[] bios) {

		return this.canView(bios)
				&& (Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_14400)
					|| Memory.check(bios, 0x7754, PLAY_STATEMENT_TABLE_12000));
	}

	@Override
	public void patchValue(final byte[] bios, final String newValue) {

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
			throw new IllegalArgumentException("Invalid frequency value: " + newValue);
		}
	}
}
