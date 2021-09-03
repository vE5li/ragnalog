package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class EntityMoveHandler implements PacketHandler {

    @Override
    @PacketSignature({(byte)0x86, 0x00})
    public void handle(byte[] payload, boolean useColors) {

        int entityId = integer4bytes(payload, 2);
        byte[] coordinates = getSliceOfArray(payload, 6, 5);
        byte[] orientation = getSliceOfArray(payload, 11, 1); // always 88 ?
        int timestamp = integer4bytes(payload, 12);

        int yPositionTo = (Byte.toUnsignedInt(coordinates[4])) | ((Byte.toUnsignedInt(coordinates[3]) & 0b11) << 8);
        int xPositionTo = ((Byte.toUnsignedInt(coordinates[3]) >> 2) & 0b111111) | ((Byte.toUnsignedInt(coordinates[2]) & 0b1111) << 6);
        int yPositionFrom = (Byte.toUnsignedInt(coordinates[2]) >> 4) | ((Byte.toUnsignedInt(coordinates[1]) & 0b111111) << 4);
        int xPositionFrom = (Byte.toUnsignedInt(coordinates[1]) >> 6) | (Byte.toUnsignedInt(coordinates[0]) << 2);

        printSignature("entity moving", true, useColors);
        printField("entity id", entityId, useColors);
        printField("x position from", xPositionFrom, useColors);
        printField("y position from", yPositionFrom, useColors);
        printField("x position to", xPositionTo, useColors);
        printField("y position to", yPositionTo, useColors);
        printField("timestamp", timestamp, useColors);
    }
}
