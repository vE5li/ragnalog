package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class MessageFromPlayerNearbyHandler implements PacketHandler {


    @Override
    @PacketSignature({(byte) 0x8D, 0x00})
    public void handle(byte[] payload) {

        int length = integer2bytes(payload, 2);
        int playerId = integer4bytes(payload, 4);
        String message = stringWithLength(payload, 8, length - 8);

        printSignature("message from player nearby", true, false);
        printField("length", length, false);
        printField("player id", playerId, false);
        printField("message", message, false);


    }
}
