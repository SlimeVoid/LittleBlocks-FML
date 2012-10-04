package littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.LBCore;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketLittleBlocksSettings;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import eurysmods.network.packets.core.PacketIds;
import eurysmods.network.packets.core.PacketTileEntity;
import eurysmods.network.packets.core.PacketUpdate;

public class ClientPacketHandler implements IPacketHandler {

	public static void sendPacket(Packet packet) {
		ModLoader.getMinecraftInstance().getSendQueue().addToSendQueue(packet);
	}

	private static void handleLogin(PacketLittleBlocksSettings settings, EntityPlayer entityplayer, World world) {
		if (settings.getCommand() == LBPacketIds.SETTINGS) {
			LBCore.littleBlocksClip = settings.getClipMode();
		}
	}

	public static void handleTileEntityPacket(PacketTileEntity packet, EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketTileEntityLB) {
			PacketTileEntityLB packetLB = (PacketTileEntityLB) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				return;
			}
			TileEntity tileentity = packet.getTileEntity(world);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				int[][][] content = new int[tileentitylb.size][tileentitylb.size][tileentitylb.size];
				int[][][] metadata = new int[tileentitylb.size][tileentitylb.size][tileentitylb.size];
				for (int xx = 0; xx < content.length; xx++) {
					for (int yy = 0; yy < content[xx].length; yy++) {
						for (int zz = 0; zz < content[xx][yy].length; zz++) {
							int blockId = packetLB.payload
									.getIntPayload(xx + (yy * tileentitylb.size) + (zz * tileentitylb.size * tileentitylb.size));
							int blockMeta = packetLB.payload
									.getIntPayload((tileentitylb.size * tileentitylb.size * tileentitylb.size) + xx + (yy * tileentitylb.size) + (zz * tileentitylb.size * tileentitylb.size));
							content[xx][yy][zz] = blockId;
							metadata[xx][yy][zz] = blockMeta;
						}
					}
				}
				tileentitylb.setContent(content);
				tileentitylb.setMetadata(metadata);
				// tileentitylb.onInventoryChanged();
				world.markBlockNeedsUpdate(
						packet.xPosition,
						packet.yPosition,
						packet.zPosition);
			}
		}
	}

	public void handleLittleData(PacketUpdate packetLB, TileEntity tileentitylittleblocks) {
	}

	public static void blockUpdate(World world, EntityPlayer entityplayer, int x, int y, int z, int q, float a, float b, float c, BlockLittleBlocks block, String command) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				command,
				x,
				y,
				z,
				q,
				a,
				b,
				c,
				block.xSelected,
				block.ySelected,
				block.zSelected,
				block.blockID,
				block.side);
		packetLB.setSender(LBPacketIds.CLIENT);
		ModLoader.sendPacket(packetLB.getPacket());
	}

	public static void handlePacket(PacketUpdate packet, EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			System.out.println("PacketLittleBlocks");
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				CommonPacketHandler.handlePacket(packet, entityplayer, world);
			}
			if (packetLB.getCommand().equals(LBCore.littleNotifyCommand)) {
			} else if (packetLB.targetExists(world)) {
				TileEntity tileentity = packetLB.getTileEntity(world);
				int xx = (packetLB.xPosition << 3) + packetLB.getSelectedX(), yy = (packetLB.yPosition << 3) + packetLB
						.getSelectedY(), zz = (packetLB.zPosition << 3) + packetLB
						.getSelectedZ();
				if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
					TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
					if (packetLB.getCommand().equals(LBCore.idModifiedCommand)) {
						tileentitylb.setContent(
								packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ(),
								packetLB.getBlockID());
						tileentitylb.setMetadata(
								packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ(),
								packetLB.getMetadata());
					}
					if (packetLB.getCommand().equals(
							LBCore.metaDataModifiedCommand)) {
						tileentitylb.setContent(
								packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ(),
								packetLB.getBlockID());
						tileentitylb.setMetadata(
								packetLB.getSelectedX(),
								packetLB.getSelectedY(),
								packetLB.getSelectedZ(),
								packetLB.getMetadata());
					}
					// tileentitylb.onInventoryChanged();
					world.markBlockNeedsUpdate(
							packetLB.xPosition,
							packetLB.yPosition,
							packetLB.zPosition);
				}
			}
		}
	}

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
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
				ClientPacketHandler.handleLogin(settings, entityplayer, world);
				break;
			case PacketIds.TILE:
				PacketTileEntityLB packetTileLB = new PacketTileEntityLB();
				packetTileLB.readData(data);
				ClientPacketHandler.handleTileEntityPacket(
						packetTileLB,
						entityplayer,
						world);
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
