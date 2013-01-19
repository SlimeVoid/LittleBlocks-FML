package slimevoid.littleblocks.network.handlers;

import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketTileEntityLB;

public class PacketTileEntityHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketTileEntityLB();
	}

}
