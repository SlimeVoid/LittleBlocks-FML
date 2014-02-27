package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.littleblocks.network.packets.PacketLittleBlocks;

import slimevoidlib.network.PacketUpdate;
import slimevoidlib.network.handlers.SubPacketHandler;

public class PacketLittleBlocksHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocks();
    }

}
