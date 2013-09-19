package slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import slimevoid.littleblocks.api.ILittleWorld;
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
import net.minecraft.logging.ILogAgent;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ForgeDirection;

public class LittleWorldServer extends WorldServer implements ILittleWorld {

	/**
	 * TreeSet of scheduled ticks which is used as a priority queue for the
	 * ticks
	 */
	private TreeSet<NextTickListEntry> pendingTickListEntries;

	/** Set of scheduled ticks (used for checking if a tick already exists) */
	private Set<NextTickListEntry> scheduledTickSet;
	
	
    private ArrayList<NextTickListEntry> tickedEntries = new ArrayList<NextTickListEntry>();

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
	
	private final World realWorld;
	private final LittleWorld littleWorld;

	public LittleWorldServer(World referenceWorld, MinecraftServer minecraftServer, ISaveHandler iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7iLogAgent) {
		super(minecraftServer, iSaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7iLogAgent);
		this.realWorld = referenceWorld;
		this.littleWorld = new LittleWorld(referenceWorld, this.provider);
		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet<NextTickListEntry>();
		}

		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet<NextTickListEntry>();
		}
	}

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	public LittleWorld getLittleWorld() {
		return this.littleWorld;
	}
	
	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		Block block = Block.blocksList[blockId];
		if (block != null) {
			PacketLib.sendMetadata(
					this,
					blockX,
					blockY,
					blockZ,
					blockId,
					side,
					metadata);
		}
	}

	@Override
	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		if (lastBlockId != 0) {
			Block block = Block.blocksList[lastBlockId];
			if (block != null) {
				block.breakBlock(
						this,
						blockX,
						blockY,
						blockZ,
						side,
						metadata);
				PacketLib.sendBreakBlock(
						this.getLittleWorld(),
						blockX,
						blockY,
						blockZ,
						side,
						lastBlockId,
						metadata);
			}
		}
		if (blockId != 0) {
			Block block = Block.blocksList[blockId];
			if (block != null) {
				block.onBlockAdded(
						this,
						blockX,
						blockY,
						blockZ);
				PacketLib.sendBlockAdded(
						this.getLittleWorld(),
						blockX,
						blockY,
						blockZ,
						side,
						blockId,
						metadata);
			}
		}
	}

	@Override
	public boolean isOutdated(World world) {
		return this.getLittleWorld().isOutdated(world);
	}
	
	@Override
	protected void initialize(WorldSettings worldSettings) {
		if (this.scheduledTickSet == null) {
			this.scheduledTickSet = new HashSet<NextTickListEntry>();
		}
		if (this.pendingTickListEntries == null) {
			this.pendingTickListEntries = new TreeSet<NextTickListEntry>();
		}
		this.getLittleWorld().initializeLittleWorld(worldSettings);
	}

	@Override
	public void tick() {
        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);
        this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
		this.tickUpdates(false);
		this.sendAndApplyBlockEvents();
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
				nextTick = (NextTickListEntry)this.pendingTickListEntries
						.first();

				if (!tick && nextTick.scheduledTime > this.getRealWorld().getWorldInfo()
						.getWorldTotalTime()) {
					break;
				}

				this.pendingTickListEntries.remove(nextTick);
				this.scheduledTickSet.remove(nextTick);
                this.tickedEntries.add(nextTick);
			}
            Iterator tickedEntryList = this.tickedEntries.iterator();

            while (tickedEntryList.hasNext()) {
            	nextTick = (NextTickListEntry)tickedEntryList.next();
            	tickedEntryList.remove();
				byte max = 0;
				if (this.checkChunksExist(
						nextTick.xCoord - max,
						nextTick.yCoord - max,
						nextTick.zCoord - max,
						nextTick.xCoord + max,
						nextTick.yCoord + max,
						nextTick.zCoord + max)) {
					//System.out.println("Existing Chunk");
					int blockId = this.getBlockId(
							nextTick.xCoord,
							nextTick.yCoord,
							nextTick.zCoord);

					if (blockId > 0 && Block.isAssociatedBlockID(blockId, nextTick.blockID)) {
						//System.out.println("Associated Ticking Block");
						try {
							Block littleBlock = Block.blocksList[blockId];
							if (BlockUtil.isBlockAllowedToTick(littleBlock)) {
								//System.out.println("Allowed to Tick");
								littleBlock.updateTick(
										this,
										nextTick.xCoord,
										nextTick.yCoord,
										nextTick.zCoord,
										this.rand);
							} else {
								LoggerLittleBlocks.getInstance(
										Logger.filterClassName(
												this.getClass().toString()
										)
								).write(
										this.isRemote,
										"BlockUpdateTick Prohibited[" + Block.blocksList[littleBlock.blockID].getLocalizedName() + "].("+ 
												nextTick.xCoord + ", " +
												nextTick.yCoord + ", " +
												nextTick.zCoord + ")",
										LoggerLittleBlocks.LogLevel.DEBUG
								);
							}
						} catch(Throwable thrown) {
							LoggerLittleBlocks.getInstance(
									Logger.filterClassName(
											this.getClass().toString()
									)
							).write(
									this.isRemote,
									"BlockUpdateTick FAILED[" + Block.blocksList[blockId].getLocalizedName() + "].("+ 
											nextTick.xCoord + ", " +
											nextTick.yCoord + ", " +
											nextTick.zCoord + ")",
									LoggerLittleBlocks.LogLevel.DEBUG
							);
							CrashReport crashReport = CrashReport.makeCrashReport(thrown, "Exception while ticking a block");
							CrashReportCategory var9 = crashReport.makeCategory("Block being ticked");
							int metadata;
							
							try {
								metadata = this.getBlockMetadata(nextTick.xCoord, nextTick.yCoord, nextTick.zCoord);
							} catch (Throwable thrown2) {
								metadata = -1;
							}
							
							CrashReportCategory.addBlockCrashInfo(var9, nextTick.xCoord, nextTick.yCoord, nextTick.zCoord, blockId, metadata);
							throw new ReportedException(crashReport);
						}
						/*TileEntity tileentity = this
								.getRealWorld()
									.getBlockTileEntity(
											nextTick.xCoord >> 3,
											nextTick.yCoord >> 3,
											nextTick.zCoord >> 3);
						if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
							if (!littleBlockTiles.contains(tileentity)) {
								littleBlockTiles.add(tileentity);
							}
						}*/
					}
				} else {
					//System.out.println("Scheduling Update....");
                    this.scheduleBlockUpdate(nextTick.xCoord, nextTick.yCoord, nextTick.zCoord, nextTick.blockID, 0);
                }
			}
			/*if (this.ticksInWorld >= MAX_TICKS_IN_WORLD) {
				for (TileEntity tile : littleBlockTiles) {
				}
			}*/
            this.tickedEntries.clear();
			return !this.pendingTickListEntries.isEmpty();
		}
	}
	
	@Override
	public void updateEntities() {
		this.getLittleWorld().updateEntities();
	}

	/**
	 * Returns true if the given block will receive a scheduled tick in the
	 * future. Args: X, Y, Z, blockID
	 */
	@Override
	public boolean isBlockTickScheduledThisTick(int x, int y, int z, int blockId) {
		//System.out.println("isBlockTickScheduled");
		NextTickListEntry nextticklistentry = new NextTickListEntry(x, y,
				z, blockId);
		return this.tickedEntries.contains(nextticklistentry);
	}

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    @SuppressWarnings("rawtypes")
	@Override
	public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
        Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8);
        explosion.isFlaming = isFlaming;
        explosion.isSmoking = isSmoking;
        explosion.doExplosionA();
        explosion.doExplosionB(false);

        if (!isSmoking)
        {
            explosion.affectedBlockPositions.clear();
        }
        
        double xCoord = (int)x >> 3;
        double yCoord = (int)y >> 3;
        double zCoord = (int)z >> 3;

        Iterator players = this.getRealWorld().playerEntities.iterator();

        while (players.hasNext())
        {
            EntityPlayer player = (EntityPlayer)players.next();

            if (player.getDistanceSq(xCoord, yCoord, zCoord) < 4096.0D)
            {
                ((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(xCoord, yCoord, zCoord, strength / 8, explosion.affectedBlockPositions, (Vec3)explosion.func_77277_b().get(player)));
            }
        }

        return explosion;
    }

	/**
	 * Send and apply locally all pending BlockEvents to each player with 64m
	 * radius of the event.
	 */
	private void sendAndApplyBlockEvents() {
		//Set<TileEntityLittleBlocks> tileentities = new HashSet();
		while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
			int index = this.blockEventCacheIndex;
			this.blockEventCacheIndex ^= 1;
			Iterator blockEvent = this.blockEventCache[index].iterator();

			while (blockEvent.hasNext()) {
				BlockEventData eventData = (BlockEventData) blockEvent.next();
				if (this.onBlockEventReceived(eventData)) {
					PacketLib.sendBlockEvent(
							eventData.getX(),
							eventData.getY(),
							eventData.getZ(),
							eventData.getBlockID(),
							eventData.getEventID(),
							eventData.getEventParameter());
				} else {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							this.isRemote,
							"onBlockEvenReceived(" + eventData.getBlockID() + ").[Event: " + 
									eventData.getEventID() + "(" +
									eventData.getX() + ", " +
									eventData.getY() + ", " +
									eventData.getZ() + "), " + 
									eventData.getEventParameter(),
							LoggerLittleBlocks.LogLevel.DEBUG
					);
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
			return Block.blocksList[blockId].onBlockEventReceived(
					this,
					blockEventData.getX(),
					blockEventData.getY(),
					blockEventData.getZ(),
					blockEventData.getEventID(),
					blockEventData.getEventParameter());
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"FAILED:onBlockEvenReceived(" + blockEventData.getBlockID() + ").[Event: " + 
							blockEventData.getEventID() + "(" +
							blockEventData.getX() + ", " +
							blockEventData.getY() + ", " +
							blockEventData.getZ() + "), " + 
							blockEventData.getEventParameter(),
					LoggerLittleBlocks.LogLevel.DEBUG
			);
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

/*	@Override
	public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean forceRemove) {
		ArrayList<NextTickListEntry> pendingUpdates = null;
		ChunkCoordIntPair chunkPair = chunk.getChunkCoordIntPair();
		int x = (chunkPair.chunkXPos << 4) + 2;
		int maxX = x + 16 + 2;
		int z = (chunkPair.chunkZPos << 4) + 2;
		int maxZ = z + 16 + 2;
		Iterator<NextTickListEntry> pendingTicks = this.pendingTickListEntries.iterator();

		while (pendingTicks.hasNext()) {
			NextTickListEntry nextTick = pendingTicks
					.next();

			if (nextTick.xCoord >= x && nextTick.xCoord < maxX && nextTick.zCoord >= z && nextTick.zCoord < maxZ) {
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
	}*/

	/**
	 * Schedules a tick to a block with a delay (Most commonly the tick rate)
	 */
	@Override
	public void scheduleBlockUpdate(int x, int y, int z, int blockId, int tickRate) {
		this.scheduleBlockUpdateWithPriority(x, y, z, blockId, tickRate, 0);
	}

	/**
	 * Schedules a tick to a block with a delay (Most commonly the tick rate) with some Value
	 */
	@Override
    public void scheduleBlockUpdateWithPriority(int x, int y, int z, int blockId, int tickRate, int someValue) {
		NextTickListEntry nextTickEntry = new NextTickListEntry(x, y, z, blockId);
		byte max = 8;

		if (this.scheduledUpdatesAreImmediate && blockId > 0) {
			if (Block.blocksList[blockId].func_82506_l()) {
				if (this.checkChunksExist(
						nextTickEntry.xCoord - max,
						nextTickEntry.yCoord - max,
						nextTickEntry.zCoord - max,
						nextTickEntry.xCoord + max,
						nextTickEntry.yCoord + max,
						nextTickEntry.zCoord + max)) {
					int nextTickId = this.getBlockId(
							nextTickEntry.xCoord,
							nextTickEntry.yCoord,
							nextTickEntry.zCoord);
	
					if (nextTickId == nextTickEntry.blockID && nextTickId > 0) {
						Block.blocksList[nextTickId].updateTick(
								this,
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
		if (this.checkChunksExist(
				x - max,
				y - max,
				z - max,
				x + max,
				y + max,
				z + max)) {
			if (blockId > 0) {
				nextTickEntry.setScheduledTime(tickRate + this.getRealWorld().getWorldInfo()
						.getWorldTotalTime());
				nextTickEntry.setPriority(someValue);
			}

			if (!this.scheduledTickSet.contains(nextTickEntry)) {
				this.scheduledTickSet.add(nextTickEntry);
				this.pendingTickListEntries.add(nextTickEntry);
			}
		}
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
			nextTick.setScheduledTime((long)tickRate + this.getRealWorld().getWorldInfo().getWorldTotalTime());
		}

		if (!this.scheduledTickSet.contains(nextTick)) {
			this.scheduledTickSet.add(nextTick);
			this.pendingTickListEntries.add(nextTick);
		}
	}
	
	@Override
	public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
		if (!this.isRemote) {
			if (this.blockExists(x, y, z)) {
				PacketLib.sendTileEntity(this.littleWorld, tileentity, x, y, z);
			}
		}
	}
	
	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
		return this.getLittleWorld().getSkyBlockTypeBrightness(enumskyblock, x, y, z);
	}
	
	@Override
	public long getWorldTime() {
		return this.getLittleWorld().getWorldTime();
	}
	
	@Override
	public long getTotalWorldTime() {
		return this.getLittleWorld().getTotalWorldTime();
	}
	
	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
		return this.getLittleWorld().getLightBrightnessForSkyBlocks(x, y, z, l);
	}
	
	@Override
	public float getBrightness(int x, int y, int z, int l) {
		return this.getLittleWorld().getBrightness(x, y, z, l);
	}
	
	@Override
	public int getBlockLightValue(int x, int y, int z) {
		return this.getLittleWorld().getBlockLightValue(x, y, z);
	}
	
	@Override
	public void setSpawnLocation() {
		this.getLittleWorld().setSpawnLocation();
	}
	
	@Override
	public boolean blockExists(int x, int y, int z) {
		return this.getLittleWorld().blockExists(x, y, z);
	}
	
	@Override
	public int getBlockId(int x, int y, int z) {
		return this.getLittleWorld().getBlockId(x, y, z);
	}
	
	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		return this.getLittleWorld().spawnEntityInWorld(entity);
	}
	
	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return this.getLittleWorld().getBlockMetadata(x, y, z);
	}
	
	@Override
	public int getHeight() {
		return this.getLittleWorld().getHeight();
	}
	
	//@Override
	//public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update, boolean newTile) {
	//	return this.getLittleWorld().setBlock(x, y, z, blockID, newmeta, update, newTile);
	//}
	
	@Override
	public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update) {
		return this.getLittleWorld().setBlock(x, y, z, blockID, newmeta, update);
	}
	
	@Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int newmeta, int update) {
		return this.getLittleWorld().setBlockMetadataWithNotify(x, y, z, newmeta, update);
	}
	
	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return this.getLittleWorld().checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
		this.getLittleWorld().notifyBlocksOfNeighborChange(x, y, z, blockId);
	}
	
	@Override
	public void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
		this.getLittleWorld().notifyBlockOfNeighborChange(x, y, z, blockId);
	}
	
	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		return this.getLittleWorld().getBlockTileEntity(x, y, z);
	}
	
	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
		this.getLittleWorld().setBlockTileEntity(x, y, z, tileentity);
	}
	
	@Override
	public void addTileEntity(Collection collection) {
		this.getLittleWorld().addTileEntity(collection);
	}
	
	@Override
	public void addTileEntity(TileEntity tileentity) {
		this.getLittleWorld().addTileEntity(tileentity);
	}
	
	@Override
	public void markTileEntityForDespawn(TileEntity tileentity) {
		this.getLittleWorld().markTileEntityForDespawn(tileentity);
	}
	
	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		this.getLittleWorld().removeBlockTileEntity(x, y, z);
	}
	
	@Override
	public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
		return this.getLittleWorld().isBlockSolidOnSide(x, y, z, side, _default);
	}
	
	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		this.getLittleWorld().playSoundEffect(x, y, z, s, f, f1);
	}
	
	@Override
	public void playRecord(String s, int x, int y, int z) {
		this.getLittleWorld().playRecord(s, x, y, z);
	}
	
	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		this.getLittleWorld().playAuxSFX(x, y, z, l, i1);
	}
	
	@Override
	public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
		this.getLittleWorld().spawnParticle(s, x, y, z, d3, d4, d5);
	}
	
	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		return this.getLittleWorld().rayTraceBlocks_do_do(Vec3, Vec31, flag, flag1);
	}
	
	@Override
	protected IChunkProvider createChunkProvider() {
		return this.getLittleWorld().createChunkProvider();
	}
	
	@Override
	public Entity getEntityByID(int entityId) {
		return this.getLittleWorld().getEntityByID(entityId);
	}
	
	@Override
	public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
		return this.getLittleWorld().getClosestPlayer(x, y, z, distance);
	}
	
	@Override
	public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
		return this.getLittleWorld().getClosestVulnerablePlayer(x, y, z, distance);
	}
	
	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		this.getLittleWorld().markBlockForUpdate(x, y, z);
	}
	
	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		this.getLittleWorld().markBlockForRenderUpdate(x, y, z);
	}
	
	@Override
	public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
		this.getLittleWorld().markBlockRangeForRenderUpdate(x, y, z, x2, y2, z2);
	}
	
	@Override
	public void updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
		this.getLittleWorld().updateLightByType(enumSkyBlock, x, y, z);
	}
}
