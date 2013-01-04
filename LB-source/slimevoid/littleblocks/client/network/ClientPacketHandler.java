package slimevoid.littleblocks.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.network.LBPacketIds;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.network.packets.PacketTileEntityLB;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import slimevoid.littleblocks.world.LittleWorld;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
				handleLittleTilePacket(world, packetLB, tileentitylb);
				// handleBigTilePacket(world, packetLB, tileentitylb);
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
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				CommonPacketHandler.handlePacket(packet, entityplayer, world);
			}
			TileEntityLittleBlocks tileentitylittleblocks = (TileEntityLittleBlocks) world.getBlockTileEntity(
					packetLB.xPosition >> 3,
					packetLB.yPosition >> 3,
					packetLB.zPosition >> 3);
			if (tileentitylittleblocks != null) {
				if (packetLB.getCommand().equals(LBCore.blockAdded)) {
					tileentitylittleblocks.handleBlockAdded(world, entityplayer, packetLB);
				}
				if (packetLB.getCommand().equals(LBCore.breakBlock)) {
					tileentitylittleblocks.handleBreakBlock(world, entityplayer, packetLB);
				}
				if (packetLB.getCommand().equals(
						LBCore.metaDataModifiedCommand)) {
					tileentitylittleblocks.handleUpdateMetadata(world, entityplayer, packetLB);
				}
				world.markBlockForUpdate(
						packetLB.xPosition >> 3,
						packetLB.yPosition >> 3,
						packetLB.zPosition >> 3);
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
				ClientPacketHandler.handleLogin(settings, entityplayer, world);
				break;
			case PacketIds.TILE:
				PacketTileEntityLB packetTileLB = new PacketTileEntityLB();
				packetTileLB.readData(data);
				ClientPacketHandler.handleTileEntityPacket(packetTileLB, entityplayer, world);
				break;
			case PacketIds.UPDATE:
				PacketLittleBlocks packetLB = new PacketLittleBlocks();
				packetLB.readData(data);
				ClientPacketHandler.handlePacket(packetLB, entityplayer, world);
				break;
			case PacketIds.ENTITY:
				PacketLittleBlocksCollection collection = new PacketLittleBlocksCollection();
				collection.readData(data);
				ClientPacketHandler.handleEntity(collection, entityplayer, world);
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void handleEntity(PacketLittleBlocksCollection collection, EntityPlayer entityplayer, World world) {
		Entity entity = collection.getEntity(world);
		if (entity instanceof EntityItemLittleBlocksCollection) {
			((EntityItemLittleBlocksCollection)entity).itemstackCollection = collection.itemstackCollection;
		}
	}
}
