package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.library.network.SlimevoidPayload;
import com.slimevoid.library.network.handlers.SubPacketHandler;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocks;

public class PacketLittleBlocksHandler extends SubPacketHandler {

    @Override
    protected SlimevoidPayload createNewPacket() {
        return new PacketLittleBlocks();
    }

}
