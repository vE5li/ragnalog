package de.thebois.packethandler;

import static de.thebois.util.ByteArrayUtil.*;
import static de.thebois.util.PrintUtils.printField;
import static de.thebois.util.PrintUtils.printSignature;

public class PartyChatMessageHandler implements PacketHandler {

    @Override
    @PacketSignature({0x09, 0x01})
    public void handle(byte[] payload) {

        printSignature("message from player in your party", true, false);
    }
}
