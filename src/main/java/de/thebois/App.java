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

                    if (!packetDistributor.distribute(data) && arguments.showUnknown) {

                        byte[] signature = getSliceOfArray(data, 0, 2);
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
