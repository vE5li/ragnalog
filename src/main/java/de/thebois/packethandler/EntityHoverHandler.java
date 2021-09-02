package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class EntityHoverHandler implements PacketHandler {

    @Override
    @PacketSignature({0x30, 0x0A})
    public void handle(byte[] payload) {

        int entityId = integer4bytes(payload, 2);
        String entityName = stringWithLength(payload, 6, 24);
        String partyName = stringWithLength(payload, 30, 24);
        String guildName = stringWithLength(payload, 54, 24);
        String guildPosition = stringWithLength(payload, 78, 24);
        byte[] unknown = getSliceOfArray(payload, 82, 4);

        printSignature("entity hover", true, false);
        printField("entity id", entityId, false);
        printField("entity name", entityName, false);
        printField("party name", partyName, false);
        printField("guild name", guildName, false);
        printField("guild position", guildPosition, false);
        printField("???", unknown, false);
    }
}
