package slimevoid.littleblocks.network.handlers;

import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleNotifyHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleNotify();
	}

}
