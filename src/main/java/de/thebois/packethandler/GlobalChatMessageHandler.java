package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class GlobalChatMessageHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0xC1, 0x02})
    public void handle(byte[] payload, boolean useColors) {

        int length = integer2bytes(payload, 2);
        byte[] unknown = getSliceOfArray(payload, 4, 4);
        byte[] color = getSliceOfArray(payload, 8, 4);
        String message = stringWithLength(payload, 12, length - 12);

        printSignature("message from player in global chat", true, useColors);
        printField("length", length, useColors);
        printField("???", unknown, useColors);
        printField("color (rgb)", color, useColors);
        printField("message", message, useColors);
    }
}
