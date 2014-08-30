package net.slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.slimevoid.library.data.Logger;
import net.slimevoid.littleblocks.blocks.BlockLBPistonBase;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import net.slimevoid.littleblocks.world.events.LittleBlockEvent;
import net.slimevoid.littleblocks.world.events.LittleBlockEventList;

public class LittleServerWorld extends LittleWorld {

    /**
     * TreeSet of scheduled ticks which is used as a priority queue for the
     * ticks
     */
    private TreeSet<NextTickListEntry>   pendingTickListEntriesTreeSet;

    /** Set of scheduled ticks (used for checking if a tick already exists) */
    private Set<NextTickListEntry>       pendingTickListEntriesHashSet;

    private ArrayList<NextTickListEntry> pendingTickListEntriesThisTick = new ArrayList<NextTickListEntry>();

    /**
     * Double buffer of ServerBlockEventList[] for holding pending
     * BlockEventData's
     */
    private LittleBlockEventList[]       blockEventCache                = new LittleBlockEventList[] {
            new LittleBlockEventList((LittleBlockEvent) null),
            new LittleBlockEventList((LittleBlockEvent) null)          };

    /**
     * The index into the blockEventCache; either 0, or 1, toggled in
     * sendBlockEventPackets where all BlockEvent are applied locally and send
     * to clients.
     */
    private int                          blockEventCacheIndex           = 0;

    protected int                        updateLCG                      = (new Random()).nextInt();

