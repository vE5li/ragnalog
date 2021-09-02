package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class RequestPlayerMoveHandler implements PacketHandler {

    @Override
    @PacketSignature({0x5F, 0x03})
    public void handle(byte[] payload) {

        byte[] unknown = getSliceOfArray(payload, 2, 3);

        printSignature("request player move", false, false);
        printField("encrypted coordinates", unknown, false);
    }
}
