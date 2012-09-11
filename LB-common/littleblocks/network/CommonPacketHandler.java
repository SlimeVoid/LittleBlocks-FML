package littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.LBCore;
import littleblocks.core.LittleWorld;
import littleblocks.network.packets.PacketLittleBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.network.packets.core.PacketIds;
import net.minecraft.src.EurysMods.network.packets.core.PacketUpdate;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CommonPacketHandler implements IPacketHandler {

	public static void metadataModified(LittleWorld littleWorld, int x, int y,
			int z, int side, float vecX, float vecY, float vecZ,
			int lastMetadata, int newMetadata) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.metaDataModifiedCommand, x, y, z, side, vecX, vecY,
				vecZ, littleWorld.getBlockId(x, y, z), newMetadata);
		sendToAll(packetLB);
	}

	public static void idModified(int x, int y, int z, int side, float vecX,
			float vecY, float vecZ, int lastId, int newId,
			LittleWorld littleWorld) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				LBCore.idModifiedCommand, x, y, z, side, vecX, vecY, vecZ,
				newId, littleWorld.getBlockMetadata(x, y, z));
		sendToAll(packetLB);
	}

	public static void handlePacket(PacketUpdate packet,
			EntityPlayer entityplayer, World world) {
		if (packet instanceof PacketLittleBlocks) {
			PacketLittleBlocks packetLB = (PacketLittleBlocks) packet;
			if (packetLB.getCommand().equals(LBCore.blockActivateCommand)) {
				if (world.getBlockId(packetLB.xPosition, packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = packetLB
							.getSelectedX();
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = packetLB
							.getSelectedY();
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = packetLB
							.getSelectedZ();
					((BlockLittleBlocks) LBCore.littleBlocks).side = packetLB
							.getMetadata();
					((BlockLittleBlocks) LBCore.littleBlocks).onServerBlockActivated(
							world, packet.xPosition, packet.yPosition,
							packet.zPosition, entityplayer, packet.side,
							packet.vecX, packet.vecY, packet.vecZ);
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).side = -1;
				}
			}
			if (packetLB.getCommand().equals(LBCore.blockClickCommand)) {
				if (world.getBlockId(packetLB.xPosition, packetLB.yPosition,
						packetLB.zPosition) == LBCore.littleBlocksID) {
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = packetLB
							.getSelectedX();
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = packetLB
							.getSelectedY();
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = packetLB
							.getSelectedZ();
					((BlockLittleBlocks) LBCore.littleBlocks).side = packetLB
							.getMetadata();
					entityplayer.addChatMessage("Clicking Block");
					((BlockLittleBlocks) LBCore.littleBlocks).onServerBlockClicked(
							world, packet.xPosition, packet.yPosition,
							packet.zPosition, entityplayer);
					((BlockLittleBlocks) LBCore.littleBlocks).xSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).ySelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).zSelected = -10;
					((BlockLittleBlocks) LBCore.littleBlocks).side = -1;
				}
			}
		}
	}

	public static void sendToAll(PacketUpdate packet) {
		sendToAllWorlds(null, packet.getPacket(), packet.xPosition,
				packet.yPosition, packet.zPosition, true);
	}

	public static void sendToAllWorlds(EntityPlayer entityplayer,
			Packet packet, int x, int y, int z, boolean sendToPlayer) {
		World[] worlds = DimensionManager.getWorlds();
		for (int i = 0; i < worlds.length; i++) {
			sendToAllPlayers(worlds[i], entityplayer, packet, x, y, z,
					sendToPlayer);
		}
	}

	public static void sendToAllPlayers(World world, EntityPlayer entityplayer,
			Packet packet, int x, int y, int z, boolean sendToPlayer) {
		for (int j = 0; j < world.playerEntities.size(); j++) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) world.playerEntities
					.get(j);
			boolean shouldSendToPlayer = true;
			if (entityplayer != null) {
				if (entityplayer.username.equals(entityplayermp.username)
						&& !sendToPlayer)
					shouldSendToPlayer = false;
			}
			if (shouldSendToPlayer) {
				if (Math.abs(entityplayermp.posX - x) <= 16
						&& Math.abs(entityplayermp.posY - y) <= 16
						&& Math.abs(entityplayermp.posZ - z) <= 16) {
					entityplayermp.addChatMessage("Sending to player");
					entityplayermp.serverForThisPlayer.sendPacketToPlayer(packet);
				}
			}
		}
	}

	public static void blockUpdate(World world, EntityPlayer entityplayer, int x,
			int y, int z, int q, float a, float b, float c,
			BlockLittleBlocks block, String blockActivateCommand) {
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
