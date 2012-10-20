package littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.blocks.BlockLittleBlocksBlock;
import littleblocks.core.LBCore;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketLittleBlocksSettings;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import eurysmods.network.packets.core.PacketIds;
import eurysmods.network.packets.core.PacketUpdate;

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
					tileentity.onInventoryChanged();
					// world.markBlockNeedsUpdate(tileentity.xCoord,
					// tileentity.yCoord+1, tileentity.zCoord);
				}
			}
		}
	}

	public static void metadataModified(LittleWorld littleWorld, BlockLittleBlocksBlock lbb) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.metaDataModifiedCommand,
				lbb);
		packetLB.setSender(LBPacketIds.SERVER);
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				lbb.getParentX(),
				lbb.getParentY(),
				lbb.getParentZ(),
				true);
	}

	public static void idModified(LittleWorld littleWorld, BlockLittleBlocksBlock lbb) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.idModifiedCommand,
				lbb);
		packetLB.setSender(LBPacketIds.SERVER);
		sendToAllPlayers(
				littleWorld.getRealWorld(),
				null,
				packetLB.getPacket(),
				lbb.getParentX(),
				lbb.getParentY(),
				lbb.getParentZ(),
				true);
	}

	public static void handlePacket(PacketUpdate packet, EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getCommand().equals(LBCore.blockActivateCommand)) {
				if (world.getBlockId(
						packetLB.xPosition,
						packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = packetLB
							.getSelectedX();
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = packetLB
							.getSelectedY();
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = packetLB
							.getSelectedZ();
					((BlockLittleBlocks) LBCore.littleBlocks).side = packetLB
							.getMetadata();
					((BlockLittleBlocks) LBCore.littleBlocks)
							.onServerBlockActivated(
									world,
									packet.xPosition,
									packet.yPosition,
									packet.zPosition,
									entityplayer,
									packet.side,
									packet.vecX,
									packet.vecY,
									packet.vecZ);
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).side = -1;
				}
			}
			if (packetLB.getCommand().equals(LBCore.blockClickCommand)) {
				if (world.getBlockId(
						packetLB.xPosition,
						packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = packetLB
							.getSelectedX();
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = packetLB
							.getSelectedY();
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = packetLB
							.getSelectedZ();
					((BlockLittleBlocks) LBCore.littleBlocks).side = packetLB
							.getMetadata();
					((BlockLittleBlocks) LBCore.littleBlocks)
							.onServerBlockClicked(
									world,
									packet.xPosition,
									packet.yPosition,
									packet.zPosition,
									entityplayer);
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).side = -1;
				}
			}
		}
	}

	public static void sendTo(EntityPlayer entityplayer, Packet packet) {
		if (entityplayer != null && entityplayer instanceof EntityPlayerMP) {
			((EntityPlayerMP) entityplayer).serverForThisPlayer
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
}
