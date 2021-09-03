package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.integer2bytes;
import static de.thebois.util.ByteArrayUtil.stringWithLength;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class InfoMessageHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0x9A, 0x00})
    public void handle(byte[] payload, boolean useColors) {

        int length = integer2bytes(payload, 2);
        String message = stringWithLength(payload, 4, length - 4);

        printSignature("info message", true, useColors);
        printField("length", length, useColors);
        printField("message", message, useColors);
    }
}
