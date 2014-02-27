package com.slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.slimevoid.littleblocks.core.LoggerLittleBlocks;
import com.slimevoid.littleblocks.core.lib.BlockUtil;
import com.slimevoid.littleblocks.core.lib.PacketLib;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import com.slimevoid.littleblocks.world.events.LittleBlockEvent;
import com.slimevoid.littleblocks.world.events.LittleBlockEventList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet60Explosion;
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
import slimevoidlib.data.Logger;

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
        this.tickBlocksAndAmbiance();
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
                    int blockId = this.getBlockId(nextTick.xCoord,
                                                  nextTick.yCoord,
                                                  nextTick.zCoord);

                    if (blockId > 0
                        && Block.isAssociatedBlockID(blockId,
                                                     nextTick.blockID)) {
                        try {
                            Block littleBlock = Block.blocksList[blockId];
                            if (BlockUtil.isBlockAllowedToTick(littleBlock)) {
                                littleBlock.updateTick(this,
                                                       nextTick.xCoord,
                                                       nextTick.yCoord,
                                                       nextTick.zCoord,
                                                       this.rand);
                            } else {
                                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
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
                            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
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

                            CrashReportCategory.addBlockCrashInfo(var9,
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
                                             nextTick.blockID,
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
            TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getBlockTileEntity(pos.x,
                                                                                                          pos.y,
                                                                                                          pos.z);
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
    protected void tickBlocksAndAmbiance() {
        super.tickBlocksAndAmbiance();
        this.tickLittleChunks();
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
        NextTickListEntry nextticklistentry = new NextTickListEntry(x, y, z, blockId);
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
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty()) {
            int index = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;
            Iterator blockEvent = this.blockEventCache[index].iterator();

            while (blockEvent.hasNext()) {
                BlockEventData eventData = (BlockEventData) blockEvent.next();
                if (this.onBlockEventReceived(eventData)) {
                    PacketLib.sendBlockEvent(eventData.getX(),
                                             eventData.getY(),
                                             eventData.getZ(),
                                             eventData.getBlockID(),
                                             eventData.getEventID(),
                                             eventData.getEventParameter());
                } else {
                    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
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
        int blockId = this.getBlockId(blockEventData.getX(),
                                      blockEventData.getY(),
                                      blockEventData.getZ());

        if (blockId == blockEventData.getBlockID()) {
            return Block.blocksList[blockId].onBlockEventReceived(this,
                                                                  blockEventData.getX(),
                                                                  blockEventData.getY(),
                                                                  blockEventData.getZ(),
                                                                  blockEventData.getEventID(),
                                                                  blockEventData.getEventParameter());
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
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
    public void scheduleBlockUpdate(int x, int y, int z, int blockId, int tickRate) {
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
    public void scheduleBlockUpdateWithPriority(int x, int y, int z, int blockId, int tickRate, int priority) {
        NextTickListEntry nextTickEntry = new NextTickListEntry(x, y, z, blockId);
        byte max = 8;

        if (this.scheduledUpdatesAreImmediate && blockId > 0) {
            if (Block.blocksList[blockId].func_82506_l()) {
                if (this.checkChunksExist(nextTickEntry.xCoord - max,
                                          nextTickEntry.yCoord - max,
                                          nextTickEntry.zCoord - max,
                                          nextTickEntry.xCoord + max,
                                          nextTickEntry.yCoord + max,
                                          nextTickEntry.zCoord + max)) {
                    int nextTickId = this.getBlockId(nextTickEntry.xCoord,
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
        if (this.checkChunksExist(x - max,
                                  y - max,
                                  z - max,
                                  x + max,
                                  y + max,
                                  z + max)) {
            if (blockId > 0) {
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
    public void scheduleBlockUpdateFromLoad(int x, int y, int z, int blockId, int tickRate, int priority) {
        NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);
        nextTick.setPriority(priority);

        if (blockId > 0) {
            nextTick.setScheduledTime((long) tickRate
                                      + this.getParentWorld().getWorldInfo().getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(nextTick)) {
            this.pendingTickListEntriesHashSet.add(nextTick);
            this.pendingTickListEntriesTreeSet.add(nextTick);
        }
    }
}
