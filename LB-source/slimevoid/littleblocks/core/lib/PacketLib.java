package slimevoid.littleblocks.core.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.client.network.packets.executors.ClientBlockAddedExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientBlockChangeExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientBlockEventExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientBreakBlockExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientCopierNotifyExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientLittleCollectionExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientLittleTileEntityUpdate;
import slimevoid.littleblocks.client.network.packets.executors.ClientMetadataUpdateExecutor;
import slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksLoginExecutor;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlockCollectionHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlockEventHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlockHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleBlocksHandler;
import slimevoid.littleblocks.network.handlers.PacketLittleNotifyHandler;
import slimevoid.littleblocks.network.handlers.PacketLoginHandler;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoid.littleblocks.network.packets.PacketLittleBlock;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlockActivatedExecutor;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlockClickedExecutor;
import slimevoid.littleblocks.network.packets.executors.PacketLittleBlocksLoginExecutor;
import slimevoidlib.network.PacketIds;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketLib {

	public final static int	PACKETID_EVENT	= PacketIds.PLAYER + 100;
	public final static int BLOCK_CLICK = 0;
	public final static int DIG_ONGOING = 1;
	public final static int DIG_BROKEN = 2;

	@SideOnly(Side.CLIENT)
	public static void registerClientPacketHandlers() {
		PacketLoginHandler clientLoginHandler = new PacketLoginHandler();
		clientLoginHandler.registerPacketHandler(	CommandLib.SETTINGS,
													new ClientPacketLittleBlocksLoginExecutor());

		ClientPacketHandler.registerPacketHandler(	PacketIds.LOGIN,
													clientLoginHandler);

		PacketLittleBlocksHandler clientLittleBlocksHandler = new PacketLittleBlocksHandler();
		clientLittleBlocksHandler.registerPacketHandler(CommandLib.UPDATE_CLIENT, new ClientBlockChangeExecutor());
/*		clientLittleBlocksHandler.registerPacketHandler(CommandLib.BLOCK_ADDED,
														new ClientBlockAddedExecutor());
		clientLittleBlocksHandler.registerPacketHandler(CommandLib.BREAK_BLOCK,
														new ClientBreakBlockExecutor());
		clientLittleBlocksHandler.registerPacketHandler(CommandLib.METADATA_MODIFIED,
														new ClientMetadataUpdateExecutor());
		clientLittleBlocksHandler.registerPacketHandler(CommandLib.TILE_ENTITY_UPDATE,
														new ClientLittleTileEntityUpdate());*/

		ClientPacketHandler.registerPacketHandler(	PacketIds.UPDATE,
													clientLittleBlocksHandler);

		PacketLittleBlockCollectionHandler clientCollectionHandler = new PacketLittleBlockCollectionHandler();
		clientCollectionHandler.registerPacketHandler(	CommandLib.ENTITY_COLLECTION,
														new ClientLittleCollectionExecutor());

		ClientPacketHandler.registerPacketHandler(	PacketIds.ENTITY,
													clientCollectionHandler);

		PacketLittleNotifyHandler clientLittleNotifyHandler = new PacketLittleNotifyHandler();
		clientLittleNotifyHandler.registerPacketHandler(CommandLib.COPIER_MESSAGE,
														new ClientCopierNotifyExecutor());

		ClientPacketHandler.registerPacketHandler(	PacketIds.PLAYER,
													clientLittleNotifyHandler);

		PacketLittleBlockEventHandler clientLittleBlockEventHandler = new PacketLittleBlockEventHandler();
		clientLittleBlockEventHandler.registerPacketHandler(CommandLib.BLOCK_EVENT,
															new ClientBlockEventExecutor());

		ClientPacketHandler.registerPacketHandler(	PacketLib.PACKETID_EVENT,
													clientLittleBlockEventHandler);
	}

	public static void registerPacketHandlers() {
		PacketLoginHandler loginHandler = new PacketLoginHandler();
		loginHandler.registerPacketHandler(	CommandLib.FETCH,
											new PacketLittleBlocksLoginExecutor());

		CommonPacketHandler.registerPacketHandler(	PacketIds.LOGIN,
													loginHandler);

		PacketLittleBlockHandler littleBlockHandler = new PacketLittleBlockHandler();
		littleBlockHandler.registerPacketHandler(	CommandLib.BLOCK_ACTIVATED,
													new PacketLittleBlockActivatedExecutor());
		littleBlockHandler.registerPacketHandler(	CommandLib.BLOCK_CLICKED,
													new PacketLittleBlockClickedExecutor());

		CommonPacketHandler.registerPacketHandler(	PacketIds.TILE,
													littleBlockHandler);
	}

	@SideOnly(Side.CLIENT)
	public static void blockActivated(World world, EntityPlayer entityplayer, int x, int y, int z, int q, float a, float b, float c) {
		PacketLittleBlock packetLB = new PacketLittleBlock(x, y, z, q, entityplayer.getCurrentEquippedItem(), a, b, c);
		PacketDispatcher.sendPacketToServer(packetLB.getPacket());
	}/*

	@SideOnly(Side.CLIENT)
	public static void blockClicked(World world, EntityPlayer entityplayer, int x, int y, int z, BlockLittleChunk block) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(CommandLib.BLOCK_CLICKED, x, y, z, 0, 0, 0, 0, block.xSelected, block.ySelected, block.zSelected, block.blockID, block.side);
		PacketDispatcher.sendPacketToServer(packetLB.getPacket());
	}

	public static void sendBreakBlock(ILittleWorld littleWorld, int blockX, int blockY, int blockZ, int side, int lastBlockId, int metadata) {

		PacketLittleBlocks packetLB = new PacketLittleBlocks(CommandLib.BREAK_BLOCK, blockX, blockY, blockZ, side, lastBlockId, metadata);
		PacketDispatcher.sendPacketToAllPlayers(packetLB.getPacket());
	}

	public static void sendBlockAdded(ILittleWorld littleWorld, int blockX, int blockY, int blockZ, int side, int blockId, int metadata) {

		PacketLittleBlocks packetLB = new PacketLittleBlocks(CommandLib.BLOCK_ADDED, blockX, blockY, blockZ, side, blockId, metadata);
		PacketDispatcher.sendPacketToAllPlayers(packetLB.getPacket());
	}

	public static void sendMetadata(ILittleWorld littleWorld, int blockX, int blockY, int blockZ, int blockId, int side, int metadata) {
		PacketLittleBlocks packetLB = new PacketLittleBlocks(CommandLib.METADATA_MODIFIED, blockX, blockY, blockZ, side, blockId, metadata);
		PacketDispatcher.sendPacketToAllPlayers(packetLB.getPacket());
	}

	public static void sendTileEntity(ILittleWorld littleWorld, TileEntity tileentity, int x, int y, int z) {
		PacketLittleBlocks packetTile = new PacketLittleBlocks(CommandLib.TILE_ENTITY_UPDATE, x, y, z, 0, littleWorld.getBlockId(	x,
																																	y,
																																	z), littleWorld.getBlockMetadata(	x,
																																										y,
																																										z));
		packetTile.setTileEntityData(tileentity);
		PacketDispatcher.sendPacketToAllPlayers(packetTile.getPacket());
	}*/

	public static void sendBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParameter) {
		PacketLittleBlocksEvents eventPacket = new PacketLittleBlocksEvents(x, y, z, blockID, eventID, eventParameter);
		PacketDispatcher.sendPacketToAllPlayers(eventPacket.getPacket());
	}

	public static void sendBlockClick(int x, int y, int z, int side) {
		PacketLittleBlock clickPacket = new PacketLittleBlock(x, y, z, side);
		PacketDispatcher.sendPacketToServer(clickPacket.getPacket());
	}

	public static void sendBlockPlace(World world, EntityPlayer entityplayer, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		PacketLittleBlock placePacket = new PacketLittleBlock(x, y, z, side, entityplayer.inventory.getCurrentItem(), hitX, hitY, hitZ);
		PacketDispatcher.sendPacketToServer(placePacket.getPacket());
	}

	public static void sendBlockChange(World world, EntityPlayer entityplayer, int x, int y, int z) {
		PacketLittleBlocks changePacket = new PacketLittleBlocks(x, y, z, world);
		PacketDispatcher.sendPacketToPlayer(changePacket.getPacket(), (Player) entityplayer);
	}
}
