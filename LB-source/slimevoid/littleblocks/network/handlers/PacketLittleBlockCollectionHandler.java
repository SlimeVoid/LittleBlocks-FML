package slimevoid.littleblocks.network.handlers;

import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;

public class PacketLittleBlockCollectionHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksCollection();
	}

}
