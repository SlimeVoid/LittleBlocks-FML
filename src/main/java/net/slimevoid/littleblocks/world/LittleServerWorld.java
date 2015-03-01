package net.slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.slimevoid.library.data.Logger;
import net.slimevoid.littleblocks.blocks.BlockLBPistonBase;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import net.slimevoid.littleblocks.world.events.LittleBlockEvent;
import net.slimevoid.littleblocks.world.events.LittleBlockEventList;

import java.util.*;

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
        super(world, worldprovider, true);
        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet<NextTickListEntry>();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet<NextTickListEntry>();
        }
    }

    @Override
    public void initialize(WorldSettings worldSettings) {
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
        //this.func_147456_g/* tickBlocksAndAmbiance */(); TODO
        this.sendAndApplyBlockEvents();
    }

    @Override
    protected int getRenderDistanceChunks() {
        return 0;
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
                if (this.isAreaLoaded(nextTick.position.add(-max, -max, -max), nextTick.position.add(max, max, max))) {
                    IBlockState blockState = this.getBlockState(nextTick.position);

                    if (blockState.getBlock() != Blocks.air
                        && blockState.getBlock().isAssociatedBlock(nextTick.getBlock())) {
                        try {
                            IBlockState littleBlock = blockState;
                            if (BlockUtil.isBlockAllowedToTick(littleBlock.getBlock())) {
                                littleBlock.getBlock().updateTick(this, nextTick.position, littleBlock, this.rand);
                            } else {
                                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                                         "BlockUpdateTick Prohibited["
                                                                                                                                 + littleBlock.getBlock().getLocalizedName()
                                                                                                                                 + "].("
                                                                                                                                 + nextTick.position
                                                                                                                                 + ")",
                                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
                            }
                        } catch (Throwable thrown) {
                            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                                     "BlockUpdateTick FAILED["
                                                                                                                             + blockState.getBlock().getLocalizedName()
                                                                                                                             + "].("
                                                                                                                             + nextTick.position
                                                                                                                             + ")",
                                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
                            CrashReport crashReport = CrashReport.makeCrashReport(thrown,
                                                                                  "Exception while ticking a block");
                            CrashReportCategory var9 = crashReport.makeCategory("Block being ticked");
//                            int metadata; TODO??
//
//                            try {
//                                metadata = this.getBlockMetadata(nextTick.xCoord,
//                                                                 nextTick.yCoord,
//                                                                 nextTick.zCoord);
//                            } catch (Throwable thrown2) {
//                                metadata = -1;
//                            }
//
//                            CrashReportCategory.func_147153_a/* addBlockCrashInfo */(var9,
//                                                                                     nextTick.xCoord,
//                                                                                     nextTick.yCoord,
//                                                                                     nextTick.zCoord,
//                                                                                     blockId,
//                                                                                     metadata);
//                            throw new ReportedException(crashReport);
                        }
                    }
                } else {
                    this.scheduleUpdate(nextTick.position, nextTick.getBlock(), 0);
                }
            }
            this.pendingTickListEntriesThisTick.clear();
            return !this.pendingTickListEntriesTreeSet.isEmpty();
        }
    }

    protected void tickLittleChunks() {
        Set<BlockPos> done = new HashSet<BlockPos>();
        for (BlockPos pos : this.activeChunkPosition) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(pos);
            if (tile != null /*&& tile.getNeedsRandomTick()TODO*/) {
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

//    @Override TODO fail?
//    protected void func_147456_g/* tickBlocksAndAmbiance */() {
//        super.func_147456_g();
//        this.tickLittleChunks();
//    }

    @Override
    public void updateEntities() {
        super.updateEntities();
    }

    /**
     * Returns true if the given block will receive a scheduled tick in the
     * future. Args: X, Y, Z, block
     */
    @Override
    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(pos, blockType);
        return this.pendingTickListEntriesThisTick.contains(nextticklistentry);
    }


    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is
     * not finished)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(false);

        if (!isSmoking) {
//            explosion.set.clear();
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
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S27PacketExplosion(xCoord, yCoord, zCoord, strength / 8, explosion.func_180343_e() /*affectedBlocks*/, (Vec3) explosion.func_77277_b().get(player)));
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
                    PacketLib.sendBlockEvent(eventData.getPosition().getX(),
                                            eventData.getPosition().getY(),
                                            eventData.getPosition().getZ(),
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
                                                                                                                     + eventData.getPosition().getX()
                                                                                                                     + ", "
                                                                                                                     + eventData.getPosition().getY()
                                                                                                                     + ", "
                                                                                                                     + eventData.getPosition().getZ()
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
        IBlockState state = this.getBlockState(blockEventData.getPosition());

        if (state.getBlock() == blockEventData.getBlock()) {
            if (state.getBlock() instanceof BlockPistonBase) {
//                return BlockLBPistonBase.onEventReceived(this, TODO PISTON
//                                                         blockEventData.func_151340_a(),
//                                                         blockEventData.func_151342_b(),
//                                                         blockEventData.func_151341_c(),
//                                                         blockEventData.getEventID(),
//                                                         blockEventData.getEventParameter());
            }
            return state.getBlock().onBlockEventReceived(this,
                    blockEventData.getPosition(),
                    state,
                    blockEventData.getEventID(),
                    blockEventData.getEventParameter());
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.isRemote,
                                                                                                     "FAILED:onBlockEvenReceived("
                                                                                                             + blockEventData.getBlock()
                                                                                                             + ").[Event: "
                                                                                                             + blockEventData.getEventID()
                                                                                                             + "("
                                                                                                             + blockEventData.getPosition().getX()
                                                                                                             + ", "
                                                                                                             + blockEventData.getPosition().getY()
                                                                                                             + ", "
                                                                                                             + blockEventData.getPosition().getZ()
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
    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        BlockEventData eventData = new BlockEventData(pos, blockIn, eventID, eventParam);
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
                if (nextTick.position.getX() >= x && nextTick.position.getX() < maxX
                    && nextTick.position.getZ() >= z && nextTick.position.getZ() < maxZ) {
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
    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
        this.scheduleBlockUpdateWithPriority(pos,
                blockIn,
                delay,
                0);
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     * with some Value
     */


//    @Override TODO ??
    public void scheduleBlockUpdateWithPriority(BlockPos pos, Block blockId, int delay, int priority) {
        NextTickListEntry nextTickEntry = new NextTickListEntry(pos, blockId);
        byte max = 8;

        if (this.scheduledUpdatesAreImmediate && blockId != Blocks.air) {
            if (blockId.requiresUpdates()) { // TODO == func_149698_L ?
                if (this.isAreaLoaded(nextTickEntry.position.add(-max, -max, -max), nextTickEntry.position.add(max, max, max))) {
                    IBlockState state = this.getBlockState(nextTickEntry.position);

                    if (state.getBlock().isAssociatedBlock(nextTickEntry.getBlock())
                            && state.getBlock() != Blocks.air) {
                        state.getBlock().updateTick(this,
                                nextTickEntry.position,
                                state,
                                this.rand);
                    }
                }
                return;
            }
            delay = 1;
        }
        if (this.isAreaLoaded(pos.add(-max, -max, -max), pos.add(max, max, max))) {
            if (blockId != Blocks.air) {
                nextTickEntry.setScheduledTime(delay
                        + this.getParentWorld().getWorldInfo().getWorldTotalTime());
                nextTickEntry.setPriority(priority);
            }

            if (!this.pendingTickListEntriesHashSet.contains(nextTickEntry)) {
                this.pendingTickListEntriesHashSet.add(nextTickEntry);
                this.pendingTickListEntriesTreeSet.add(nextTickEntry);
            }
        }
    }

//    /** TODO
//     * Schedules a block update from the saved information in a chunk. Called
//     * when the chunk is loaded.
//     */
//    @Override
//    public void func_147446_b/* scheduleBlockUpdateFromLoad */(int x, int y, int z, Block blockId, int tickRate, int priority) {
//        NextTickListEntry nextTick = new NextTickListEntry(x, y, z, blockId);
//        nextTick.setPriority(priority);
//
//        if (blockId != Blocks.air) {
//            nextTick.setScheduledTime((long) tickRate
//                                      + this.getParentWorld().getWorldInfo().getWorldTotalTime());
//        }
//
//        if (!this.pendingTickListEntriesHashSet.contains(nextTick)) {
//            this.pendingTickListEntriesHashSet.add(nextTick);
//            this.pendingTickListEntriesTreeSet.add(nextTick);
//        }
//    }

    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        return null;
    }
}
