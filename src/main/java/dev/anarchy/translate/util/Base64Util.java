package dev.anarchy.translate.util;

import java.util.Base64;

public class Base64Util {
	/**
	 * Encode a string to Base64.
	 */
	public String encode(String src) {
		return Base64.getEncoder().encodeToString(src.getBytes());
	}

	/**
	 * Decode a Base64 String in to a human-readable string.
	 */
	public String decode(String src) {
		return new String(Base64.getDecoder().decode(src));
	}

	/**
	 * Encode a byte array to Base64.
	 */
	public String encodeByteArray(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * Decode a Base64 byte array in to a human-readable string.
	 */
	public String decodeByteArray(byte[] bytes) {
		return new String(Base64.getDecoder().decode(bytes));
	}
}