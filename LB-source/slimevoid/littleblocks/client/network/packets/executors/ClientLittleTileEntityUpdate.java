package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;

public class ClientLittleTileEntityUpdate implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.hasTileEntity()) {
				System.out.println("Has TileEntity");
				((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(world, false)
					.setBlockTileEntity(
							packet.xPosition,
							packet.yPosition,
							packet.zPosition,
							TileEntity.createAndLoadEntity(
									packetLB.getTileEntityData())
							);
			}
		}
	}

}
