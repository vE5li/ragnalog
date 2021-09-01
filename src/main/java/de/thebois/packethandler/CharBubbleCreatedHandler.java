package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.ByteArrayUtil.stringWithLength;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class CharBubbleCreatedHandler implements PacketHandler {

    @Override
    @PacketSignature({})
    public void handle(byte[] payload) {
            int length = integer2bytes(payload, 2);
            byte[] unknown0 = getSliceOfArray(payload, 4, 4);

            byte[] unknown1 = getSliceOfArray(payload, 8, 2);
            int roomId = integer2bytes(payload, 10);
            int capacity = integer1byte(payload, 12);
            byte[] unknown2 = getSliceOfArray(payload, 13, 4);
            String name = stringWithLength(payload, 17, length - 17);

            printSignature("new chat room", true, false);
            printField("length", length, false);
            printField("???", unknown0, false);
            printField("???", unknown1, false);
            printField("roomId", roomId, false);
            printField("capacity", capacity, false);
            printField("???", unknown2, false);
            printField("name", name, false);
    }

}
