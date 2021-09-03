package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class ServerBroadcastMessageHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0xC3, 0x01})
    public void handle(byte[] payload, boolean useColors) {

        int length = integer2bytes(payload, 2);
        byte[] unknown0 = getSliceOfArray(payload, 4, 1);
        byte[] color = getSliceOfArray(payload, 5, 3);
        byte[] unknown1 = getSliceOfArray(payload, 8, 10);
        String message = stringWithLength(payload, 18, length - 18);

        printSignature("message from server in global chat", true, useColors);
        printField("???", unknown0, useColors);
        printField("color (brg)", color, useColors);
        printField("???", unknown1, useColors);
        printField("message", message, useColors);
    }
}
