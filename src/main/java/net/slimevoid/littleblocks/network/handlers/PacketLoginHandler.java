package net.slimevoid.littleblocks.network.handlers;

import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.network.handlers.SubPacketHandler;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

public class PacketLoginHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocksSettings();
    }

}
