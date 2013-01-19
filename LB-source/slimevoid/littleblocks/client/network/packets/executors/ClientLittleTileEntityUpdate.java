package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;

public class ClientLittleTileEntityUpdate implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.hasTileEntity()) {
				System.out.println("Has TileEntity");
				TileEntity tileentity = world.getBlockTileEntity(
						packet.xPosition >> 3,
						packet.yPosition >> 3,
						packet.zPosition >> 3);
				if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
					TileEntity littleTile = TileEntity.createAndLoadEntity(
							packetLB.getTileEntityData());
					if (littleTile != null) {
						((TileEntityLittleBlocks)tileentity).setTileEntity(
								packet.xPosition & 7,
								packet.yPosition & 7,
								packet.zPosition & 7,
								littleTile);
					}
				}
			}
		}
	}

}
