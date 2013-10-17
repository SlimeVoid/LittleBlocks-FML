package slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlock;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlockClickedExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlock
			&& packet.getCommand().equals(CommandLib.BLOCK_CLICKED)) {
			PacketLittleBlock littlePacket = (PacketLittleBlock) packet;
			((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockClicked(	(World) LittleBlocks.proxy.getLittleWorld(world, false),
																					packet.xPosition,
																					packet.yPosition,
																					packet.zPosition,
																					packet.side,
																					entityplayer);
/*			if (world.getBlockId(	packet.xPosition,
									packet.yPosition,
									packet.zPosition) == ConfigurationLib.littleChunkID) {
				((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockClicked(	world,
																						packet.xPosition,
																						packet.yPosition,
																						packet.zPosition,
																						packet.side,
																						entityplayer,
																						((PacketLittleBlocks) packet).getSelectedX(),
																						((PacketLittleBlocks) packet).getSelectedY(),
																						((PacketLittleBlocks) packet).getSelectedZ());
			}*/
		}
	}
}
