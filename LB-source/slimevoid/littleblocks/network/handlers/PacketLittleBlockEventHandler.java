package slimevoid.littleblocks.network.handlers;

import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

public class PacketLittleBlockEventHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksEvents();
	}

}
