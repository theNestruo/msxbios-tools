package com.github.thenestruo.msx.msxbiostools.utils;

public interface Msx {

	/** Power-up, check RAM */
	int CHKRAM = 0x0000;

	/** Two bytes, address of ROM character set */
	int CGTABL = 0x0004;

	/** Check BASIC program character */
	int SYNCHR = 0x0008;

	/** Read RAM in any slot */
	int RDSLT = 0x000c;

	/** Get next BASIC program character */
	int CHRGTR = 0x0010;

	/** Frecuency (1b), date format (3b) and charset (4b) */
	int MSXID1 = 0x002b;

	/** Basic version (4b) and Keybaord type (4b) */
	int MSXID2 = 0x002c;

	/** MSX version number */
	int MSXID3 = 0x002d;

	/** Initialize VDP to 40x24 Text Mode */
	int INITXT = 0x006c;

	/** Initialize VDP to 32x24 Text Mode */
	int INIT32 = 0x006f;

	/** Routine that reads from a primary slot */
	int RDPRIM = 0xf380;

	/** Width for SCREEN 0 (default 37) */
	int LINL40 = 0xf3ae;

	/** Border colour */
	int BDRCLR = 0xf3eb;

	/** Work aera of the instructions CALL and OPEN. Contents the instruction name or device name */
	int PROCNM = 0xfd89;
}
