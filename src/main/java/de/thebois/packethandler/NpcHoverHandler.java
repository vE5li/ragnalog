package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class NpcHoverHandler implements PacketHandler {
    @Override
    //TODO: since java does not have unsigned bytes we maybe should just use ints here
    @PacketSignature({(byte) 0xDF, 0x0A})
    public void handle(byte[] payload) {

        int npcId = integer4bytes(payload, 2);
        byte[] unknown1 = getSliceOfArray(payload, 6, 4);
        String npcName = stringWithLength(payload, 10, 48);

        printSignature("npc hover", true, false);
        printField("npc id", npcId, false);
        printField("???", unknown1, false);
        printField("npc name", npcName, false);

    }
}
