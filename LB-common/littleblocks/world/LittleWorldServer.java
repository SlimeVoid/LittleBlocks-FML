package littleblocks.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import littleblocks.core.LBCore;
import littleblocks.network.CommonPacketHandler;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.BlockEventData;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.NextTickListEntry;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import net.minecraft.src.WorldSettings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

public class LittleWorldServer extends LittleWorld {

	/**
	 * TreeSet of scheduled ticks which is used as a priority queue for the
	 * ticks
	 */
	private TreeSet pendingTickListEntries;

	/** Set of scheduled ticks (used for checking if a tick already exists) */
	private Set scheduledTickSet;

	/**
	 * Double buffer of ServerBlockEventList[] for holding pending
	 * BlockEventData's
	 */
	private LittleBlockEventList[] blockEventCache = new LittleBlockEventList[] {
			new LittleBlockEventList((LittleBlockEvent) null),
			new LittleBlockEventList((LittleBlockEvent) null) };

	/**
	 * The index into the blockEventCache; either 0, or 1, toggled in
	 * sendBlockEventPackets where all BlockEvent are applied locally and send
	 * to clients.
	 */
	private int blockEventCacheIndex = 0;

	public LittleWorldServer(World world, WorldProvider worldprovider) {
		super(world, worldprovider);

		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet();
		}

		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet();
		}
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
		if (this.worldInfo.getWorldTime() != this.realWorld
				.getWorldInfo()
					.getWorldTime()) {
			this.worldInfo.setWorldTime(this.realWorld
					.getWorldInfo()
						.getWorldTime());
			this.scheduledTickSet.clear();
			this.pendingTickListEntries.clear();
		}
		this.sendAndApplyBlockEvents();
		this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
		this.tickUpdates(false);
		this.sendAndApplyBlockEvents();
	}

	@Override
	public boolean tickUpdates(boolean tick) {
		Set<TileEntity> littleBlockTiles = new HashSet();
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

					if (blockId == nextTick.blockID && blockId > 0) {
						Block littleBlock = Block.blocksList[blockId];
						if (LBCore.isBlockAllowedToTick(littleBlock)) {
							littleBlock.updateTick(
									this,
									nextTick.xCoord,
									nextTick.yCoord,
									nextTick.zCoord,
									this.rand);
						}
						TileEntity tileentity = this
								.getRealWorld()
									.getBlockTileEntity(
											nextTick.xCoord >> 3,
											nextTick.yCoord >> 3,
											nextTick.zCoord >> 3);
						if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
							if (!littleBlockTiles.contains(tileentity)) {
								littleBlockTiles.add(tileentity);
							}
						}
					}
				}
			}
			if (this.ticksInWorld >= MAX_TICKS_IN_WORLD) {
				for (TileEntity tile : littleBlockTiles) {
				}
			}
			return !this.pendingTickListEntries.isEmpty();
		}
	}

	/**
	 * Send and apply locally all pending BlockEvents to each player with 64m
	 * radius of the event.
	 */
	private void sendAndApplyBlockEvents() {
		Set<TileEntityLittleBlocks> tileentities = new HashSet();
		while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
			int index = this.blockEventCacheIndex;
			this.blockEventCacheIndex ^= 1;
			Iterator blockEvent = this.blockEventCache[index].iterator();

			while (blockEvent.hasNext()) {
				BlockEventData eventData = (BlockEventData) blockEvent.next();
				if (this.onBlockEventReceived(eventData)) {
				}
			}

			this.blockEventCache[index].clear();
		}
	}

	/**
	 * Called to apply a pending BlockEvent to apply to the current world.
	 */
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
					nextTick.setScheduledTime(tickRate + this.worldInfo
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
		NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);

		if (blockId > 0) {
			nextTick.setScheduledTime(tickRate + this.worldInfo.getWorldTime());
		}

		if (!this.scheduledTickSet.contains(nextTick)) {
			this.scheduledTickSet.add(nextTick);
			this.pendingTickListEntries.add(nextTick);
		}
	}

	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		CommonPacketHandler.metadataModified(
				this,
				x,
				y,
				z,
				side,
				littleX,
				littleY,
				littleZ,
				blockId,
				metadata);
	}

	@Override
	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		if (lastBlockId != 0) {
			Block.blocksList[lastBlockId].breakBlock(
					this,
					(x << 3) + littleX,
					(y << 3) + littleY,
					(z << 3) + littleZ,
					0,
					0);
		}
		if (blockId != 0) {
			Block.blocksList[blockId].onBlockAdded(
					this,
					(x << 3) + littleX,
					(y << 3) + littleY,
					(z << 3) + littleZ);
		}
		super.idModified(
				lastBlockId,
				x,
				y,
				z,
				side,
				littleX,
				littleY,
				littleZ,
				blockId,
				metadata);
		this.notifyBlockChange(
				(x << 3) + littleX,
				(y << 3) + littleY,
				(z << 3) + littleZ,
				blockId);
		if (lastBlockId != 0) {
			CommonPacketHandler.idModified(
					this,
					lastBlockId,
					x,
					y,
					z,
					side,
					littleX,
					littleY,
					littleZ,
					blockId,
					metadata);
		}
	}
}
