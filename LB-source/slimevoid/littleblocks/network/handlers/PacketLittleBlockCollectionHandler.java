package slimevoid.littleblocks.network.handlers;

import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlockCollectionHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksCollection();
	}

}
