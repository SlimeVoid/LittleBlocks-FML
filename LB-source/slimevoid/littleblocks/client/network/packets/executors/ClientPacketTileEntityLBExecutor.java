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
				handleLittleTilePacket(world, packetLB, tileentitylb);
			}
		}
	}
	
	private static void handleLittleTilePacket(World world, PacketTileEntityLB packetLB, TileEntityLittleBlocks tileentitylb) {
		int numberOfBlocks = packetLB.payload.getIntPayload(0);
		int index = 1;
		for (int i = 0; i < numberOfBlocks; i++) {
			int id = packetLB.payload.getIntPayload(index), meta = packetLB.payload
					.getIntPayload(index + 1), x = packetLB.payload
					.getIntPayload(index + 2), y = packetLB.payload
					.getIntPayload(index + 3), z = packetLB.payload
					.getIntPayload(index + 4);
			tileentitylb.setContent(x, y, z, id, meta);
			index += 5;
		}
		tileentitylb.setTiles(packetLB.getTileEntities());
		world.markBlockForRenderUpdate(
				packetLB.xPosition,
				packetLB.yPosition,
				packetLB.zPosition);
	}

}
