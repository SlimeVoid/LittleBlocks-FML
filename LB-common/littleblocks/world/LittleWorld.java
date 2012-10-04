package littleblocks.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;

import littleblocks.blocks.BlockLittleBlocksBlock;
import littleblocks.core.LBCore;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.LBPacketIds;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Block;
import net.minecraft.src.BlockEventData;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.IntHashMap;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.Packet54PlayNoteBlock;
import net.minecraft.src.Profiler;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import net.minecraft.src.WorldSettings;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class LittleWorld extends World {

	/**
	 * TreeSet of scheduled ticks which is used as a priority queue for the
	 * ticks
	 */
	private TreeSet pendingTickListEntries;

	/** Set of scheduled ticks (used for checking if a tick already exists) */
	private Set scheduledTickSet;
	
	private int ticksInWorld = 0;

	private World realWorld;
	/**
	 * Double buffer of ServerBlockEventList[] for holding pending
	 * BlockEventData's
	 */
	private LittleBlockEventList[] blockEventCache = new LittleBlockEventList[] {
			new LittleBlockEventList((LittleBlockEvent) null),
			new LittleBlockEventList((LittleBlockEvent) null) };

	@SideOnly(Side.CLIENT)
	public LittleWorld(World world) {
		super(
				world.getSaveHandler(),
				"LittleBlocksWorld",
				world.provider,
				new WorldSettings(world.getWorldInfo().getSeed(), world
						.getWorldInfo()
							.getGameType(), world
						.getWorldInfo()
							.isMapFeaturesEnabled(), world
						.getWorldInfo()
							.isHardcoreModeEnabled(), world
						.getWorldInfo()
							.getTerrainType()), world.theProfiler);

		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet();
		}

		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}
		this.realWorld = world;
	}

	public LittleWorld(World world, WorldProvider worldprovider) {
		super(world.getSaveHandler(), "LittleBlocksWorld", new WorldSettings(
				world.getWorldInfo().getSeed(),
				world.getWorldInfo().getGameType(),
				world.getWorldInfo().isMapFeaturesEnabled(),
				world.getWorldInfo().isHardcoreModeEnabled(),
				world.getWorldInfo().getTerrainType()), worldprovider, null);

		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet();
		}

		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}
		this.realWorld = world;
	}

	@Override
	protected void initialize(WorldSettings worldSettings) {
		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet();
		}
		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}
		super.initialize(worldSettings);
	}

	@Override
	public void tick() {
		super.tick();
		this.ticksInWorld++;
		if (this.isRemote) {
			this.setWorldTime(this.getWorldTime() + 1L);
		}
		if (!this.isRemote) {
			if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
				this.sendAndApplyBlockEvents();
			}
			this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
			this.tickUpdates(false);
			if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
				this.sendAndApplyBlockEvents();
			}
		}
		if (this.ticksInWorld >= 40) {
			this.ticksInWorld = 0;
		}
	}

	/**
	 * The index into the blockEventCache; either 0, or 1, toggled in
	 * sendBlockEventPackets where all BlockEvent are applied locally and send
	 * to clients.
	 */
	private int blockEventCacheIndex = 0;

	/**
	 * Send and apply locally all pending BlockEvents to each player with 64m
	 * radius of the event.
	 */
	@SideOnly(Side.SERVER)
	private void sendAndApplyBlockEvents() {
		Set<TileEntityLittleBlocks> tileentities = new HashSet();
		while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
			int index = this.blockEventCacheIndex;
			this.blockEventCacheIndex ^= 1;
			Iterator blockEvent = this.blockEventCache[index].iterator();

			while (blockEvent.hasNext()) {
				BlockEventData eventData = (BlockEventData) blockEvent.next();
				if (this.onBlockEventReceived(eventData)) {
					TileEntity tileentity = this.realWorld.getBlockTileEntity(
							eventData.getX() >> 3,
							eventData.getY() >> 3,
							eventData.getZ() >> 3);
					if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
						if (!tileentities.contains((TileEntityLittleBlocks)tileentity)) {
							tileentities.add((TileEntityLittleBlocks)tileentity);
						}
					}
				}
			}

			this.blockEventCache[index].clear();
		}
		for (TileEntityLittleBlocks tileentitylb : tileentities) {
			tileentitylb.onInventoryChanged();
		}
	}

	/**
	 * Called to apply a pending BlockEvent to apply to the current world.
	 */
	@SideOnly(Side.SERVER)
	private boolean onBlockEventReceived(BlockEventData blockEventData) {
		int blockId = this.getBlockId(
				blockEventData.getX(),
				blockEventData.getY(),
				blockEventData.getZ());

		if (blockId == blockEventData.getBlockID()) {
			Block.blocksList[blockId].onBlockEventReceived(
					this,
					blockEventData.getX(),
					blockEventData.getY(),
					blockEventData.getZ(),
					blockEventData.getEventID(),
					blockEventData.getEventParameter());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a block event with the given Args to the blockEventCache. During the
	 * next tick(), the block specified will have its onBlockEvent handler
	 * called with the given parameters. Args: X,Y,Z, BlockID, EventID,
	 * EventParameter
	 */
	public void addBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParam) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			super.addBlockEvent(x, y, z, blockID, eventID, eventParam);
			return;
		}
		BlockEventData eventData = new BlockEventData(
				x,
				y,
				z,
				blockID,
				eventID,
				eventParam);
		Iterator nextEvent = this.blockEventCache[this.blockEventCacheIndex]
				.iterator();
		BlockEventData newBlockEvent;

		do {
			if (!nextEvent.hasNext()) {
				this.blockEventCache[this.blockEventCacheIndex].add(eventData);
				return;
			}

			newBlockEvent = (BlockEventData) nextEvent.next();
		} while (!newBlockEvent.equals(eventData));
	}

	/**
	 * Runs through the list of updates to run and ticks them
	 */
	@Override
	public boolean tickUpdates(boolean tick) {
		if (this.isRemote)
			return false;
		Set<TileEntityLittleBlocks> littleBlockTiles = new HashSet();
		int numberOfUpdates = this.pendingTickListEntries.size();

		if (numberOfUpdates != this.scheduledTickSet.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (numberOfUpdates > 1000) {
				numberOfUpdates = 1000;
			}

			for (int update = 0; update < numberOfUpdates; ++update) {
				NextTickListEntry nextTick = (NextTickListEntry) this.pendingTickListEntries
						.first();

				if (!tick && nextTick.scheduledTime > this.worldInfo
						.getWorldTime()) {
					break;
				}

				this.pendingTickListEntries.remove(nextTick);
				this.scheduledTickSet.remove(nextTick);
				byte max = 8;
				if (this.checkChunksExist(
						nextTick.xCoord - max,
						nextTick.yCoord - max,
						nextTick.zCoord - max,
						nextTick.xCoord + max,
						nextTick.yCoord + max,
						nextTick.zCoord + max)) {
					int blockId = this.getBlockId(
							nextTick.xCoord,
							nextTick.yCoord,
							nextTick.zCoord);

					if (blockId == nextTick.blockID && blockId > 0 /*&& blockId != Block.waterMoving.blockID && blockId != Block.waterStill.blockID && blockId != Block.lavaMoving.blockID && blockId != Block.lavaStill.blockID*/) {
						Block.blocksList[blockId].updateTick(
								this,
								nextTick.xCoord,
								nextTick.yCoord,
								nextTick.zCoord,
								this.rand);
						TileEntity tileentity = this.realWorld
								.getBlockTileEntity(
										nextTick.xCoord >> 3,
										nextTick.yCoord >> 3,
										nextTick.zCoord >> 3);
						if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
							if (!littleBlockTiles
									.contains((TileEntityLittleBlocks) tileentity)) {
								littleBlockTiles
										.add((TileEntityLittleBlocks) tileentity);
							}
						}
					}
				}
			}
			if (this.ticksInWorld >= 40) {
				for (TileEntityLittleBlocks tile : littleBlockTiles) {
					this.realWorld.markBlockNeedsUpdate(
							tile.xCoord,
							tile.yCoord,
							tile.zCoord);
				}
			}
			return !this.pendingTickListEntries.isEmpty();
		}
	}

	@Override
	public List getPendingBlockUpdates(Chunk chunk, boolean forceRemove) {
		ArrayList pendingUpdates = null;
		ChunkCoordIntPair chunkPair = chunk.getChunkCoordIntPair();
		int x = chunkPair.chunkXPos << 4;
		int maxX = x + 16;
		int z = chunkPair.chunkZPos << 4;
		int maxZ = z + 16;
		Iterator pendingTicks = this.pendingTickListEntries.iterator();

		while (pendingTicks.hasNext()) {
			NextTickListEntry nextTick = (NextTickListEntry) pendingTicks
					.next();

			if (nextTick.xCoord >= x && nextTick.xCoord < maxX && nextTick.zCoord >= z && nextTick.zCoord < maxZ) {
				if (forceRemove) {
					this.scheduledTickSet.remove(nextTick);
					pendingTicks.remove();
				}

				if (pendingUpdates == null) {
					pendingUpdates = new ArrayList();
				}

				pendingUpdates.add(nextTick);
			}
		}

		return pendingUpdates;
	}

	/**
	 * Schedules a tick to a block with a delay (Most commonly the tick rate)
	 */
	@Override
	public void scheduleBlockUpdate(int x, int y, int z, int blockId, int tickRate) {
		if (this.isRemote)
			return;
		NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);
		byte max = 8;

		if (this.scheduledUpdatesAreImmediate) {
			if (this.checkChunksExist(
					nextTick.xCoord - max,
					nextTick.yCoord - max,
					nextTick.zCoord - max,
					nextTick.xCoord + max,
					nextTick.yCoord + max,
					nextTick.zCoord + max)) {
				int var8 = this.getBlockId(
						nextTick.xCoord,
						nextTick.yCoord,
						nextTick.zCoord);

				if (var8 == nextTick.blockID && var8 > 0) {
					Block.blocksList[var8].updateTick(
							this,
							nextTick.xCoord,
							nextTick.yCoord,
							nextTick.zCoord,
							this.rand);
				}
			}
		} else {
			if (this.checkChunksExist(
					x - max,
					y - max,
					z - max,
					x + max,
					y + max,
					z + max)) {
				if (blockId > 0) {
					nextTick.setScheduledTime((long) tickRate + this.worldInfo
							.getWorldTime());
				}

				if (!this.scheduledTickSet.contains(nextTick)) {
					this.scheduledTickSet.add(nextTick);
					this.pendingTickListEntries.add(nextTick);
				}
			}
		}
	}

	/**
	 * Schedules a block update from the saved information in a chunk. Called
	 * when the chunk is loaded.
	 */
	@Override
	public void scheduleBlockUpdateFromLoad(int x, int y, int z, int blockId, int tickRate) {
		if (this.isRemote)
			return;
		NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);

		if (blockId > 0) {
			nextTick.setScheduledTime((long) tickRate + this.worldInfo
					.getWorldTime());
		}

		if (!this.scheduledTickSet.contains(nextTick)) {
			this.scheduledTickSet.add(nextTick);
			this.pendingTickListEntries.add(nextTick);
		}
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int i, int j, int k) {
		return super.getSkyBlockTypeBrightness(enumskyblock, i >> 3, j >> 3, k >> 3);
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
		return realWorld.getLightBrightnessForSkyBlocks(
				i >> 3,
				j >> 3,
				k >> 3,
				l);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getBrightness(int i, int j, int k, int l) {
		if (realWorld != null) {
			return realWorld.getBrightness(i >> 3, j >> 3, k >> 3, l);
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBlockLightValue(int i, int j, int k) {
		if (realWorld != null) {
			return realWorld.getBlockLightValue(i >> 3, j >> 3, k >> 3);
		}
		return 0;
	}

	@Override
	public void setSpawnLocation() {
	}

	public boolean isOutdated(World world) {
		boolean outdated = !realWorld.equals(world);
		return outdated;
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return 0;
		}
		if (y < 0) {
			return 0;
		}
		if (y >= 128 << 3) {
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getContent(x & 7, y & 7, z & 7);
			} else {
				return id;
			}
		}
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		entity.setPosition(entity.posX / 8, entity.posY / 8, entity.posZ / 8);
		entity.motionX /= 8;
		entity.motionY /= 8;
		entity.motionZ /= 8;
		entity.worldObj = realWorld;
		return realWorld.spawnEntityInWorld(entity);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return 0;
		}
		if (y < 0) {
			return 0;
		}
		if (y >= 128 << 3) {
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			int metadata = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockMetadata(
								(x & 0x7f) >> 3,
								y >> 3,
								(z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getMetadata(x & 7, y & 7, z & 7);
			} else {
				return metadata;
			}
		}
	}

	@Override
	public int getHeight() {
		return super.getHeight() * LBCore.littleBlocksSize;
	}

	@Override
	public boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
			if (currentId != id || currentData != metadata) {
				tile.setContent(x & 7, y & 7, z & 7, id, metadata);
				flag = true;
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return flag;
		}
	}

	/**
	 * Sets the block ID and metadata of a block, optionally marking it as
	 * needing update. Args: X,Y,Z, blockID, metadata, needsUpdate
	 */
	public boolean setBlockAndMetadataWithUpdate(int x, int y, int z, int blockId, int metadata, boolean needsUpdate) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			boolean flag = false;
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntity tileentity = realWorld.getBlockTileEntity(
					x >> 3,
					y >> 3,
					z >> 3);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				int currentId = tileentitylb.getContent(x & 7, y & 7, z & 7);
				int currentData = tileentitylb.getMetadata(x & 7, y & 7, z & 7);
				if (currentId != blockId || currentData != metadata) {
					tileentitylb.setContent(
							x & 7,
							y & 7,
							z & 7,
							blockId,
							metadata);
					flag = true;
				}
				realWorld.markBlockNeedsUpdate(x >> 3, y >> 3, z >> 3);
			}
			return flag;
		}
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			if (currentId != id) {
				tile.setContent(x & 7, y & 7, z & 7, id);
				flag = true;
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return flag;
		}
	}

	@Override
	public boolean setBlockMetadata(int x, int y, int z, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return false;
		}
		if (y < 0) {
			return false;
		}
		if (y >= 128 << 3) {
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
				if (currentData != metadata) {
					tile.setMetadata(x & 7, y & 7, z & 7, metadata);
				}
			} else {
				chunk.setBlockMetadata(
						(x & 0x7f) >> 3,
						y >> 3,
						(z & 0x7f) >> 3,
						metadata);
			}
			realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			return true;
		}
	}

	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		int xDiff = (maxX - minX) >> 1,
			yDiff = (maxY - minY) >> 1,
			zDiff = (maxZ - minZ) >> 1;
		int xMid = (minX + maxX) >> 1,
			yMid = (minY + maxY) >> 1,
			zMid = (minZ + maxZ) >> 1;

		boolean flag = realWorld.checkChunksExist(
				(xMid >> 3) - xDiff,
				(yMid >> 3) - yDiff,
				(zMid >> 3) - zDiff,
				(xMid >> 3) + xDiff,
				(yMid >> 3) + yDiff,
				(zMid >> 3) + zDiff);

		return flag;
	}

	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int side) {
		// if (!this.isRemote) {
		notifyBlockOfNeighborChange(x - 1, y, z, side);
		notifyBlockOfNeighborChange(x + 1, y, z, side);
		notifyBlockOfNeighborChange(x, y - 1, z, side);
		notifyBlockOfNeighborChange(x, y + 1, z, side);
		notifyBlockOfNeighborChange(x, y, z - 1, side);
		notifyBlockOfNeighborChange(x, y, z + 1, side);
		// }
	}

	private void notifyBlockOfNeighborChange(int x, int y, int z, int side) {
		World world;
		int blockId = realWorld.getBlockId(x >> 3, y >> 3, z >> 3);
		if (blockId == LBCore.littleBlocksID) {
			world = this;
		} else {
			x >>= 3;
			y >>= 3;
			z >>= 3;
			world = realWorld;
		}
		if ((!realWorld.editingBlocks && !realWorld.isRemote && !this.editingBlocks && !this.isRemote)) {
			Block block = Block.blocksList[world.getBlockId(x, y, z)];
			if (block != null) {
				block.onNeighborBlockChange(world, x, y, z, side);

				if (world == this) {
					TileEntity tileentity = realWorld.getBlockTileEntity(x >> 3, z >> 3, z >> 3);
					if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
						TileEntityLittleBlocks tile = (TileEntityLittleBlocks)tileentity;
						int littleBlockId = tile.getContent(x & 7, y & 7, z & 7);
						int newmetadata = tile.getMetadata(x & 7, y & 7, z & 7);
						BlockLittleBlocksBlock lbb = new BlockLittleBlocksBlock(littleBlockId, x & 7, y & 7, z & 7, newmetadata);
						this.metadataModified(lbb, newmetadata);
					}
					realWorld.markBlockNeedsUpdate(x, y, z);
				}
			}
		}
	}

	public void idModified(BlockLittleBlocksBlock lbb, int lastId) {
		if (lastId != 0) {
			if (!this.isRemote) {
				Block.blocksList[lastId].breakBlock(
						this,
						lbb.getWorldX(),
						lbb.getWorldY(),
						lbb.getWorldZ(),
						0,
						0);
			}
		}
		realWorld.updateAllLightTypes(
				lbb.getWorldX(),
				lbb.getWorldY(),
				lbb.getWorldZ());
		if (lbb.getBlockId() != 0) {
			if (!this.isRemote) {
				Block.blocksList[lbb.getBlockId()].onBlockAdded(
						this,
						lbb.getWorldX(),
						lbb.getWorldY(),
						lbb.getWorldZ());
			}
		}
		if (!this.isRemote) {
			CommonPacketHandler.idModified(this, lbb);
		}
		this.notifyBlockChange(
				lbb.getWorldX(),
				lbb.getWorldY(),
				lbb.getWorldZ(),
				lbb.getBlockId());
	}

