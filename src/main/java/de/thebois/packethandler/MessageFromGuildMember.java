package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.integer2bytes;
import static de.thebois.util.ByteArrayUtil.stringWithLength;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class MessageFromGuildMember implements PacketHandler {

    @Override
    @PacketSignature({0x7F, 0x01})
    public void handle(byte[] payload) {

        int length = integer2bytes(payload, 2);
        String message = stringWithLength(payload, 4, length - 4);

        printSignature("message from player in your guild", true, false);
        printField("length", length, false);
        printField("message", message, false);
    }
}
