package littleblocks.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.items.EntityItemLittleBlocksCollection;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.LBPacketIds;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketLittleBlocksCollection;
import littleblocks.network.packets.PacketLittleBlocksSettings;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
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
			tileentitylb.setTiles(packetLB.getTileEntities());
			index += 5;
		}
		world.markBlockForRenderUpdate(
				packetLB.xPosition,
				packetLB.yPosition,
				packetLB.zPosition);
	}

	private static void handleBigTilePacket(World world, PacketTileEntityLB packetLB, TileEntityLittleBlocks tileentitylb) {
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
		world.markBlockForRenderUpdate(
				packetLB.xPosition,
				packetLB.yPosition,
				packetLB.zPosition);
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
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getSender() == LBPacketIds.CLIENT) {
				CommonPacketHandler.handlePacket(packet, entityplayer, world);
			}
			if (packetLB.getCommand().equals(LBCore.littleNotifyCommand)) {
			} else {
				if (packetLB.getCommand().equals(LBCore.idModifiedCommand)) {
					LittleWorld lw = ((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(world, false);
					lw.setBlockAndMetadata(
							packetLB.xPosition,
							packetLB.yPosition,
							packetLB.zPosition,
							packetLB.getBlockID(),
							packetLB.getMetadata());
				}
				if (packetLB.getCommand().equals(
						LBCore.metaDataModifiedCommand)) {
					LittleWorld lw = ((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(world, false);
					lw.setBlockAndMetadata(
							packetLB.xPosition,
							packetLB.yPosition,
							packetLB.zPosition,
							packetLB.getBlockID(),
							packetLB.getMetadata());
				}
				world.markBlockForRenderUpdate(
						packetLB.xPosition,
						packetLB.yPosition,
						packetLB.zPosition);
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
