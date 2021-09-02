package de.thebois;

import com.beust.jcommander.JCommander;
import de.thebois.packethandler.reflection.PacketDistributor;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;


public class App {

    private static final int SNAP_LEN = 65536;

    public static void main(String[] args) throws Exception {

        PacketDistributor packetDistributor = PacketDistributor.getInstance();

        Arguments arguments = new Arguments();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        System.out.println(arguments);

        PcapNetworkInterface networkInterface = Pcaps.getDevByName(arguments.deviceName);
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

        try (PcapHandle handle = networkInterface.openLive(SNAP_LEN, mode, arguments.timeout)) {

            while (handle.isOpen()) {
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

                    if (!packetDistributor.distribute(data)) {
                        System.out.println("not implemented!");
                    }

                    if (signatureMatches(signature, 0x09, 0x01)) {

                        printSignature("message from player in your party", isServerSource, arguments.useColors);

                    }  else if (signatureMatches(signature, 0x07, 0x01)) { // fix this

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

                        int playerId = integer4bytes(data, 2);
                        byte[] unknown1 = getSliceOfArray(data, 6, 4);
                        byte[] unknown2 = getSliceOfArray(data, 10, 2);
                        int unknown3 = integer4bytes(data, 12);

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

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("failed to initialize");
        }
    }

}
