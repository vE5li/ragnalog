package de.thebois.packethandler;


public class PlayerHoverHandler implements PacketHandler{
    @Override
    @PacketSignature({0x30, 0x0a})
    public void handle(byte[] payload) {
    System.out.println("PlayerHoverHandler");
    }
}
