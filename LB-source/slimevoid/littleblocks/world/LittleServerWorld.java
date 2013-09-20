package slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.world.events.LittleBlockEvent;
import slimevoid.littleblocks.world.events.LittleBlockEventList;
import slimevoidlib.data.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Explosion;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;

public class LittleServerWorld extends LittleWorld {

	/**
	 * TreeSet of scheduled ticks which is used as a priority queue for the
	 * ticks
	 */
	private TreeSet<NextTickListEntry>		pendingTickListEntries;

	/** Set of scheduled ticks (used for checking if a tick already exists) */
	private Set<NextTickListEntry>			scheduledTickSet;

	private ArrayList<NextTickListEntry>	tickedEntries			= new ArrayList<NextTickListEntry>();

	/**
	 * Double buffer of ServerBlockEventList[] for holding pending
	 * BlockEventData's
	 */
	private LittleBlockEventList[]			blockEventCache			= new LittleBlockEventList[] {
			new LittleBlockEventList((LittleBlockEvent) null),
			new LittleBlockEventList((LittleBlockEvent) null)		};

	/**
	 * The index into the blockEventCache; either 0, or 1, toggled in
	 * sendBlockEventPackets where all BlockEvent are applied locally and send
	 * to clients.
	 */
	private int								blockEventCacheIndex	= 0;

