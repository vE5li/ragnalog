package de.thebois.util;

import static de.thebois.util.ByteArrayUtil.binaryString;

public class PrintUtils {

    public static void printSignature(String signature, boolean isSourceAddress, boolean useColors) {
        if (useColors) {
            String prefix = isSourceAddress ? green() + "INCOMING " + none() : red() + "OUTGOING " + none();
            System.out.println("\n" + prefix + signature);
        } else {
            String prefix = isSourceAddress ? "<- " : "-> ";
            System.out.println("\n" + prefix + "[ " + signature + " ]");
        }
    }

    public static void printField(String name, byte[] value, boolean useColors) {
        if (useColors)
            System.out.println("  " + name + ": " + yellow() + binaryString(value) + none());
        else
            System.out.println("    " + name + " : " + binaryString(value));
    }

    public static void printField(String name, int value, boolean useColors) {
        if (useColors)
            System.out.println("  " + name + ": " + cyan() + Integer.toUnsignedString(value) + none());
        else
            System.out.println("    " + name + " : " + Integer.toUnsignedString(value));
    }

    public static void printField(String name, String value, boolean useColors) {
        if (useColors)
            System.out.println("  " + name + ": " + magenta() + value + none());
        else
            System.out.println("    " + name + " : " + value);
    }

    public static String none() {
        return (char) 27 + "[0m";
    }

    public static String red() {
        return (char) 27 + "[31m";
    }

    public static String green() {
        return (char) 27 + "[32m";
    }

    public static String yellow() {
        return (char) 27 + "[33m";
    }

    public static String magenta() {
        return (char) 27 + "[35m";
    }

    public static String cyan() {
        return (char) 27 + "[36m";
    }
}
