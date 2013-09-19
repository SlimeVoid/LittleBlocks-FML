package slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlocksActivatedExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks
			&& packet.getCommand().equals(CommandLib.BLOCK_ACTIVATED)) {
			if (world.getBlockId(	packet.xPosition,
									packet.yPosition,
									packet.zPosition) == ConfigurationLib.littleChunkID) {
				((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockActivated(	world,
																							packet.xPosition,
																							packet.yPosition,
																							packet.zPosition,
																							entityplayer,
																							packet.side,
																							packet.hitX,
																							packet.hitY,
																							packet.hitZ,
																							((PacketLittleBlocks) packet).getSelectedX(),
																							((PacketLittleBlocks) packet).getSelectedY(),
																							((PacketLittleBlocks) packet).getSelectedZ(),
																							((PacketLittleBlocks) packet).getMetadata());
			}
		}
	}

}
