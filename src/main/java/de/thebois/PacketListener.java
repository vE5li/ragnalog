package de.thebois;

import de.thebois.packethandler.reflection.PacketDistributor;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.thebois.util.ByteArrayUtil.getSliceOfArray;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class PacketListener {

    private static final int SNAP_LEN = 65536;
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketListener.class);

    private final PacketDistributor packetDistributor;
    private final PcapNetworkInterface networkInterface;
    private final PcapNetworkInterface.PromiscuousMode mode;
    private Arguments arguments;

    public PacketListener(Arguments arguments) throws PcapNativeException {
        this.arguments = arguments;

        packetDistributor = PacketDistributor.getInstance();
        networkInterface = Pcaps.getDevByName(arguments.deviceName);
        mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
    }

    public void runAsync() {
        Thread t = new Thread(this::run);
        t.start();
    }

    public void run() {
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

                    if (!packetDistributor.distribute(data, arguments.useColors) && arguments.showUnknown) {

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
            LOGGER.warn("oh no!", e);
        }
    }
}
