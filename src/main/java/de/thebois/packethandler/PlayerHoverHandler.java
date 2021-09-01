package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class PlayerHoverHandler implements PacketHandler {
    @Override
    @PacketSignature({0x30, 0x0A})
    public void handle(byte[] payload) {
        int playerId = integer4bytes(payload, 2);
        String playerName = stringWithLength(payload, 6, 24);
        String partyName = stringWithLength(payload, 30, 24);
        String guildName = stringWithLength(payload, 54, 24);
        String guildPosition = stringWithLength(payload, 78, 24);
        byte[] unknown = getSliceOfArray(payload, 82, 4);

        printSignature("player hover", true, false);
        printField("player id", playerId, false);
        printField("player name", playerName, false);
        printField("party name", partyName, false);
        printField("guild name", guildName, false);
        printField("guild position", guildPosition, false);
        printField("???", unknown, false);
    }
}
