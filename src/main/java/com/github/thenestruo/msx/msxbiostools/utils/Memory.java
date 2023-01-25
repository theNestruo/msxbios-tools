package com.github.thenestruo.msx.msxbiostools.utils;

public class Memory {

	public static final int get16bits(byte[] memory, int address) {

		return    Byte.toUnsignedInt(memory[address    ])
				+ Byte.toUnsignedInt(memory[address + 1]) * 0x0100;
	}

	public static final boolean check(byte[] memory, int pAddress, byte ... values) {

		int address = pAddress;
		for (byte value : values) {
			if (memory[address++] != value) {
				return false;
			}
		}
		return true;
	}

	public static final String toHex(byte[] memory, int address, int length) {

		StringBuilder sb = new StringBuilder();
		for (int i = address, n = address + length; i < n; i++) {
			sb.append(String.format("%02x", memory[i]));
		}
		return sb.toString();

	}
}
