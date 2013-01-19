package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketTileEntityLB;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;

public class ClientPacketTileEntityLBExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketTileEntityLB && packet.getCommand().equals(CommandLib.UPDATE_CLIENT)) {
			PacketTileEntityLB packetLB = (PacketTileEntityLB) packet;
			TileEntity tileentity = packetLB.getTileEntity(world);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				tileentitylb.handleTilePacket(world, packetLB);
			}
		}
	}
}
