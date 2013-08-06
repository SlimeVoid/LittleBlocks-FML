package slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlocksClickedExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks && packet.getCommand().equals(CommandLib.BLOCK_CLICKED)) {
			if (world.getBlockId(
					packet.xPosition,
					packet.yPosition,
					packet.zPosition) == LBCore.littleChunkID) {
				((BlockLittleChunk) LBCore.littleChunk)
						.onServerBlockClicked(
								world,
								packet.xPosition,
								packet.yPosition,
								packet.zPosition,
								packet.side,
								entityplayer,
								((PacketLittleBlocks) packet).getSelectedX(),
								((PacketLittleBlocks) packet).getSelectedY(),
								((PacketLittleBlocks) packet).getSelectedZ());
			}
		}
	}
}
