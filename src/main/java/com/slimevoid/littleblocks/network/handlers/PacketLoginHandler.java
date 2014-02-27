package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

import slimevoidlib.network.handlers.SubPacketHandler;
import slimevoidlib.network.PacketUpdate;

public class PacketLoginHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocksSettings();
    }

}
