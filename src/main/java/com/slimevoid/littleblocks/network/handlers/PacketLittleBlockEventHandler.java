package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.library.network.handlers.SubPacketHandler;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

public class PacketLittleBlockEventHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocksEvents();
    }

}
