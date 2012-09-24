package littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.LBCore;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketLittleBlocksSettings;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.network.packets.core.PacketIds;
import net.minecraft.src.EurysMods.network.packets.core.PacketTileEntity;
import net.minecraft.src.EurysMods.network.packets.core.PacketUpdate;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler {

	public static void sendPacket(Packet packet) {
		ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet);
	}

	private static void handleLogin(PacketLittleBlocksSettings settings, EntityPlayer entityplayer, World world) {
		if (settings.getCommand() == LBPacketIds.SETTINGS) {
			LBCore.littleBlocksClip = settings.getClipMode();
		}
	}

	public static void handleTileEntityPacket(PacketTileEntity packet,
			EntityPlayer entityplayer, World world) {
		TileEntity tileentity = world.getBlockTileEntity(packet.xPosition,
				packet.yPosition, packet.zPosition);
		if (packet instanceof PacketTileEntityLB) {
			PacketTileEntityLB packetLB = (PacketTileEntityLB) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				return;
			}
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
		packetLB.setSender(LBPacketIds.CLIENT);
		ModLoader.sendPacket(packetLB.getPacket());
	}
	
	public static void handlePacket(PacketUpdate packet,
			EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				CommonPacketHandler.handlePacket(packet, entityplayer, world);
			}
			if (packetLB.targetExists(world)) {
				TileEntity tileentity = packetLB.getTileEntity(world);
				if (tileentity != null
						&& tileentity instanceof TileEntityLittleBlocks) {
					TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
					if (packetLB.getCommand().equals("UPDATECLIENT")) {
						tileentitylb.setContent(
								packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ(),
								packetLB.getBlockID());
						tileentitylb.onInventoryChanged();
						world.markBlockAsNeedsUpdate(
								packetLB.xPosition,
								packetLB.yPosition,
								packetLB.zPosition);
					}
				}
			}
		}
	}

	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		EntityPlayer entityplayer = (EntityPlayer) player;
		World world = entityplayer.worldObj;
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(
				packet.data));
		try {
			int packetID = data.read();
			switch (packetID) {
			case PacketIds.LOGIN:
				PacketLittleBlocksSettings settings = new PacketLittleBlocksSettings();
				settings.readData(data);
				this.handleLogin(settings, entityplayer, world);
				break;
			case PacketIds.TILE:
				PacketTileEntityLB packetTileLB = new PacketTileEntityLB();
				packetTileLB.readData(data);
				this.handleTileEntityPacket(packetTileLB,
						entityplayer, world);
				break;
			case PacketIds.UPDATE:
				PacketLittleBlocks packetLB = new PacketLittleBlocks();
				packetLB.readData(data);
				this.handlePacket(packetLB, entityplayer, world);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