    public LittleServerWorld(World world, WorldProvider worldprovider) {
        super(world, worldprovider);
        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet<NextTickListEntry>();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet<NextTickListEntry>();
        }
    }

    @Override
    protected void initialize(WorldSettings worldSettings) {
        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet<NextTickListEntry>();
        }
        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet<NextTickListEntry>();
        }
    }

    @Override
    public void tick() {
        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        }
        this.tickUpdates(false);
        this.func_147456_g/* tickBlocksAndAmbiance */();
        this.sendAndApplyBlockEvents();
    }

    public boolean littleTickUpdates(boolean tick) {
        return tick;
    }

    @Override
    public boolean tickUpdates(boolean tick) {
        int numberOfUpdates = this.pendingTickListEntriesTreeSet.size();

        if (numberOfUpdates != this.pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (numberOfUpdates > 1000) {
                numberOfUpdates = 1000;
            }
            NextTickListEntry nextTick;
            for (int update = 0; update < numberOfUpdates; ++update) {
                nextTick = (NextTickListEntry) this.pendingTickListEntriesTreeSet.first();

                if (!tick
                    && nextTick.scheduledTime > this.getParentWorld().getWorldInfo().getWorldTotalTime()) {
                    break;
                }

                this.pendingTickListEntriesTreeSet.remove(nextTick);
                this.pendingTickListEntriesHashSet.remove(nextTick);
                this.pendingTickListEntriesThisTick.add(nextTick);
            }
            Iterator tickedEntryList = this.pendingTickListEntriesThisTick.iterator();

            while (tickedEntryList.hasNext()) {
                nextTick = (NextTickListEntry) tickedEntryList.next();
                tickedEntryList.remove();
                byte max = 0;
                if (this.checkChunksExist(nextTick.xCoord - max,
                                          nextTick.yCoord - max,
                                          nextTick.zCoord - max,
                                          nextTick.xCoord + max,
                                          nextTick.yCoord + max,
                                          nextTick.zCoord + max)) {
                    Block blockId = this.getBlock(nextTick.xCoord,
                                                  nextTick.yCoord,
                                                  nextTick.zCoord);

                    if (blockId != Blocks.air
                        && blockId.isAssociatedBlock(nextTick.func_151351_a())) {
                        try {
                            Block littleBlock = blockId;
                            if (BlockUtil.isBlockAllowedToTick(littleBlock)) {
                                littleBlock.updateTick(this,
                                                       nextTick.xCoord,
                                                       nextTick.yCoord,
                                                       nextTick.zCoord,
                                                       this.rand);
                            } else {
                                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                                         "BlockUpdateTick Prohibited["
                                                                                                                                 + littleBlock.getLocalizedName()
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
                            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                                     "BlockUpdateTick FAILED["
                                                                                                                             + blockId.getLocalizedName()
                                                                                                                             + "].("
                                                                                                                             + nextTick.xCoord
                                                                                                                             + ", "
                                                                                                                             + nextTick.yCoord
                                                                                                                             + ", "
                                                                                                                             + nextTick.zCoord
                                                                                                                             + ")",
                                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
                            CrashReport crashReport = CrashReport.makeCrashReport(thrown,
                                                                                  "Exception while ticking a block");
                            CrashReportCategory var9 = crashReport.makeCategory("Block being ticked");
                            int metadata;

                            try {
                                metadata = this.getBlockMetadata(nextTick.xCoord,
                                                                 nextTick.yCoord,
                                                                 nextTick.zCoord);
                            } catch (Throwable thrown2) {
                                metadata = -1;
                            }

                            CrashReportCategory.func_147153_a/* addBlockCrashInfo */(var9,
                                                                                     nextTick.xCoord,
                                                                                     nextTick.yCoord,
                                                                                     nextTick.zCoord,
                                                                                     blockId,
                                                                                     metadata);
                            throw new ReportedException(crashReport);
                        }
                    }
                } else {
                    this.scheduleBlockUpdate(nextTick.xCoord,
                                             nextTick.yCoord,
                                             nextTick.zCoord,
                                             nextTick.func_151351_a(),
                                             0);
                }
            }
            this.pendingTickListEntriesThisTick.clear();
            return !this.pendingTickListEntriesTreeSet.isEmpty();
        }
    }

    protected void tickLittleChunks() {
        Set<ChunkPosition> done = new HashSet<ChunkPosition>();
        for (ChunkPosition pos : this.activeChunkPosition) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(pos.chunkPosX,
                                                                                                     pos.chunkPosY,
                                                                                                     pos.chunkPosZ);
            if (tile != null && tile.getNeedsRandomTick()) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                if (updateLCG % 3 == 0) {
                    tile.littleUpdateTick(this,
                                          this.updateLCG);
                }
            }
            if (tile == null) {
                done.add(pos);
            }
        }
        if (done.size() > 0) {
            this.activeChunkPosition.removeAll(done);
        }
    }

    @Override
    protected void func_147456_g/* tickBlocksAndAmbiance */() {
        super.func_147456_g();
        this.tickLittleChunks();
    }

    @Override
    public void updateEntities() {
        super.updateEntities();
    }

    /**
     * Returns true if the given block will receive a scheduled tick in the
     * future. Args: X, Y, Z, block
     */
    @Override
    public boolean isBlockTickScheduledThisTick(int x, int y, int z, Block block) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(x, y, z, block);
        return this.pendingTickListEntriesThisTick.contains(nextticklistentry);
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

        Iterator players = this.getParentWorld().playerEntities.iterator();

        while (players.hasNext()) {
            EntityPlayer player = (EntityPlayer) players.next();

            if (player.getDistanceSq(xCoord,
                                     yCoord,
                                     zCoord) < 4096.0D) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S27PacketExplosion(xCoord, yCoord, zCoord, strength / 8, explosion.affectedBlockPositions, (Vec3) explosion.func_77277_b().get(player)));
            }
        }

        return explosion;
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m
     * radius of the event.
     */
    private void sendAndApplyBlockEvents() {
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
            int index = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;
            Iterator blockEvent = this.blockEventCache[index].iterator();

            while (blockEvent.hasNext()) {
                BlockEventData eventData = (BlockEventData) blockEvent.next();
                if (this.onBlockEventReceived(eventData)) {
                    PacketLib.sendBlockEvent(eventData.func_151340_a(),
                                             eventData.func_151342_b(),
                                             eventData.func_151341_c(),
                                             Block.getIdFromBlock(eventData.getBlock()),
                                             eventData.getEventID(),
                                             eventData.getEventParameter());
                } else {
                    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                             "onBlockEvenReceived("
                                                                                                                     + eventData.getBlock().getLocalizedName()
                                                                                                                     + ").[Event: "
                                                                                                                     + eventData.getEventID()
                                                                                                                     + "("
                                                                                                                     + eventData.func_151340_a()
                                                                                                                     + ", "
                                                                                                                     + eventData.func_151342_b()
                                                                                                                     + ", "
                                                                                                                     + eventData.func_151341_c()
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
        Block block = this.getBlock(blockEventData.func_151340_a(),
                                    blockEventData.func_151342_b(),
                                    blockEventData.func_151341_c());

        if (block == blockEventData.getBlock()) {
            if (block instanceof BlockPistonBase) {
                return BlockLBPistonBase.onEventReceived(this,
                                                         blockEventData.func_151340_a(),
                                                         blockEventData.func_151342_b(),
                                                         blockEventData.func_151341_c(),
                                                         blockEventData.getEventID(),
                                                         blockEventData.getEventParameter());
            }
            return block.onBlockEventReceived(this,
                                              blockEventData.func_151340_a(),
                                              blockEventData.func_151342_b(),
                                              blockEventData.func_151341_c(),
                                              blockEventData.getEventID(),
                                              blockEventData.getEventParameter());
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                     "FAILED:onBlockEvenReceived("
                                                                                                             + blockEventData.getBlock()
                                                                                                             + ").[Event: "
                                                                                                             + blockEventData.getEventID()
                                                                                                             + "("
                                                                                                             + blockEventData.func_151340_a()
                                                                                                             + ", "
                                                                                                             + blockEventData.func_151342_b()
                                                                                                             + ", "
                                                                                                             + blockEventData.func_151341_c()
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
    public void addBlockEvent(int x, int y, int z, Block blockID, int eventID, int eventParam) {
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
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean forceRemove) {
        ArrayList<NextTickListEntry> pendingUpdates = null;
        ChunkCoordIntPair chunkPair = chunk.getChunkCoordIntPair();
        int x = (chunkPair.chunkXPos << 3) - 2;
        int maxX = x + 10;
        int z = (chunkPair.chunkZPos << 3) - 2;
        int maxZ = z + 10;

        for (int i = 0; i < 2; i++) {
            Iterator<NextTickListEntry> pendingTicks;

            if (i == 0) {
                pendingTicks = this.pendingTickListEntriesTreeSet.iterator();
            } else {
                pendingTicks = this.pendingTickListEntriesThisTick.iterator();
            }
            while (pendingTicks.hasNext()) {
                NextTickListEntry nextTick = pendingTicks.next();
                if (nextTick.xCoord >= x && nextTick.xCoord < maxX
                    && nextTick.zCoord >= z && nextTick.zCoord < maxZ) {
                    if (forceRemove) {
                        this.pendingTickListEntriesHashSet.remove(nextTick);
                        pendingTicks.remove();
                    }
                    if (pendingUpdates == null) {
                        pendingUpdates = new ArrayList<NextTickListEntry>();
                    }
                    pendingUpdates.add(nextTick);
                }
            }
        }
        return pendingUpdates;
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    @Override
    public void scheduleBlockUpdate(int x, int y, int z, Block blockId, int tickRate) {
        this.scheduleBlockUpdateWithPriority(x,
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
    public void scheduleBlockUpdateWithPriority(int x, int y, int z, Block blockId, int tickRate, int priority) {
        NextTickListEntry nextTickEntry = new NextTickListEntry(x, y, z, blockId);
        byte max = 8;

        if (this.scheduledUpdatesAreImmediate && blockId != Blocks.air) {
            if (blockId.func_149698_L()) {
                if (this.checkChunksExist(nextTickEntry.xCoord - max,
                                          nextTickEntry.yCoord - max,
                                          nextTickEntry.zCoord - max,
                                          nextTickEntry.xCoord + max,
                                          nextTickEntry.yCoord + max,
                                          nextTickEntry.zCoord + max)) {
                    Block nextTickId = this.getBlock(nextTickEntry.xCoord,
                                                     nextTickEntry.yCoord,
                                                     nextTickEntry.zCoord);

                    if (nextTickId == nextTickEntry.func_151351_a()
                        && nextTickId != Blocks.air) {
                        nextTickId.updateTick(this,
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
        if (this.checkChunksExist(x - max,
                                  y - max,
                                  z - max,
                                  x + max,
                                  y + max,
                                  z + max)) {
            if (blockId != Blocks.air) {
                nextTickEntry.setScheduledTime(tickRate
                                               + this.getParentWorld().getWorldInfo().getWorldTotalTime());
                nextTickEntry.setPriority(priority);
            }

            if (!this.pendingTickListEntriesHashSet.contains(nextTickEntry)) {
                this.pendingTickListEntriesHashSet.add(nextTickEntry);
                this.pendingTickListEntriesTreeSet.add(nextTickEntry);
            }
        }
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called
     * when the chunk is loaded.
     */
    @Override
    public void func_147446_b/* scheduleBlockUpdateFromLoad */(int x, int y, int z, Block blockId, int tickRate, int priority) {
        NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);
        nextTick.setPriority(priority);

        if (blockId != Blocks.air) {
            nextTick.setScheduledTime((long) tickRate
                                      + this.getParentWorld().getWorldInfo().getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(nextTick)) {
            this.pendingTickListEntriesHashSet.add(nextTick);
            this.pendingTickListEntriesTreeSet.add(nextTick);
        }
    }

    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        return null;
    }

	@Override
	/** getRenderViewDistance()**/
	protected int func_152379_p() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getViewDistance();
	}
}
