package com.github.thenestruo.msx.msxbiostools.utils;

public class Z80 {

	public static final byte LD_B_N = (byte) 0x06;

	public static final byte LD_DE_NN = (byte) 0x11;

	public static final byte LD_HL_INDIRECT_N = (byte) 0x36;

	public static final byte LD_A_N = (byte) 0x3e;

	public static final byte JP = (byte) 0xc3;

	public static final byte CALL = (byte) 0xcd;

	public static final byte DI = (byte) 0xf3;

	//

	public static final boolean checkLdDeNn(byte[] memory, int atAddress, int address) {

		return Memory.check(memory, atAddress, LD_DE_NN, low(address), high(address));
	}

	public static final boolean checkCallTo(byte[] memory, int atAddress, int toAddress) {

		return Memory.check(memory, atAddress, CALL, low(toAddress), high(toAddress));
	}

	public static final Byte getLdBValue(byte[] memory, int atAddress) {

		return Memory.check(memory, atAddress, LD_B_N)
				? memory[atAddress + 1]
				: null;
	}

	public static final Byte getLdHlIndirectValue(byte[] memory, int atAddress) {

		return Memory.check(memory, atAddress, LD_HL_INDIRECT_N)
				? memory[atAddress + 1]
				: null;
	}

	public static final Byte getLdAValue(byte[] memory, int atAddress) {

		return Memory.check(memory, atAddress, LD_A_N)
				? memory[atAddress + 1]
				: null;
	}

	public static final Integer getCallAddress(byte[] memory, int atAddress) {

		return Memory.check(memory, atAddress, CALL)
				? Memory.get16bits(memory, atAddress + 1)
				: null;
	}

	private static final byte low(int address) {

		return (byte) (address & 0x00ff);
	}

	private static final byte high(int address) {

		return (byte) ((address & 0xff00) >> 8);
	}
}