/*	public void idModified(int x, int y, int z, int side, float vecX, float vecY, float vecZ, int lastId, int newId) {
		if (lastId != 0) {
			Block.blocksList[lastId].breakBlock(this, x, y, z, 0, 0);
		}
		realWorld.updateAllLightTypes(x, y, z);
		if (newId != 0) {
			if (!this.isRemote) {
				Block.blocksList[newId].onBlockAdded(this, x, y, z);
			}
		}
		if (!this.isRemote) {
			CommonPacketHandler.idModified(
					x,
					y,
					z,
					side,
					vecX,
					vecY,
					vecZ,
					lastId,
					newId,
					this);
		}

		notifyBlockChange(x, y, z, newId);
	}*/

	public void metadataModified(BlockLittleBlocksBlock lbb, int metadata) {
		if (!this.isRemote) {
			CommonPacketHandler.metadataModified(
					this,
					lbb,
					metadata
			);
		}
	}

	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return null;
		}
		if (y < 0) {
			return null;
		}
		if (y >= 128 << 3) {
			return null;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getTileEntity(x & 7, y & 7, z & 7);
			} else {
				return realWorld.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			}
		}
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileEntity) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= 128 << 3) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.setTileEntity(x & 7, y & 7, z & 7, tileEntity);
			} else {
				realWorld
						.setBlockTileEntity(x >> 3, y >> 3, z >> 3, tileEntity);
			}
			return;
		}
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= 128 << 3) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.removeTileEntity(x & 7, y & 7, z & 7);
			}
		}
	}

	@Override
	public void markBlockNeedsUpdate(int i, int j, int k) {
		realWorld.markBlockNeedsUpdate(i >> 3, j >> 3, k >> 3);
	}

	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		realWorld.playSoundEffect(x / 8, y / 8, z / 8, s, f, f1);
	}

	@Override
	public void playRecord(String s, int x, int y, int z) {
		realWorld.playRecord(s, x, y, z);
	}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		realWorld.playAuxSFX(x / 8, y / 8, z / 8, l, i1);
	}

	@Override
	public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
		realWorld.spawnParticle(s, x / 8, y / 8, z / 8, d3, d4, d5);
	}

	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		Vec3.xCoord *= 8;
		Vec3.yCoord *= 8;
		Vec3.zCoord *= 8;

		Vec31.xCoord *= 8;
		Vec31.yCoord *= 8;
		Vec31.zCoord *= 8;
		return super.rayTraceBlocks_do_do(Vec3, Vec31, flag, flag1);
	}

	@Override
	protected IChunkProvider createChunkProvider() {
		return new LittleChunkProvider(this);
	}

	public World getRealWorld() {
		return this.realWorld;
	}
}