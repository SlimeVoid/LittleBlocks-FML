package slimevoid.littleblocks.core.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.network.PacketIds;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.client.network.packets.executors.ClientBlockAddedExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientBreakBlockExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientLittleCollectionExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientLittleTileEntityUpdate;
import slimevoid.littleblocks.client.network.packets.executors.ClientMetadataUpdateExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksLoginExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientPacketTileEntityLBExecutor;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlockCollectionHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlocksHandler;
import slimevoid.littleblocks.network.handlers.PacketLoginHandler;
import slimevoid.littleblocks.network.handlers.PacketTileEntityHandler;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlocksActivatedExecutor;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlocksClickedExecutor;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlocksLoginExecutor;
import slimevoid.littleblocks.world.LittleWorld;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketLib {	
	@SideOnly(Side.CLIENT)
	public static void registerClientPacketHandlers() {
		PacketLoginHandler clientLoginHandler = new PacketLoginHandler();
		clientLoginHandler.registerPacketHandler(
				CommandLib.SETTINGS,
				new ClientPacketLittleBlocksLoginExecutor());
		
		ClientPacketHandler.registerPacketHandler(
				PacketIds.LOGIN,
				clientLoginHandler);
		
		PacketTileEntityHandler clientTileEntityHandler = new PacketTileEntityHandler();
		clientTileEntityHandler.registerPacketHandler(
				CommandLib.UPDATE_CLIENT,
				new ClientPacketTileEntityLBExecutor());
		
		ClientPacketHandler.registerPacketHandler(
				PacketIds.TILE,
				clientTileEntityHandler);
		
		PacketLittleBlocksHandler clientLittleBlocksHandler = new PacketLittleBlocksHandler();
		clientLittleBlocksHandler.registerPacketHandler(
				CommandLib.BLOCK_ADDED,
				new ClientBlockAddedExecutor());
		clientLittleBlocksHandler.registerPacketHandler(
				CommandLib.BREAK_BLOCK,
				new ClientBreakBlockExecutor());
		clientLittleBlocksHandler.registerPacketHandler(
				CommandLib.METADATA_MODIFIED,
				new ClientMetadataUpdateExecutor());
		clientLittleBlocksHandler.registerPacketHandler(
				CommandLib.TILE_ENTITY_UPDATE,
				new ClientLittleTileEntityUpdate());
		
		ClientPacketHandler.registerPacketHandler(PacketIds.UPDATE, clientLittleBlocksHandler);
		
		PacketLittleBlockCollectionHandler clientCollectionHandler = new PacketLittleBlockCollectionHandler();
		clientCollectionHandler.registerPacketHandler(CommandLib.ENTITY_COLLECTION, new ClientLittleCollectionExecutor());
		
		ClientPacketHandler.registerPacketHandler(
				PacketIds.ENTITY,
				clientCollectionHandler);
		
	}

	@SideOnly(Side.CLIENT)
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
		PacketDispatcher.sendPacketToServer(packetLB.getPacket());
	}
	
	public static void registerPacketHandlers() {
		PacketLoginHandler loginHandler = new PacketLoginHandler();
		loginHandler.registerPacketHandler(
				CommandLib.FETCH,
				new PacketLittleBlocksLoginExecutor());
		
		CommonPacketHandler.registerPacketHandler(
				PacketIds.LOGIN, 
				loginHandler);
		
		PacketLittleBlocksHandler littleBlocksHandler = new PacketLittleBlocksHandler();
		littleBlocksHandler.registerPacketHandler(
				CommandLib.BLOCK_ACTIVATED,
				new PacketLittleBlocksActivatedExecutor());
		littleBlocksHandler.registerPacketHandler(
				CommandLib.BLOCK_CLICKED,
				new PacketLittleBlocksClickedExecutor());
		
		CommonPacketHandler.registerPacketHandler(
				PacketIds.UPDATE,
				littleBlocksHandler);
	}

	public static void sendBreakBlock(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ, int side,
			int lastBlockId, int metadata, TileEntity tileData) {
		
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				CommandLib.BREAK_BLOCK,
				blockX, blockY, blockZ,
				side,
				lastBlockId,
				metadata);
		if (tileData != null) {
			packetLB.setTileEntityData(tileData);
		}
		PacketDispatcher.sendPacketToAllPlayers(
				packetLB.getPacket());
	}

	public static void sendBlockAdded(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ, int side,
			int blockId, int metadata, TileEntity tileentity) {
		
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				CommandLib.BLOCK_ADDED,
				blockX,
				blockY,
				blockZ,
				side,
				blockId,
				metadata);
		if (tileentity != null) {
			packetLB.setTileEntityData(tileentity);
		}
		PacketDispatcher.sendPacketToAllPlayers(
				packetLB.getPacket());
	}

	public static void sendMetadata(
			LittleWorld littleWorld,
			int blockX, int blockY, int blockZ,
			int blockId, int side, int metadata, TileEntity tileData) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				CommandLib.METADATA_MODIFIED,
				blockX,
				blockY,
				blockZ,
				side,
				blockId,
				metadata);
		if (tileData != null) {
			packetLB.setTileEntityData(tileData);
		}
		PacketDispatcher.sendPacketToAllPlayers(
				packetLB.getPacket());
	}
	
	public static void sendTileEntity(
			LittleWorld littleWorld,
			TileEntity tileentity,
			int x, int y, int z) {
		PacketLittleBlocks packetTile = new PacketLittleBlocks(
			CommandLib.TILE_ENTITY_UPDATE,
			x,
			y,
			z,
			0,
			littleWorld.getBlockId(x, y, z),
			littleWorld.getBlockMetadata(x, y, z));
		packetTile.setTileEntityData(tileentity);
		PacketDispatcher.sendPacketToAllPlayers(packetTile.getPacket());
	}
}
