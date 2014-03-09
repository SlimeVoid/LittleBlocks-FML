package com.slimevoid.littleblocks.network.handlers;

import com.slimevoid.library.network.SlimevoidPayload;
import com.slimevoid.library.network.handlers.SubPacketHandler;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;

public class PacketLittleBlockCollectionHandler extends SubPacketHandler {

    @Override
    protected SlimevoidPayload createNewPacket() {
        return new PacketLittleBlocksCollection();
    }

}
