package slimevoid.littleblocks.network.handlers;

import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoidlib.network.PacketUpdate;

public class PacketLoginHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocksSettings();
	}

}
