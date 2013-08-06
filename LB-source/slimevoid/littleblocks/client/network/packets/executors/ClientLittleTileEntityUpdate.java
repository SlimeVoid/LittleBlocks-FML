package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientLittleTileEntityUpdate implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.hasTileEntity()) {
				TileEntity tileentity = world.getBlockTileEntity(
						packet.xPosition >> 3,
						packet.yPosition >> 3,
						packet.zPosition >> 3);
				if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
					TileEntityLittleChunk tileentitylb = (TileEntityLittleChunk) tileentity;
					tileentitylb.handleLittleTilePacket(world, packetLB);
				}
			}
		}
	}

}
