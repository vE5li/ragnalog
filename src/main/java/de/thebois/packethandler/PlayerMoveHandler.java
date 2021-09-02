package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class PlayerMoveHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0x87, 0x00})
    public void handle(byte[] payload) {

        int timestamp = integer4bytes(payload, 2);
        byte[] coordinates = getSliceOfArray(payload, 6, 5);
        byte[] orientation = getSliceOfArray(payload, 11, 1); // always 88 ?

        int yPositionTo = Byte.toUnsignedInt(coordinates[4]) | ((Byte.toUnsignedInt(coordinates[3]) & 0b11) << 8);
        int xPositionTo = (Byte.toUnsignedInt(coordinates[3]) >> 2) | ((Byte.toUnsignedInt(coordinates[2]) & 0b1111) << 6);
        int yPositionFrom = (Byte.toUnsignedInt(coordinates[2]) >> 4) | ((Byte.toUnsignedInt(coordinates[1]) & 0b111111) << 4);
        int xPositionFrom = (Byte.toUnsignedInt(coordinates[1]) >> 6) | (Byte.toUnsignedInt(coordinates[0]) << 2);

        printSignature("player moving", true, false);
        printField("timestamp", timestamp, false);
        printField("x position from", xPositionFrom, false);
        printField("y position from", yPositionFrom, false);
        printField("x position to", xPositionTo, false);
        printField("y position to", yPositionTo, false);
    }
}
