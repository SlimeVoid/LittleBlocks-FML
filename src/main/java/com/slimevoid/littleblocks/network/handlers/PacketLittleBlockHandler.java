package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.littleblocks.network.packets.PacketLittleBlock;

import slimevoidlib.network.PacketUpdate;
import slimevoidlib.network.handlers.SubPacketHandler;

public class PacketLittleBlockHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlock();
    }

}
