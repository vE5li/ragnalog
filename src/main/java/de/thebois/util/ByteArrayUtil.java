package de.thebois.util;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

public class ByteArrayUtil {

    public static boolean signatureMatches(byte[] signature, int first, int second) {
        return Arrays.equals(signature, new byte[]{(byte) first, (byte) second});
    }

    public static int integer1byte(byte[] data, int offset) {
        return Byte.toUnsignedInt(data[offset]);
    }

    public static int integer2bytes(byte[] data, int offset) {
        return Byte.toUnsignedInt(data[offset]) | Byte.toUnsignedInt(data[offset + 1]) << 8;
    }

    public static int integer4bytes(byte[] data, int offset) {
        return Byte.toUnsignedInt(data[offset]) | Byte.toUnsignedInt(data[offset + 1]) << 8 | Byte.toUnsignedInt(data[offset + 2]) << 16 | Byte.toUnsignedInt(data[offset + 3]) << 24;
    }

    public static String stringWithLength(byte[] data, int offset, int length) {
        return new String(getSliceOfArray(data, offset, length));
    }

    public static byte[] getSliceOfArray(byte[] array, int start, int length) {
        byte[] slice = new byte[length];
        System.arraycopy(array, start, slice, 0, length);
        return slice;
    }

    public static String binaryString(byte[] data) {
        String hexString = Hex.encodeHexString(data);
        StringBuilder formatted = new StringBuilder();

        while (!hexString.isEmpty()) {
            formatted.append(hexString.substring(0, 2));
            hexString = hexString.substring(2);

            if (!hexString.isEmpty()) {
                formatted.append(" ");
            }
        }

        return formatted.toString();
    }
}
