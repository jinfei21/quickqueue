package com.ctrip.quickqueue.util;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtils {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(ByteUtils.class);

	public static final int SIZE_OF_INT = Integer.SIZE / Byte.SIZE;
	public static final int SIZE_OF_LONG = Long.SIZE / Byte.SIZE;
    public static final String UTF8_ENCODING = "UTF-8";

	public static void setByte(byte[] dest, int offset, byte src) {
		dest[offset] = src;
	}

	public static byte toByte(byte[] src, int offset) {
		return src[offset];
	}

	public static void setString(byte[] dest, int offset, int len, String src) {
		if (src == null) {
			return;
		}
		byte[] srcBytes = src.getBytes();
		int length = Math.min(len, srcBytes.length);
		System.arraycopy(srcBytes, 0, dest, offset, length);
	}

	public static int toInt(byte[] bytes, int offset) {
		return toInt(bytes, offset, SIZE_OF_INT);
	}

	public static int toInt(byte[] bytes, int offset, int length) {
		if (length != SIZE_OF_INT || offset + length > bytes.length) {
			throw new IllegalArgumentException("argment error!");
		}

		int n = 0;
		for (int i = offset; i < (offset + length); i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}

	public static void setLong(byte[] b, long n) {
		setLong(b, 0, n);
	}

	public static void setLong(byte[] b, int offset, long n) {
		b[offset + 0] = (byte) (n >>> 56);
		b[offset + 1] = (byte) (n >>> 48);
		b[offset + 2] = (byte) (n >>> 40);
		b[offset + 3] = (byte) (n >>> 32);
		b[offset + 4] = (byte) (n >>> 24);
		b[offset + 5] = (byte) (n >>> 16);
		b[offset + 6] = (byte) (n >>> 8);
		b[offset + 7] = (byte) (n >>> 0);
	}
	
	public static long toLong(byte[] bytes, int offset){
		return toLong(bytes,offset,SIZE_OF_LONG);
	}
	
	public static long toLong(byte[] bytes, int offset, int length) {
		if (length != SIZE_OF_LONG || offset + length > bytes.length) {
			throw new IllegalArgumentException("argment error!");
		}

		long n = 0;
		for (int i = offset; i < (offset + length); i++) {
			n <<= 8;
			n ^= bytes[i] & 0xFF;
		}
		return n;
	}

	public static String toString(final byte[] b1, String sep, final byte[] b2) {
		return toString(b1, 0, b1.length) + sep + toString(b2, 0, b2.length);
	}

	public static String toString(final byte[] b, int off, int len) {
		if (b == null) {
			return null;
		}
		if (len == 0) {
			return "";
		}
		try {
			return (new String(b, off, len, UTF8_ENCODING)).trim();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Only supported UTF-8! ", e);
			return null;
		}
	}

	public static byte[] concat(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
	}
}
