package littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.network.packets.core.PacketIds;
import net.minecraft.src.EurysMods.network.packets.core.PacketTileEntity;
import net.minecraft.src.EurysMods.network.packets.core.PacketUpdate;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler {

	public static void handleTileEntityPacket(PacketTileEntity packet,
			EntityPlayer entityplayer, World world) {
		TileEntity tileentity = world.getBlockTileEntity(packet.xPosition,
				packet.yPosition, packet.zPosition);
		if (packet instanceof PacketTileEntityLB) {
			PacketTileEntityLB packetLB = (PacketTileEntityLB) packet;
			if (tileentity != null
					&& tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				switch (packetLB.payload.getIntPayload(0)) {
				case 0:
					int[][][] content = tileentitylb.getContent();
					switch (packetLB.payload.getIntPayload(1)) {
					case 0:
						break;
					case 1:
						for (int xx = 0; xx < 8; xx++) {
							for (int yy = 0; yy < 8; yy++) {
								for (int zz = 0; zz < 8; zz++) {
									tileentitylb
											.setContent(xx, yy, zz,
													packetLB.payload
															.getIntPayload(1
																	+ xx + yy
																	* 8 + zz
																	* 8 * 8),
													packetLB.payload
															.getIntPayload(1
																	+ 8 * 8 * 8
																	+ xx + yy
																	* 8 + zz
																	* 8 * 8));
								}
							}
						}
						break;
					case 2:
						for (int i = 0; i < (packetLB.payload.getIntSize() - 2) / 5; i++) {
							int xx = packetLB.payload
									.getIntPayload(2 + i * 5 + 0);
							int yy = packetLB.payload
									.getIntPayload(2 + i * 5 + 1);
							int zz = packetLB.payload
									.getIntPayload(2 + i * 5 + 2);
							int id = packetLB.payload
									.getIntPayload(2 + i * 5 + 3);
							int data = packetLB.payload
									.getIntPayload(2 + i * 5 + 4);
							tileentitylb.setContent(xx, yy, zz, id, data);
						}
						break;
					}
					tileentitylb.onInventoryChanged();
					world.markBlockNeedsUpdate(packet.xPosition,
							packet.yPosition, packet.zPosition);
					break;
				}
			}
		}
	}

	public static void blockUpdate(World world, EntityPlayer entityplayer,
			int x, int y, int z, int q, float a, float b, float c,
			BlockLittleBlocks block, String command) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(command, x, y, z,
				q, a, b, c, block.xSelected, block.ySelected, block.zSelected,
				block.blockID, block.side);
		ModLoader.sendPacket(packetLB.getPacket());
	}

	public static void handlePacket(PacketUpdate packet,
			EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.targetExists(world)) {
				TileEntity tileentity = packetLB.getTileEntity(world);
				if (tileentity != null
						&& tileentity instanceof TileEntityLittleBlocks) {
					TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
					switch (packet.payload.getIntPayload(0)) {
					case 0:
						tileentitylb.getLittleWorld().setBlockAndMetadata(
								packetLB.xPosition, packetLB.yPosition,
								packetLB.zPosition, packetLB.getBlockID(),
								packetLB.getMetadata());
						world.markBlockAsNeedsUpdate(packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ());
						break;
					}
				}
			}
		}
	}

	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			EntityPlayer entityplayer = (EntityPlayer) player;
			World world = entityplayer.worldObj;
			int packetID = data.read();
			switch (packetID) {
			case PacketIds.TILE:
				PacketTileEntityLB packetTileLB = new PacketTileEntityLB();
				packetTileLB.readData(data);
				ClientPacketHandler.handleTileEntityPacket(packetTileLB,
						entityplayer, world);
				break;
			case PacketIds.UPDATE:
				PacketLittleBlocks packetLB = new PacketLittleBlocks();
				packetLB.readData(data);
				ClientPacketHandler.handlePacket(packetLB, entityplayer, world);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
