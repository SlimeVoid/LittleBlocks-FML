package slimevoid.littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import slimevoid.lib.network.PacketIds;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import slimevoid.littleblocks.world.LittleWorld;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CommonPacketHandler implements IPacketHandler {

	private static void handleLogin(PacketLittleBlocksSettings settings, EntityPlayer entityplayer, World world) {
		if (settings.getCommand() == LBPacketIds.FETCH) {
			PacketLittleBlocksSettings packet = new PacketLittleBlocksSettings();
			packet.setCommand(LBPacketIds.SETTINGS);
			packet.setClipMode(LBCore.littleBlocksClip);
			CommonPacketHandler.sendTo(entityplayer, packet.getPacket());
			List<TileEntity> tileEntities = world.loadedTileEntityList;
			for (TileEntity tileentity : tileEntities) {
				if (tileentity instanceof TileEntityLittleBlocks) {
					world.markBlockForUpdate(tileentity.xCoord, tileentity.yCoord+1, tileentity.zCoord);
				}
			}
		}
	}
/*
	public static void metadataModified(LittleWorld littleWorld, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.metaDataModifiedCommand,
					(x << 3) + littleX,
					(y << 3) + littleY,
					(z << 3) + littleZ,
					side,
					blockId,
					metadata);
		packetLB.setSender(LBPacketIds.SERVER);
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				x,
				y,
				z,
				true);
	}

	public static void idModified(LittleWorld littleWorld, int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata, TileEntity tileentity) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.idModifiedCommand,
				(x << 3) + littleX,
				(y << 3) + littleY,
				(z << 3) + littleZ,
					side,
					blockId,
					metadata);
		packetLB.setSender(LBPacketIds.SERVER);
		if (tileentity != null) {
			packetLB.setTileEntityData(tileentity);
		}
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				x,
				y,
				z,
				true);
	}*/

	public static void handlePacket(PacketUpdate packet, EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getCommand().equals(LBCore.blockActivateCommand)) {
				if (world.getBlockId(
						packetLB.xPosition,
						packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks)
							.onServerBlockActivated(
									world,
									packet.xPosition, packet.yPosition, packet.zPosition,
									entityplayer,
									packet.side, packet.vecX, packet.vecY, packet.vecZ,
									((PacketLittleBlocks) packet).getSelectedX(),
									((PacketLittleBlocks) packet).getSelectedY(),
									((PacketLittleBlocks) packet).getSelectedZ(),
									((PacketLittleBlocks) packet).getMetadata());
				}
			}
			if (packetLB.getCommand().equals(LBCore.blockClickCommand)) {
				if (world.getBlockId(
						packetLB.xPosition,
						packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks)
							.onServerBlockClicked(
									world,
									packet.xPosition,
									packet.yPosition,
									packet.zPosition,
									entityplayer,
									((PacketLittleBlocks) packet).getSelectedX(),
									((PacketLittleBlocks) packet).getSelectedY(),
									((PacketLittleBlocks) packet).getSelectedZ());
				}
			}
		}
	}

	public static void sendTo(EntityPlayer entityplayer, Packet packet) {
		if (entityplayer != null && entityplayer instanceof EntityPlayerMP) {
			((EntityPlayerMP) entityplayer).playerNetServerHandler
					.sendPacketToPlayer(packet);
		}
	}

	public static void sendToAll(PacketUpdate packet) {
		sendToAllWorlds(
				null,
				packet.getPacket(),
				packet.xPosition,
				packet.yPosition,
				packet.zPosition,
				true);
	}

	public static void sendToAllWorlds(EntityPlayer entityplayer, Packet packet, int x, int y, int z, boolean sendToPlayer) {
		World[] worlds = DimensionManager.getWorlds();
		for (int i = 0; i < worlds.length; i++) {
			sendToAllPlayers(
					worlds[i],
					entityplayer,
					packet,
					x,
					y,
					z,
					sendToPlayer);
		}
	}

	public static void sendToAllPlayers(World world, EntityPlayer entityplayer, Packet packet, int x, int y, int z, boolean sendToPlayer) {
		for (int j = 0; j < world.playerEntities.size(); j++) {
			EntityPlayer thePlayer = (EntityPlayer) world.playerEntities.get(j);
			if (thePlayer != null) {
				if (thePlayer instanceof EntityPlayerMP) {
					EntityPlayerMP entityplayermp = (EntityPlayerMP) thePlayer;
					boolean shouldSendToPlayer = true;
					if (entityplayer != null) {
						if (entityplayer.username
								.equals(entityplayermp.username) && !sendToPlayer)
							shouldSendToPlayer = false;
					}
					if (shouldSendToPlayer) {
						if (Math.abs(entityplayermp.posX - x) <= 16 && Math
								.abs(entityplayermp.posY - y) <= 16 && Math
								.abs(entityplayermp.posZ - z) <= 16) {
							sendTo(entityplayermp, packet);
						}
					}
				} else if (thePlayer instanceof EntityClientPlayerMP) {
					EntityClientPlayerMP entityplayermp = (EntityClientPlayerMP) thePlayer;
					boolean shouldSendToPlayer = true;
					if (entityplayer != null) {
						if (entityplayer.username
								.equals(entityplayermp.username) && !sendToPlayer)
							shouldSendToPlayer = false;
					}
					if (shouldSendToPlayer) {
						if (Math.abs(entityplayermp.posX - x) <= 16 && Math
								.abs(entityplayermp.posY - y) <= 16 && Math
								.abs(entityplayermp.posZ - z) <= 16) {
							entityplayermp.sendQueue.addToSendQueue(packet);
						}
					}
				}
			}
		}
	}

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
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
				CommonPacketHandler.handleLogin(settings, entityplayer, world);
				break;
			case PacketIds.UPDATE:
				PacketLittleBlocks packetLB = new PacketLittleBlocks();
				packetLB.readData(data);
				CommonPacketHandler.handlePacket(packetLB, entityplayer, world);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendBreakBlock(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ, int side,
			int lastBlockId, int metadata, TileEntity tileData) {
		
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.breakBlock,
				blockX, blockY, blockZ,
				side,
				lastBlockId,
				metadata);
		if (tileData != null) {
			packetLB.setTileEntityData(tileData);
		}
		packetLB.setSender(LBPacketIds.SERVER);
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				blockX >> 3,
				blockY >> 3,
				blockZ >> 3,
				true);
	}

	public static void sendBlockAdded(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ, int side,
			int blockId, int metadata, TileEntity tileentity) {
		
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.blockAdded,
				blockX,
				blockY,
				blockZ,
				side,
				blockId,
				metadata);
		packetLB.setSender(LBPacketIds.SERVER);
		if (tileentity != null) {
			packetLB.setTileEntityData(tileentity);
		}
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				blockX >> 3,
				blockY >> 3,
				blockZ >> 3,
				true);
	}

	public static void sendMetadata(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ,
			int blockId, int side, int metadata, TileEntity tileData) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.metaDataModifiedCommand,
				blockX,
				blockY,
				blockZ,
				side,
				blockId,
				metadata);
		if (tileData != null) {
			packetLB.setTileEntityData(tileData);
		}
		packetLB.setSender(LBPacketIds.SERVER);
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				blockX >> 3,
				blockY >> 3,
				blockZ >> 3,
				true);
	}
}
