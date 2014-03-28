package net.slimevoid.littleblocks.network.handlers;

import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.network.handlers.SubPacketHandler;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocks;

public class PacketLittleBlocksHandler extends SubPacketHandler {

	@Override
	protected PacketUpdate createNewPacket() {
		return new PacketLittleBlocks();
	}

}
