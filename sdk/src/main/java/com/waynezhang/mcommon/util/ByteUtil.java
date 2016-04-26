/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-9-11.
 */

package com.waynezhang.mcommon.util;

/**
 * byte转换工具类.
 *
 * @author James Shen
 */
public final class ByteUtil {

    private ByteUtil() {
    }

    /**
     * short转byte.
     *
     * @param in
     * @return
     */
    public static final byte[] toBytes(short in) {
        return toBytes(in, new byte[2], 0);
    }

    /**
     * short转byte.
     *
     * @param in
     * @param out
     * @param offset
     * @return
     */
    public static final byte[] toBytes(short in, byte[] out, int offset) {
        out[offset++] = (byte) ((in >> 8) & 0xFF);
        out[offset++] = (byte) (in & 0xFF);
        return out;
    }

    /**
     * int转byte.
     *
     * @param in
     * @return
     */
    public static final byte[] toBytes(int in) {
        return toBytes(in, new byte[4], 0);
    }

    /**
     * int转byte.
     *
     * @param in
     * @param out
     * @param offset
     * @return
     */
    public static final byte[] toBytes(int in, byte[] out, int offset) {
        out[offset++] = (byte) (in >> 24 & 0xFF);
        out[offset++] = (byte) (in >> 16 & 0xFF);
        out[offset++] = (byte) (in >> 8 & 0xFF);
        out[offset++] = (byte) (in & 0xFF);
        return out;
    }

    /**
     * long转byte.
     *
     * @param in
     * @return
     */
    public static final byte[] toBytes(long in) {
        return toBytes(in, new byte[8], 0);
    }

    /**
     * long转byte.
     *
     * @param in
     * @param out
     * @param offset
     * @return
     */
    public static final byte[] toBytes(long in, byte[] out, int offset) {
        out[offset++] = (byte) (in >> 56 & 0xFF);
        out[offset++] = (byte) (in >> 48 & 0xFF);
        out[offset++] = (byte) (in >> 40 & 0xFF);
        out[offset++] = (byte) (in >> 32 & 0xFF);
        out[offset++] = (byte) (in >> 24 & 0xFF);
        out[offset++] = (byte) (in >> 16 & 0xFF);
        out[offset++] = (byte) (in >> 8 & 0xFF);
        out[offset++] = (byte) (in & 0xFF);
        return out;
    }

    /**
     * 16进制String转byte.
     *
     * @param hexString
     * @return
     */
    public static final byte[] toBytes(String hexString) {
        int len = hexString.length() / 2;
        byte[] out = new byte[len];
        int pos = 0;
        for (int i = 0; i < len; i++) {
            out[i] = (byte) (Character.digit(hexString.charAt(pos++), 16) << 4 | Character.digit(hexString.charAt(pos++), 16));
        }
        return out;
    }

    /**
     * byte转short.
     *
     * @param in
     * @return
     */
    public static final short toShort(byte[] in) {
        return toShort(in, 0);
    }

    /**
     * byte转short.
     *
     * @param in
     * @param offset
     * @return
     */
    public static final short toShort(byte[] in, int offset) {
        return (short) (((in[offset++] & 0xFF) << 8) + (in[offset++] & 0xFF));
    }

    /**
     * byte转int.
     *
     * @param in
     * @return
     */
    public static final int toInt(byte[] in) {
        return toInt(in, 0);
    }

    /**
     * byte转int.
     *
     * @param in
     * @param offset
     * @return
     */
    public static final int toInt(byte[] in, int offset) {
        return (((in[offset++] & 0xFF) << 24) + ((in[offset++] & 0xFF) << 16) + ((in[offset++] & 0xFF) << 8) + (in[offset++] & 0xFF));
    }

    /**
     * byte转16进制String.
     *
     * @param in
     * @return
     */
    public static final String toHexString(byte[] in) {
        int len = in.length;
        StringBuilder sb = new StringBuilder(len * 2);
        String tmp;
        for (int i = 0; i < len; i++) {
            tmp = Integer.toHexString(in[i] & 0xFF);
            if (tmp.length() < 2) {
                sb.append(0);
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

    /**
     * 比较.
     *
     * @param source
     * @param expected
     * @return
     */
    public static final boolean compare(byte[] source, byte[] expected) {
        int len = source.length;
        if (len == expected.length) {
            for (int i = 0; i < len; i++) {
                if (source[i] != expected[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * byte转打印String.
     *
     * @param bytes
     * @return
     */
    public static final String toPrintString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(" ").append(b);
        }
        return sb.toString();
    }
}
