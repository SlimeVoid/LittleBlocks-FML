package slimevoid.littleblocks.network.handlers;

import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;

public class PacketLittleBlocksHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocks();
	}

}
