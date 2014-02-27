package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

import slimevoidlib.network.PacketUpdate;
import slimevoidlib.network.handlers.SubPacketHandler;

public class PacketLittleBlockEventHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocksEvents();
    }

}
