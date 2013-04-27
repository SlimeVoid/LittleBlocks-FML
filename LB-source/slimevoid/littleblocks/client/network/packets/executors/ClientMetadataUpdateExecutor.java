package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class ClientMetadataUpdateExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks && packet.getCommand().equals(CommandLib.METADATA_MODIFIED)) {
			TileEntityLittleChunk tileentitylittleblocks = (TileEntityLittleChunk) world.getBlockTileEntity(
					packet.xPosition >> 3,
					packet.yPosition >> 3,
					packet.zPosition >> 3);
			if (tileentitylittleblocks != null) {
				tileentitylittleblocks.handleUpdateMetadata(world, entityplayer, (PacketLittleBlocks)packet);
			}
			world.markBlockForUpdate(
					packet.xPosition >> 3,
					packet.yPosition >> 3,
					packet.zPosition >> 3);
		}
	}

}
