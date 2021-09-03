package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.ByteArrayUtil.stringWithLength;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class ChatBubbleCreateHandler implements PacketHandler {

    @Override
    @PacketSignature({})
    public void handle(byte[] payload, boolean useColors) {

        int length = integer2bytes(payload, 2);
        byte[] unknown0 = getSliceOfArray(payload, 4, 4);

        byte[] unknown1 = getSliceOfArray(payload, 8, 2);
        int roomId = integer2bytes(payload, 10);
        int capacity = integer1byte(payload, 12);
        byte[] unknown2 = getSliceOfArray(payload, 13, 4);
        String name = stringWithLength(payload, 17, length - 17);

        printSignature("new chat room", true, useColors);
        printField("length", length, useColors);
        printField("???", unknown0, useColors);
        printField("???", unknown1, useColors);
        printField("roomId", roomId, useColors);
        printField("capacity", capacity, useColors);
        printField("???", unknown2, useColors);
        printField("name", name, useColors);
    }
}
