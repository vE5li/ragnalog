package de.thebois;

import com.beust.jcommander.JCommander;
import org.apache.commons.codec.binary.Hex;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.util.Arrays;

public class App {

    public static void main(String[] args) throws Exception {

        Arguments arguments = new Arguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);

        PcapHandle handle;
        int snapLen = 65536;

        try {
            PcapNetworkInterface networkInterface = Pcaps.getDevByName(arguments.deviceName);
            PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
            handle = networkInterface.openLive(snapLen, mode, arguments.timeout);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("failed to initialize");
        }

        while (true) {
            try {
                Packet packet = handle.getNextPacketEx();

                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                String sourceAddress;
                String destinationAddress;

                sourceAddress = ipV4Packet.getHeader().getSrcAddr().getHostAddress();
                destinationAddress = ipV4Packet.getHeader().getDstAddr().getHostAddress();
                byte[] data = ipV4Packet.getPayload().getPayload().getRawData();

                boolean isServerSource = sourceAddress.equals(arguments.serverAddress);
                boolean isServerDestination = destinationAddress.equals(arguments.serverAddress);

                if (!isServerSource && !isServerDestination) {
                    continue;
                }

                if (!arguments.showIncoming && isServerSource) {
                    continue;
                }

                if (!arguments.showOutgoing && isServerDestination) {
                    continue;
                }

                byte[] signature = getSliceOfArray(data, 0, 2);

                if (signatureMatches(signature, 0x30, 0x0A)) {

                    int playerId = integer4bytes(data, 2);
                    String playerName = stringWithLength(data, 6, 24);
                    String partyName = stringWithLength(data, 30, 24);
                    String guildName = stringWithLength(data, 54, 24);
                    String guildPosition = stringWithLength(data, 78, 24);
                    byte[] unknown = getSliceOfArray(data, 82, 4);

                    printSignature("player hover", isServerSource, arguments.useColors);
                    printField("player id", playerId, arguments.useColors);
                    printField("player name", playerName, arguments.useColors);
                    printField("party name", partyName, arguments.useColors);
                    printField("guild name", guildName, arguments.useColors);
                    printField("guild position", guildPosition, arguments.useColors);
                    printField("???", unknown, arguments.useColors);

                } else if (signatureMatches(signature, 0xDF, 0x0A)) {

                    int npcId = integer4bytes(data, 2);
                    byte[] unknown1 = getSliceOfArray(data, 6, 4);
                    String npcName = stringWithLength(data, 10, 48);

                    printSignature("npc hover", isServerSource, arguments.useColors);
                    printField("npc id", npcId, arguments.useColors);
                    printField("???", unknown1, arguments.useColors);
                    printField("npc name", npcName, arguments.useColors);

                } else if (signatureMatches(signature, 0x8E, 0x00)) {

                    int length = integer2bytes(data, 2);
                    String message = stringWithLength(data, 4, length - 4);

                    printSignature("message from oneself", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0x8D, 0x00)) {

                    int length = integer2bytes(data, 2);
                    int playerId = integer4bytes(data, 4);
                    String message = stringWithLength(data, 8, length - 8);

                    printSignature("message from player nearby", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("player id", playerId, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0x7F, 0x01)) {

                    int length = integer2bytes(data, 2);
                    String message = stringWithLength(data, 4, length - 4);

                    printSignature("message from player in your guild", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0x9A, 0x00)) {

                    int length = integer2bytes(data, 2);
                    String message = stringWithLength(data, 4, length - 4);

                    printSignature("info message", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0x09, 0x01)) {

                    printSignature("message from player in your party", isServerSource, arguments.useColors);

                } else if (signatureMatches(signature, 0xC1, 0x02)) {

                    int length = integer2bytes(data, 2);
                    byte[] unknown = getSliceOfArray(data, 4, 4);
                    byte[] color = getSliceOfArray(data, 8, 4);
                    String message = stringWithLength(data, 12, length - 12);

                    printSignature("message from player in global chat", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("???", unknown, arguments.useColors);
                    printField("color (rgb)", color, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0xC3, 0x01)) {

                    int length = integer2bytes(data, 2);
                    byte[] unknown0 = getSliceOfArray(data, 4, 1);
                    byte[] color = getSliceOfArray(data, 5, 3);
                    byte[] unknown1 = getSliceOfArray(data, 8, 10);
                    String message = stringWithLength(data, 18, length - 18);

                    printSignature("message from server in global chat", isServerSource, arguments.useColors);

                    printField("???", unknown0, arguments.useColors);
                    printField("color (brg)", color, arguments.useColors);
                    printField("???", unknown1, arguments.useColors);
                    printField("message", message, arguments.useColors);

                } else if (signatureMatches(signature, 0xD7, 0x00)) {

                    int length = integer2bytes(data, 2);
                    byte[] unknown0 =  getSliceOfArray(data, 4, 4);

                    byte[] unknown1 =  getSliceOfArray(data, 8, 2);
                    int roomId =  integer2bytes(data, 10);
                    int capacity =  integer1byte(data, 12);
                    byte[] unknown2 =  getSliceOfArray(data, 13, 4);
                    String name = stringWithLength(data, 17, length - 17);

                    printSignature("new chat room", isServerSource, arguments.useColors);
                    printField("length", length, arguments.useColors);
                    printField("???", unknown0, arguments.useColors);
                    printField("???", unknown1, arguments.useColors);
                    printField("roomId", roomId, arguments.useColors);
                    printField("capacity", capacity, arguments.useColors);
                    printField("???", unknown2, arguments.useColors);
                    printField("name", name, arguments.useColors);

                } else if (signatureMatches(signature, 0xD8, 0x00)) {

                    if (data.length == 6) {

                        byte[] unknown0 =  getSliceOfArray(data, 2, 2);
                        int roomId = integer2bytes(data, 4);

                        printSignature("delete chat room", isServerSource, arguments.useColors);
                        printField("entity id (?)", unknown0, arguments.useColors);
                        printField("room id", roomId, arguments.useColors);
                        continue;
                    }

                    //int length = integer2bytes(data, 2);
                    //byte[] unknown1 = getSliceOfArray(data, 4, 4);

                    byte[] unknown0 =  getSliceOfArray(data, 2, 2);
                    int roomId =  integer2bytes(data, 4);
                    byte[] unknown1 = getSliceOfArray(data, 6, 4);
                    byte[] unknown2 =  getSliceOfArray(data, 10, 12);

                    // sometimes extends up to 33 instead of 22 ? (at 8 for players?)
                    String name = stringWithLength(data, 22, data.length - 22); // replace with length
                    // string is NOT zero terminated

                    printSignature("change chat room", isServerSource, arguments.useColors);
                    printField("???", unknown0, arguments.useColors);
                    printField("room id", roomId, arguments.useColors);
                    printField("entity id (?)", unknown1, arguments.useColors);
                    printField("???", unknown2, arguments.useColors);
                    printField("name", name, arguments.useColors);

                } else if (signatureMatches(signature, 0x07, 0x01)) { // fix this

                    int playerId = integer4bytes(data, 2);
                    int xPosition = integer2bytes(data, 6);
                    int yPosition = integer2bytes(data, 8);

                    printSignature("player moving", isServerSource, arguments.useColors);
                    printField("player id", playerId, arguments.useColors);
                    printField("x position", xPosition, arguments.useColors);
                    printField("y position", yPosition, arguments.useColors);

                } else if (signatureMatches(signature, 0x5F, 0x03)) {

                    byte[] unknown = getSliceOfArray(data, 2, 3);

                    printSignature("test", isServerSource, arguments.useColors);
                    printField("unknown", unknown, arguments.useColors);

                } else if (signatureMatches(signature, 0x60, 0x03)) {

                    if (arguments.showPing) {
                        int timestamp = integer4bytes(data, 2);

                        printSignature("ping (every 12 seconds)", isServerSource, arguments.useColors);
                        printField("timestamp", timestamp, arguments.useColors);
                    }

                } else if (signatureMatches(signature, 0x7F, 0x00)) {

                    if (arguments.showPing) {
                        byte[] unknown = getSliceOfArray(data, 2, 4);

                        printSignature("ping", isServerSource, arguments.useColors);
                        printField("???", unknown, arguments.useColors);
                    }

                } else if (signatureMatches(signature, 0x87, 0x00)) {

                    printSignature("oneself moving", isServerSource, arguments.useColors);

                } else if (signatureMatches(signature, 0x86, 0x00)) {

                    int playerId =  integer4bytes(data, 2);
                    byte[] unknown1 =  getSliceOfArray(data, 6, 4);
                    byte[] unknown2 =  getSliceOfArray(data, 10, 2);
                    int unknown3 =  integer4bytes(data, 12);

                    printSignature("player moving", isServerSource, arguments.useColors);
                    printField("playerId", playerId, arguments.useColors);
                    printField("???", unknown1, arguments.useColors);
                    printField("???", unknown2, arguments.useColors);
                    printField("timestamp (?)", unknown3, arguments.useColors);

                } else if (arguments.showUnknown) {

                    byte[] unknown = getSliceOfArray(data, 2, data.length - 2);

                    printSignature("unknown", isServerSource, arguments.useColors);
                    printField("signature", signature, arguments.useColors);
                    printField("unknown", unknown, arguments.useColors);

                }

            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
