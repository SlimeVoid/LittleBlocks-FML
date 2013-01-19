package slimevoid.littleblocks.network.handlers;

import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

public class PacketLoginHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksSettings();
	}

}
