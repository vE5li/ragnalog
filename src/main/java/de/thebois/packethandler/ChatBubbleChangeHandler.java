package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class ChatBubbleChangeHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0xD8, 0x00})
    public void handle(byte[] payload) {

        if (payload.length == 6) { // handles by delete handler

            byte[] unknown0 = getSliceOfArray(payload, 2, 2);
            int roomId = integer2bytes(payload, 4);

            printSignature("delete chat room", true, false);
            printField("entity id (?)", unknown0, false);
            printField("room id", roomId, false);

            return;
        }

        byte[] unknown0 = getSliceOfArray(payload, 2, 2);
        int roomId = integer2bytes(payload, 4);
        byte[] unknown1 = getSliceOfArray(payload, 6, 4);
        byte[] unknown2 = getSliceOfArray(payload, 10, 12);

        // sometimes extends up to 33 instead of 22 ? (at 8 for players?)
        String name = stringWithLength(payload, 22, payload.length - 22); // replace with length
        // string is NOT zero terminated

        printSignature("change chat room", true, false);
        printField("???", unknown0, false);
        printField("room id", roomId, false);
        printField("entity id (?)", unknown1, false);
        printField("???", unknown2, false);
        printField("name", name, false);
    }
}
