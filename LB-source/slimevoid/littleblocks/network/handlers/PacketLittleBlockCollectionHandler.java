package slimevoid.littleblocks.network.handlers;

import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import slimevoidlib.network.PacketUpdate;
import slimevoidlib.network.handlers.SubPacketHandler;

public class PacketLittleBlockCollectionHandler extends SubPacketHandler {

    @Override
    protected PacketUpdate createNewPacket() {
        return new PacketLittleBlocksCollection();
    }

}
