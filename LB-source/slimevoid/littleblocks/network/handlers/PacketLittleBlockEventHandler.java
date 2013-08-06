package slimevoid.littleblocks.network.handlers;

import slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlockEventHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksEvents();
	}

}