	public LittleServerWorld(World world, WorldProvider worldprovider) {
		super(world, worldprovider);
		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet<NextTickListEntry>();
		}

		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet<NextTickListEntry>();
		}
	}
	
	@Override
	protected void initialize(WorldSettings worldSettings) {
		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet<NextTickListEntry>();
		}
		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet<NextTickListEntry>();
		}
	}

	public void littleTick() {
	}
	
	@Override
	public void tick() {
		this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);
		this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
		this.tickUpdates(false);
		//this.littleTickUpdates(false);
		this.tickBlocksAndAmbiance();
		this.sendAndApplyBlockEvents();
	}
	
	public boolean littleTickUpdates(boolean tick) {
		return tick;
	}

	@Override
	public boolean tickUpdates(boolean tick) {
		int numberOfUpdates = this.pendingTickListEntries.size();

		if (numberOfUpdates != this.scheduledTickSet.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if (numberOfUpdates > 1000) {
				numberOfUpdates = 1000;
			}
			NextTickListEntry nextTick;
			for (int update = 0; update < numberOfUpdates; ++update) {
				nextTick = (NextTickListEntry) this.pendingTickListEntries.first();

				if (!tick
					&& nextTick.scheduledTime > this.getRealWorld().getWorldInfo().getWorldTotalTime()) {
					break;
				}

				this.pendingTickListEntries.remove(nextTick);
				this.scheduledTickSet.remove(nextTick);
				this.tickedEntries.add(nextTick);
			}
			Iterator tickedEntryList = this.tickedEntries.iterator();

			while (tickedEntryList.hasNext()) {
				nextTick = (NextTickListEntry) tickedEntryList.next();
				tickedEntryList.remove();
				byte max = 0;
				if (this.checkChunksExist(	nextTick.xCoord - max,
											nextTick.yCoord - max,
											nextTick.zCoord - max,
											nextTick.xCoord + max,
											nextTick.yCoord + max,
											nextTick.zCoord + max)) {
					// System.out.println("Existing Chunk");
					int blockId = this.getBlockId(	nextTick.xCoord,
													nextTick.yCoord,
													nextTick.zCoord);

					if (blockId > 0
						&& Block.isAssociatedBlockID(	blockId,
														nextTick.blockID)) {
						// System.out.println("Associated Ticking Block");
						try {
							Block littleBlock = Block.blocksList[blockId];
							if (BlockUtil.isBlockAllowedToTick(littleBlock)) {
								// System.out.println("Allowed to Tick");
								littleBlock.updateTick(	this,
														nextTick.xCoord,
														nextTick.yCoord,
														nextTick.zCoord,
														this.rand);
							} else {
								LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.isRemote,
																															"BlockUpdateTick Prohibited["
																																	+ Block.blocksList[littleBlock.blockID].getLocalizedName()
																																	+ "].("
																																	+ nextTick.xCoord
																																	+ ", "
																																	+ nextTick.yCoord
																																	+ ", "
																																	+ nextTick.zCoord
																																	+ ")",
																															LoggerLittleBlocks.LogLevel.DEBUG);
							}
						} catch (Throwable thrown) {
							LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.isRemote,
																														"BlockUpdateTick FAILED["
																																+ Block.blocksList[blockId].getLocalizedName()
																																+ "].("
																																+ nextTick.xCoord
																																+ ", "
																																+ nextTick.yCoord
																																+ ", "
																																+ nextTick.zCoord
																																+ ")",
																														LoggerLittleBlocks.LogLevel.DEBUG);
							CrashReport crashReport = CrashReport.makeCrashReport(	thrown,
																					"Exception while ticking a block");
							CrashReportCategory var9 = crashReport.makeCategory("Block being ticked");
							int metadata;

							try {
								metadata = this.getBlockMetadata(	nextTick.xCoord,
																	nextTick.yCoord,
																	nextTick.zCoord);
							} catch (Throwable thrown2) {
								metadata = -1;
							}

							CrashReportCategory.addBlockCrashInfo(	var9,
																	nextTick.xCoord,
																	nextTick.yCoord,
																	nextTick.zCoord,
																	blockId,
																	metadata);
							throw new ReportedException(crashReport);
						}
						/*
						 * TileEntity tileentity = this .getRealWorld()
						 * .getBlockTileEntity( nextTick.xCoord >> 3,
						 * nextTick.yCoord >> 3, nextTick.zCoord >> 3); if
						 * (tileentity != null && tileentity instanceof
						 * TileEntityLittleBlocks) { if
						 * (!littleBlockTiles.contains(tileentity)) {
						 * littleBlockTiles.add(tileentity); } }
						 */
					}
				} else {
					// System.out.println("Scheduling Update....");
					this.scheduleBlockUpdate(	nextTick.xCoord,
												nextTick.yCoord,
												nextTick.zCoord,
												nextTick.blockID,
												0);
				}
			}
			/*
			 * if (this.ticksInWorld >= MAX_TICKS_IN_WORLD) { for (TileEntity
			 * tile : littleBlockTiles) { } }
			 */
			this.tickedEntries.clear();
			return !this.pendingTickListEntries.isEmpty();
		}
	}
	
	@Override
	protected void tickBlocksAndAmbiance() {
		
	}
	
	public void updateLittleEntities() {
	}

	@Override
	public void updateEntities() {
		super.updateEntities();
	}

	/**
	 * Returns true if the given block will receive a scheduled tick in the
	 * future. Args: X, Y, Z, blockID
	 */
	@Override
	public boolean isBlockTickScheduledThisTick(int x, int y, int z, int blockId) {
		// System.out.println("isBlockTickScheduled");
		NextTickListEntry nextticklistentry = new NextTickListEntry(x, y, z, blockId);
		return this.tickedEntries.contains(nextticklistentry);
	}

	/**
	 * returns a new explosion. Does initiation (at time of writing Explosion is
	 * not finished)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
		Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8);
		explosion.isFlaming = isFlaming;
		explosion.isSmoking = isSmoking;
		explosion.doExplosionA();
		explosion.doExplosionB(false);

		if (!isSmoking) {
			explosion.affectedBlockPositions.clear();
		}

		double xCoord = (int) x >> 3;
		double yCoord = (int) y >> 3;
		double zCoord = (int) z >> 3;

		Iterator players = this.getRealWorld().playerEntities.iterator();

		while (players.hasNext()) {
			EntityPlayer player = (EntityPlayer) players.next();

			if (player.getDistanceSq(	xCoord,
										yCoord,
										zCoord) < 4096.0D) {
				((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(xCoord, yCoord, zCoord, strength / 8, explosion.affectedBlockPositions, (Vec3) explosion.func_77277_b().get(player)));
			}
		}

		return explosion;
	}

	/**
	 * Send and apply locally all pending BlockEvents to each player with 64m
	 * radius of the event.
	 */
	private void sendAndApplyBlockEvents() {
		// Set<TileEntityLittleBlocks> tileentities = new HashSet();
		while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
			int index = this.blockEventCacheIndex;
			this.blockEventCacheIndex ^= 1;
			Iterator blockEvent = this.blockEventCache[index].iterator();

			while (blockEvent.hasNext()) {
				BlockEventData eventData = (BlockEventData) blockEvent.next();
				if (this.onBlockEventReceived(eventData)) {
					PacketLib.sendBlockEvent(	eventData.getX(),
												eventData.getY(),
												eventData.getZ(),
												eventData.getBlockID(),
												eventData.getEventID(),
												eventData.getEventParameter());
				} else {
					LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.isRemote,
																												"onBlockEvenReceived("
																														+ eventData.getBlockID()
																														+ ").[Event: "
																														+ eventData.getEventID()
																														+ "("
																														+ eventData.getX()
																														+ ", "
																														+ eventData.getY()
																														+ ", "
																														+ eventData.getZ()
																														+ "), "
																														+ eventData.getEventParameter(),
																												LoggerLittleBlocks.LogLevel.DEBUG);
				}
			}

			this.blockEventCache[index].clear();
		}
	}

	/**
	 * Called to apply a pending BlockEvent to apply to the current world.
	 */
	private boolean onBlockEventReceived(BlockEventData blockEventData) {
		int blockId = this.getBlockId(	blockEventData.getX(),
										blockEventData.getY(),
										blockEventData.getZ());

		if (blockId == blockEventData.getBlockID()) {
			return Block.blocksList[blockId].onBlockEventReceived(	this,
																	blockEventData.getX(),
																	blockEventData.getY(),
																	blockEventData.getZ(),
																	blockEventData.getEventID(),
																	blockEventData.getEventParameter());
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.isRemote,
																										"FAILED:onBlockEvenReceived("
																												+ blockEventData.getBlockID()
																												+ ").[Event: "
																												+ blockEventData.getEventID()
																												+ "("
																												+ blockEventData.getX()
																												+ ", "
																												+ blockEventData.getY()
																												+ ", "
																												+ blockEventData.getZ()
																												+ "), "
																												+ blockEventData.getEventParameter(),
																										LoggerLittleBlocks.LogLevel.DEBUG);
			return false;
		}
	}

	/**
	 * Adds a block event with the given Args to the blockEventCache. During the
	 * next tick(), the block specified will have its onBlockEvent handler
	 * called with the given parameters. Args: X,Y,Z, BlockID, EventID,
	 * EventParameter
	 */
	@Override
	public void addBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParam) {
		BlockEventData eventData = new BlockEventData(x, y, z, blockID, eventID, eventParam);
		Iterator nextEvent = this.blockEventCache[this.blockEventCacheIndex].iterator();
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
	public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk,
			boolean forceRemove) {
		ArrayList<NextTickListEntry> pendingUpdates = null;
		ChunkCoordIntPair chunkPair = chunk.getChunkCoordIntPair();
		int x = (chunkPair.chunkXPos << 4) + 2;
		int maxX = x + 16 + 2;
		int z = (chunkPair.chunkZPos << 4) + 2;
		int maxZ = z + 16 + 2;
		Iterator<NextTickListEntry> pendingTicks = this.pendingTickListEntries
				.iterator();
		while (pendingTicks.hasNext()) {
			NextTickListEntry nextTick = pendingTicks.next();
			if (nextTick.xCoord >= x && nextTick.xCoord < maxX
					&& nextTick.zCoord >= z && nextTick.zCoord < maxZ) {
				if (forceRemove) {
					this.scheduledTickSet.remove(nextTick);
					pendingTicks.remove();
				}
				if (pendingUpdates == null) {
					pendingUpdates = new ArrayList<NextTickListEntry>();
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
		this.scheduleBlockUpdateWithPriority(	x,
												y,
												z,
												blockId,
												tickRate,
												0);
	}

	/**
	 * Schedules a tick to a block with a delay (Most commonly the tick rate)
	 * with some Value
	 */
	@Override
	public void scheduleBlockUpdateWithPriority(int x, int y, int z, int blockId, int tickRate, int someValue) {
		NextTickListEntry nextTickEntry = new NextTickListEntry(x, y, z, blockId);
		byte max = 8;

		if (this.scheduledUpdatesAreImmediate && blockId > 0) {
			if (Block.blocksList[blockId].func_82506_l()) {
				if (this.checkChunksExist(	nextTickEntry.xCoord - max,
											nextTickEntry.yCoord - max,
											nextTickEntry.zCoord - max,
											nextTickEntry.xCoord + max,
											nextTickEntry.yCoord + max,
											nextTickEntry.zCoord + max)) {
					int nextTickId = this.getBlockId(	nextTickEntry.xCoord,
														nextTickEntry.yCoord,
														nextTickEntry.zCoord);

					if (nextTickId == nextTickEntry.blockID && nextTickId > 0) {
						Block.blocksList[nextTickId].updateTick(this,
																nextTickEntry.xCoord,
																nextTickEntry.yCoord,
																nextTickEntry.zCoord,
																this.rand);
					}
				}
				return;
			}
			tickRate = 1;
		}
		if (this.checkChunksExist(	x - max,
									y - max,
									z - max,
									x + max,
									y + max,
									z + max)) {
			if (blockId > 0) {
				nextTickEntry.setScheduledTime(tickRate
												+ this.getRealWorld().getWorldInfo().getWorldTotalTime());
				nextTickEntry.setPriority(someValue);
			}

			if (!this.scheduledTickSet.contains(nextTickEntry)) {
				this.scheduledTickSet.add(nextTickEntry);
				this.pendingTickListEntries.add(nextTickEntry);
			}
		}
		//this.getRealWorld().scheduleBlockUpdateWithPriority(x >> 3, y >> 3, z >> 3, ConfigurationLib.littleChunkID, tickRate, someValue);
	}

	/**
	 * Schedules a block update from the saved information in a chunk. Called
	 * when the chunk is loaded.
	 */
	@Override
	public void scheduleBlockUpdateFromLoad(int x, int y, int z, int blockId, int tickRate, int par6) {
		NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);
		nextTick.setPriority(par6);

		if (blockId > 0) {
			nextTick.setScheduledTime((long) tickRate
										+ this.getRealWorld().getWorldInfo().getWorldTotalTime());
		}

		if (!this.scheduledTickSet.contains(nextTick)) {
			this.scheduledTickSet.add(nextTick);
			this.pendingTickListEntries.add(nextTick);
		}
	}
}
