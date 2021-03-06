package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.getSliceOfArray;
import static de.thebois.util.ByteArrayUtil.integer2bytes;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class ChatBubbleDeleteHandler implements PacketHandler {

    @Override
    //@PacketSignature({(byte)0xD8, 0x00})
    public void handle(byte[] payload, boolean useColors) {

        if (payload.length != 6) {
            return;
        }

        byte[] unknown0 = getSliceOfArray(payload, 2, 2);
        int roomId = integer2bytes(payload, 4);

        printSignature("delete chat room", true, useColors);
        printField("entity id (?)", unknown0, useColors);
        printField("room id", roomId, useColors);
    }
}
