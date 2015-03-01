package net.slimevoid.littleblocks.world;

import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.library.data.Logger;
import net.slimevoid.library.data.Logger.LogLevel;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import java.util.*;
import java.util.concurrent.Callable;

public abstract class LittleWorld extends World implements ILittleWorld {

    private List               addedTileEntityList             = new ArrayList<TileEntity>();
    private boolean           processingLoadedTiles;

    /** Entities marked for removal. */
    private List              tileRemoval = new ArrayList();

    protected int              ticksInWorld                    = 0;
    protected static final int MAX_TICKS_IN_WORLD              = 5;
    public Set<BlockPos>  activeChunkPosition             = new HashSet<BlockPos>();

    private int                parentDimension;

    public LittleWorld(World world, WorldProvider worldprovider, boolean isRemote) {
        super(world.getSaveHandler(),
              world.getWorldInfo(),
              worldprovider,
                world.theProfiler,
//              new WorldSettings(world.getWorldInfo().getSeed(), world
//                      .getWorldInfo().getGameType(), world.getWorldInfo()
//                      .isMapFeaturesEnabled(), world.getWorldInfo()
//                      .isHardcoreModeEnabled(), world.getWorldInfo()
//                      .getTerrainType()),
                isRemote); // TODO check that
        this.lightUpdateBlockList = new int[32768];
        this.parentDimension = world.provider.getDimensionId();
//        this.isRemote = true;
    }

    /* OLD public LittleWorld(World world, WorldProvider worldprovider) {
        super(world.getSaveHandler(),
              "LittleBlocksWorld",
              new WorldSettings(world.getWorldInfo().getSeed(), world
                      .getWorldInfo().getGameType(), world.getWorldInfo()
                      .isMapFeaturesEnabled(), world.getWorldInfo()
                      .isHardcoreModeEnabled(), world.getWorldInfo()
                      .getTerrainType()),
              worldprovider,
              null);
        this.lightUpdateBlockList = new int[32768];
        this.parentDimension = world.provider.dimensionId;
        this.isRemote = false;
    }*/

    @Override
    public void tick() {
        super.tick();
        this.setWorldTime(this.getTotalWorldTime() + 1L); // OLD func_82738_a
        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }
        //this.func_147456_g()/* tickBlocksAndAmbiance() */; TODO
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    @Override
    public boolean tickUpdates(boolean tick) {
        return false;
    }

    private final Set previousActiveChunkSet = new HashSet();

//    @Override TODO
//    protected void func_147456_g()/* tickBlocksAndAmbiance() */{
//        this.setActivePlayerChunksAndCheckLight();
//    }

    @Override
    public ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
        return this.getParentWorld().getPersistentChunks();
    }

    @Override
    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.activeChunkSet.addAll(this.getParentWorld().getPersistentChunks()
                .keySet());
        int i;
        EntityPlayer entityplayer;
        int j;
        int k;

        for (i = 0; i < this.getParentWorld().playerEntities.size(); ++i) {
            entityplayer = (EntityPlayer) this.getParentWorld().playerEntities
                    .get(i);
            j = MathHelper.floor_double(entityplayer.posX / 16.0D);
            k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
            byte b0 = 7;

            for (int l = -b0; l <= b0; ++l) {
                for (int i1 = -b0; i1 <= b0; ++i1) {
                    this.activeChunkSet
                            .add(new ChunkCoordIntPair(l + j, i1 + k));
                }
            }
        }
    }

    @Override
    public void updateEntities() {
        this.processingLoadedTiles = true;
        Iterator<TileEntity> iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = iterator.next();

            if (!tileentity.isInvalid() && tileentity.hasWorldObj() && !this.isAirBlock(tileentity.getPos())) {
                try {
                    ((IUpdatePlayerListBox)tileentity).update();
                } catch (Throwable t) {
                    SlimevoidCore.console(CoreLib.MOD_ID,
                                          t.getLocalizedMessage(),
                                          LogLevel.WARNING.ordinal());
                    LoggerLittleBlocks
                            .getInstance(Logger.filterClassName(this.getClass()
                                    .toString()))
                            .write(this.getParentWorld().isRemote,
                                   "updateEntities(" + tileentity.toString() + ", " + tileentity.getPos() + ").[" + t
                                           .getLocalizedMessage() + "]",
                                   LoggerLittleBlocks.LogLevel.DEBUG);
                }
            }

            if (tileentity.isInvalid()) {
                iterator.remove();

                TileEntity tileentitylb = this.getParentWorld()
                        .getTileEntity(BlockUtil.getParentPos(tileentity.getPos()));
                if (tileentitylb != null && tileentitylb instanceof TileEntityLittleChunk) {
                    ((TileEntityLittleChunk) tileentitylb)
                            .removeInvalidTileEntity(BlockUtil.getLittlePos(tileentity.getPos()));
                }
            }
        }

        if (!this.tileRemoval.isEmpty()) {
            for (Object tile : tileRemoval) {
                ((TileEntity) tile).onChunkUnload();
            }
            this.loadedTileEntityList.removeAll(this.tileRemoval);
            this.tileRemoval.clear();
        }

        this.processingLoadedTiles = false;

        if (!this.addedTileEntityList.isEmpty()) {
            for (int i = 0; i < this.addedTileEntityList.size(); ++i) {
                TileEntity tileentity = (TileEntity) this.addedTileEntityList
                        .get(i);

                if (!tileentity.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity)) {
                        this.loadedTileEntityList.add(tileentity);
                    }
                } else {
                    TileEntity tileentitylb = this.getParentWorld()
                            .getTileEntity(BlockUtil.getLittlePos(tileentity.getPos()));
                    if (tileentitylb != null && tileentitylb instanceof TileEntityLittleChunk) {
                        ((TileEntityLittleChunk) tileentitylb)
                                .removeInvalidTileEntity(BlockUtil.getLittlePos(tileentity.getPos()));
                    }
                }
            }

            this.addedTileEntityList.clear();
        }
    }

//    @Override OLD
//    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
//        if (this.getParentWorld() != null) {
//            return this.getParentWorld()
//                    .getSkyBlockTypeBrightness(enumskyblock,
//                                               x >> 3,
//                                               y >> 3,
//                                               z >> 3);
//        } else {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getSkyBlockTypeBrightness(" + enumskyblock + ", " + x + ", " + y + ", " + z + ").[null]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return 0;
//        }
//    }


    @Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        if(this.getParentWorld() != null) {
            return this.getParentWorld().getLightFor(type, BlockUtil.getParentPos(pos));
        } else {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                           "getSkyBlockTypeBrightness(" + type + ", " + pos  + ").[null]",
                           LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }

    @Override
    public long getWorldTime() {
        // if (this.getParentWorld() != null) {
        // return this.getParentWorld().provider.getWorldTime();
        // } else {
        // LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
        // "getWorldTime().[null]",
        // LoggerLittleBlocks.LogLevel.DEBUG);
        return super.getWorldTime();
        // }
    }

    @Override
    public long getTotalWorldTime() {
        // if (this.getParentWorld() != null) {
        // return this.getParentWorld().getWorldInfo().getWorldTotalTime();
        // } else {
        // LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
        // "getTotalWorldTime().[null]",
        // LoggerLittleBlocks.LogLevel.DEBUG);
        return super.getTotalWorldTime();
        // }
    }

//    @Override TODO ?
//    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
//        if (this.getParentWorld() != null) {
//            return this.getParentWorld().getLightBrightnessForSkyBlocks(x >> 3,
//                                                                        y >> 3,
//                                                                        z >> 3,
//                                                                        l);
//        } else {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getLightBrightnessForSkyBlocks(" + x + ", " + y + ", " + z + ").[" + l + "]:Null",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return 0;
//        }
//    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getLightBrightness(BlockUtil.getParentPos(pos));
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this
                    .getClass().toString()))
                    .write(this.getParentWorld().isRemote,
                            "getBrightness().[null]",
                            LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }


    @Override
    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
        if (this.isValid(pos)) {
            Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(pos.getX() >> 7, pos.getZ() >> 7);
//            if (chunk.getBlock((pos.getX() & 0x7f) >> 3, OLD
//                                pos.getY() >> 3,
//                                (pos.getZ() & 0x7f) >> 3) != ConfigurationLib.littleChunk && this.isAirBlock(pos)) {
            if(this.getParentWorld().isAirBlock(BlockUtil.getParentPos(pos))) {
                this.getParentWorld()
                        .setLightFor(type,
                                BlockUtil.getParentPos(pos),
                                MathHelper.floor_double(lightValue / 8));
            }
            if (chunk.getBlock((pos.getX() & 0x7f) >> 3,
                                        pos.getY() >> 3,
                                        (pos.getZ() & 0x7f) >> 3) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(BlockUtil.getParentPos(pos));
//                tile.setLightFor(BlockUtil.getLittlePos(pos), lightValue); TODO modification in TileEntityLittleChunk req
//                this.markBlockForUpdate(x >> 3, // TODO doesn't make sense
//                        y >> 3,
//                        z >> 3);
            }
        }
    }

    @Override
    public int getBlockLightOpacity(BlockPos pos) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getBlockLightOpacity(BlockUtil.getParentPos(pos));
        } else {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                            "getBlockLightValue(" + pos + ").[null]",
                            LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }



//    @Override TODO What's that?
//    public int getFullBlockLightValue(int x, int y, int z) {
//        return this.getParentWorld().getFullBlockLightValue(x >> 3,
//                                                            y >> 3,
//                                                            z >> 3);
//    }

    @Override
    public boolean canBlockSeeSky(BlockPos pos) {
        return this.getParentWorld().canBlockSeeSky(BlockUtil.getParentPos(pos));
    }

    @Override
    public void setInitialSpawnLocation() {
    }

    /**
     * Checks if the LittleWorld needs to be updated based on the parent
     * 'RealWorld'
     * 
     * @param world
     *            The world to check
     * 
     * @return outdated or not
     */
    public boolean isOutdated(World world) {
        boolean outdated = !this.getParentWorld().equals(world);
        if (outdated) {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                           "isOutDated(" + world.toString() + ").[" + outdated + "]",
                           LoggerLittleBlocks.LogLevel.DEBUG);
        }
        return outdated;
    }

//    @Override TODO What's that? = isBlockLoaded ?
//    public boolean blockExists(BlockPos pos) {
//        if (pos.getX() < 0xfe363c80 || pos.getZ() < 0xfe363c80 || pos.getX() >= 0x1c9c380 || pos.getZ() >= 0x1c9c380) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[Out of bounds]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return false;
//        }
//        if (pos.getY() < 0) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[y < 0]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return false;
//        }
//        if (pos.getY() >= this.getHeight()) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[y >= " + this
//                                   .getHeight() + "]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return false;
//        } else {
//            Block block = this.getParentWorld().getChunkFromChunkCoords(pos.getX() >> 7,
//                                                                        pos.getZ() >> 7)
//                    .getBlock((pos.getX() & 0x7f) >> 3,
//                              pos.getY() >> 3,
//                              (pos.getZ() & 0x7f) >> 3);
//            if (block.equals(ConfigurationLib.littleChunk)) {
//                TileEntityLittleChunk tile = (TileEntityLittleChunk) this
//                        .getParentWorld().getTileEntity(pos);
//                Block littleBlock = tile.getBlock(BlockUtil.getLittlePos(pos));
//                return littleBlock != Blocks.air ? true : false;
//            } else {
//                return false;
//            }
//        }
//    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (!this.isValid(pos)) {
            return Blocks.air.getDefaultState();
        } else {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            return chunk.getBlockState(pos);
        }
    }
    
    protected boolean isValid(BlockPos pos) {
        return pos.getX() > 0xfe363c80 &&
               pos.getZ() > 0xfe363c80 &&
               pos.getX() < 0x1c9c380 &&
               pos.getZ() < 0x1c9c380 &&
               pos.getY() >= 0 &&
               pos.getY() < this.getHeight();
    }



//    @Override TODO done in blockstate, right?
//    public Block getBlock(BlockPos pos) {
//        if (pos.getX() < 0xfe363c80 || pos.getZ() < 0xfe363c80 || pos.getX() >= 0x1c9c380 || pos.getZ() >= 0x1c9c380) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[Out of bounds]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return Blocks.air;
//        }
//        if (pos.getY() < 0) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[pos.getY() < 0]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return Blocks.air;
//        }
//        if (pos.getY() >= this.getHeight()) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockId(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ").[pos.getY() >= " + this
//                                   .getHeight() + "]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return Blocks.air;
//        } else {
//            Block id = this.getParentWorld().getChunkFromChunkCoords(pos.getX() >> 7,
//                                                                     pos.getZ() >> 7)
//                    .getBlock((pos.getX() & 0x7f) >> 3,
//                              pos.getY() >> 3,
//                              (pos.getZ() & 0x7f) >> 3);
//            if (id.equals(ConfigurationLib.littleChunk)) {
//                TileEntityLittleChunk tile = (TileEntityLittleChunk) this
//                        .getParentWorld().getTileEntity(new BlockPos(pos.getX() >> 3,
//                                                                    pos.getY() >> 3,
//                                                                    pos.getZ() >> 3));
//                Block littleBlock = tile.getBlock(pos.getX() & 7,
//                                                  pos.getY() & 7,
//                                                  pos.getZ() & 7);
//                return littleBlock;
//            } else {
//                return id;
//            }
//        }
//    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        entity.setPosition(entity.posX / ConfigurationLib.littleBlocksSize,
                           entity.posY / ConfigurationLib.littleBlocksSize,
                           entity.posZ / ConfigurationLib.littleBlocksSize);
        entity.motionX /= ConfigurationLib.littleBlocksSize;
        entity.motionY /= ConfigurationLib.littleBlocksSize;
        entity.motionZ /= ConfigurationLib.littleBlocksSize;
        entity.worldObj = this.getParentWorld();
        LoggerLittleBlocks
                .getInstance(Logger.filterClassName(this.getClass().toString()))
                .write(this.getParentWorld().isRemote,
                       "spawnEntityInWorld(" + entity.getEntityId() + ").[" + entity.posX + ", " + entity.posY + ", " + entity.posZ + "]",
                       LoggerLittleBlocks.LogLevel.DEBUG);
        return this.getParentWorld().spawnEntityInWorld(entity);
    }

    @Override
    public int getHeight() {
        return super.getHeight() * ConfigurationLib.littleBlocksSize;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (!this.isValid(pos)) {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                            "setBlock("+pos+" "+newState+" "+flags+").[Pos invalid]",
                            LoggerLittleBlocks.LogLevel.DEBUG);
            return false;
        } else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        } else {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            Block block = newState.getBlock();

//            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null; What is that? TODO
//            if (this.captureBlockSnapshots && !this.isRemote) {
//                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(this, pos, flags);
//                this.capturedBlockSnapshots.add(blockSnapshot);
//            }
            IBlockState currentState = chunk.getBlockState(pos);
            if(currentState.getBlock().getClass() != BlockLittleChunk.class) {
                this.getParentWorld().setBlockState(LittleBlocks.scaleDownPos(pos), BlockLittleChunk.getStateById(0)); // TODO Is that right?
            }
            IBlockState state = chunk.setBlockState(pos, newState);

            if (state == null) {
                return false;
            } else {
                Block block1 = state.getBlock();

                if (block.getLightOpacity() != block1.getLightOpacity() || block.getLightValue() != block1.getLightValue()) {
                    this.theProfiler.startSection("checkLight");
                    this.checkLight(pos);
                    this.theProfiler.endSection();
                }

//                if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
                this.markAndNotifyBlock(pos, chunk, state, newState, flags); // Modularize client and physic updates
//                }

                return true;
            }
        }
        /// OLD
//        if (isValidPos(pos)) {
//            if (pos.getY() < 0) {
//                LoggerLittleBlocks
//                        .getInstance(Logger.filterClassName(this.getClass()
//                                .toString()))
//                        .write(this.getParentWorld().isRemote,
//                               "setBlock(" + x + ", " + y + ", " + z + ", " + block
//                                       .getLocalizedName() + ", " + newmeta + ", " + update + ").[y < 0]",
//                               LoggerLittleBlocks.LogLevel.DEBUG);
//                return false;
//            } else if (y >= this.getHeight()) {
//                LoggerLittleBlocks
//                        .getInstance(Logger.filterClassName(this.getClass()
//                                .toString()))
//                        .write(this.getParentWorld().isRemote,
//                               "setBlock(" + x + ", " + y + ", " + z + ", " + block
//                                       .getLocalizedName() + ", " + newmeta + ", " + update + ").[y >= " + this
//                                       .getHeight() + "]",
//                               LoggerLittleBlocks.LogLevel.DEBUG);
//                return false;
//            } else {
//                Chunk chunk = this.getParentWorld()
//                        .getChunkFromChunkCoords(x >> 7,
//                                                 z >> 7);
//                if (chunk.getBlock((x & 0x7f) >> 3,
//                                   y >> 3,
//                                   (z & 0x7f) >> 3) != ConfigurationLib.littleChunk && this
//                        .isAirBlock(x,
//                                    y,
//                                    z)) {
//                    this.getParentWorld()
//                            .setBlock(x >> 3,
//                                      y >> 3,
//                                      z >> 3,
//                                      ConfigurationLib.littleChunk);
//                } else if (chunk.getBlock((x & 0x7f) >> 3,
//                                          y >> 3,
//                                          (z & 0x7f) >> 3) != ConfigurationLib.littleChunk) {
//                    return false;
//                }
//                if (!this.isRemote) {
//                    TileEntityLittleChunk tile = (TileEntityLittleChunk) this
//                            .getParentWorld().getTileEntity(x >> 3,
//                                                            y >> 3,
//                                                            z >> 3);
//                    Block originalId = null;
//
//                    if ((update & 1) != 0) {
//                        originalId = tile.getBlock(x & 7,
//                                                   y & 7,
//                                                   z & 7);
//                    }
//
//                    boolean flag = tile.setBlockIDWithMetadata(x & 7,
//                                                               y & 7,
//                                                               z & 7,
//                                                               block,
//                                                               newmeta);
//
//                    this.func_147451_t/* updateAllLightTypes */(x,
//                                                                y,
//                                                                z);
//
//                    if (flag) {
//                        if ((update & 2) != 0 && ((update & 4) == 0)) {
//                            this.markBlockForUpdate(x,
//                                                    y,
//                                                    z);
//                        }
//
//                        if ((update & 1) != 0) {
//                            this.notifyBlockChange(x,
//                                                   y,
//                                                   z,
//                                                   originalId);
//
//                            if (block != null && block
//                                    .hasComparatorInputOverride()) {
//                                this.func_147453_f(x,
//                                                   y,
//                                                   z,
//                                                   block);
//                            }
//                        }
//                    }
//                    return flag;
//                }
//            }
//        } else {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "setBlock(" + x + ", " + y + ", " + z + ", " + block
//                                   .getLocalizedName() + ", " + newmeta + ", " + update + ").[" + block
//                                   .getLocalizedName() + ", " + newmeta + "]:No Change",
//                           LoggerLittleBlocks.LogLevel.ERROR);
//        }
//        return false;
    }



//    @Override TODO ?
//    public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
//        int xDiff = (maxX - minX) >> 1, yDiff = (maxY - minY) >> 1, zDiff = (maxZ - minZ) >> 1;
//        int xMid = (minX + maxX) >> 1, yMid = (minY + maxY) >> 1, zMid = (minZ + maxZ) >> 1;
//
//        boolean flag = this.getParentWorld()
//                .checkChunksExist((xMid >> 3) - xDiff,
//                                  (yMid >> 3) - yDiff,
//                                  (zMid >> 3) - zDiff,
//                                  (xMid >> 3) + xDiff,
//                                  (yMid >> 3) + yDiff,
//                                  (zMid >> 3) + zDiff);
//
//        return flag;
//    }


    @Override
    public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn) {
        World world;
        IBlockState state = this.getBlockState(pos);
        if(state.getBlock().getClass() != BlockLittleChunk.class) {
            pos = BlockUtil.getParentPos(pos);
            world = this.getParentWorld();
        } else {
            world = this;
        }
        if (!this.isRemote) {
            try {
                state.getBlock().onNeighborBlockChange(this, pos, state, blockIn);
            } catch (Throwable throwable) {
                LoggerLittleBlocks
                        .getInstance(Logger.filterClassName(this.getClass()
                                .toString()))
                        .write(this.getParentWorld().isRemote,
                                throwable.getMessage()+" onNeighborBlockChange(" + pos + ", " + blockIn + ").[" + state + "]",
                                LogLevel.ERROR);
            }
        }
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        if(!isValid(pos)) {
            IBlockState state = this.getParentWorld().getBlockState(BlockUtil.getParentPos(pos));
            TileEntity tile = this.getParentWorld().getTileEntity(BlockUtil.getParentPos(pos));
            if(state.getBlock().getClass() == BlockLittleChunk.class) {
                TileEntityLittleChunk tileLc = (TileEntityLittleChunk) tile;
                return tileLc.getTileEntity(BlockUtil.getLittlePos(pos), Chunk.EnumCreateEntityType.IMMEDIATE);
            } else {
                return tile;
            }
        }
        return null;
    }

//    @Override OLD
//    public TileEntity getTileEntity(int x, int y, int z) {
//        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getTileEntity(" + x + ", " + y + ", " + z + ").[Out of bounds]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return null;
//        }
//        if (y < 0) {
//            LoggerLittleBlocks
//                    .getInstance(Logger.filterClassName(this.getClass()
//                            .toString()))
//                    .write(this.getParentWorld().isRemote,
//                           "getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
//                           LoggerLittleBlocks.LogLevel.DEBUG);
//            return null;
//        }
//        if (y >= this.getHeight()) {
//            return null;
//        } else {
//            Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
//                                                                        z >> 7);
//            if (chunk.getBlock((x & 0x7f) >> 3,
//                               y >> 3,
//                               (z & 0x7f) >> 3) == ConfigurationLib.littleChunk) {
//                TileEntity tileentity = null;
//                int l;
//                TileEntity tileentity1;
//
//                if (this.field_147481_N) {
//                    for (l = 0; l < this.addedTileEntityList.size(); ++l) {
//                        tileentity1 = (TileEntity) this.addedTileEntityList
//                                .get(l);
//
//                        if (!tileentity1.isInvalid() && tileentity1.xCoord == x && tileentity1.yCoord == y && tileentity1.zCoord == z) {
//                            tileentity = tileentity1;
//                            break;
//                        }
//                    }
//                }
//
//                if (tileentity == null) {
//                    TileEntityLittleChunk tile = (TileEntityLittleChunk) this
//                            .getParentWorld().getTileEntity(x >> 3,
//                                                            y >> 3,
//                                                            z >> 3);
//                    if (tile != null) {
//                        tileentity = tile.getTileEntity(x & 7,
//                                y & 7,
//                                z & 7);
//                    }
//                }
//
//                if (tileentity == null) {
//                    for (l = 0; l < this.addedTileEntityList.size(); ++l) {
//                        tileentity1 = (TileEntity) this.addedTileEntityList
//                                .get(l);
//
//                        if (!tileentity1.isInvalid() && tileentity1.xCoord == x && tileentity1.yCoord == y && tileentity1.zCoord == z) {
//                            tileentity = tileentity1;
//                            break;
//                        }
//                    }
//                }
//
//                return tileentity;
//            } else {
//                return this.getParentWorld().getTileEntity(x >> 3,
//                                                           y >> 3,
//                                                           z >> 3);
//            }
//        }
//    }


    @Override
    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
            IBlockState state = this.getParentWorld().getBlockState(BlockUtil.getParentPos(pos));
            if(state.getBlock().getClass() == BlockLittleChunk.class) {
                if (this.processingLoadedTiles) {
                    tileEntityIn.setPos(pos);
                    Iterator iterator = this.addedTileEntityList.iterator();

                    while (iterator.hasNext()) {
                        TileEntity tileentity1 = (TileEntity)iterator.next();

                        if (tileentity1.getPos().equals(pos)) {
                            tileentity1.invalidate();
                            iterator.remove();
                        }
                    }

                    this.addedTileEntityList.add(tileEntityIn);
                } else {
                    this.addTileEntity(tileEntityIn);
                    TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(BlockUtil.getParentPos(pos));
                    if (tile != null) tile.addTileEntity(pos, tileEntityIn);
                }
            } else {
                this.getParentWorld().setTileEntity(BlockUtil.getParentPos(pos), tileEntityIn);
            }
        }
    }

    @Override
    public boolean addTileEntity(TileEntity tile) {
        List dest = processingLoadedTiles ? addedTileEntityList : loadedTileEntityList;
        boolean flag = dest.add(tile);

        if (flag && tile instanceof IUpdatePlayerListBox) {
            this.tickableTileEntities.add(tile);
        }

        return flag;
    }

    public void addTileEntities(Collection tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            Iterator iterator = tileEntityCollection.iterator();

            while (iterator.hasNext()) {
                TileEntity tileentity = (TileEntity)iterator.next();
                this.loadedTileEntityList.add(tileentity);

                if (tileentity instanceof IUpdatePlayerListBox) {
                    this.tickableTileEntities.add(tileentity);
                }
            }
        }
    }

    @Override
    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.tileRemoval.add(tileEntityIn);
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);

        if (tileentity != null && this.processingLoadedTiles)
        {
            tileentity.invalidate();
            this.addedTileEntityList.remove(tileentity);
            if (!(tileentity instanceof IUpdatePlayerListBox)) //Forge: If they are not tickable they wont be removed in the update loop.
                this.loadedTileEntityList.remove(tileentity);

        }
        else
        {
            if (tileentity != null)
            {
                this.addedTileEntityList.remove(tileentity);
                this.loadedTileEntityList.remove(tileentity);
                this.tickableTileEntities.remove(tileentity);
            }
            TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(BlockUtil.getParentPos(pos));
            tile.removeTileEntity(BlockUtil.getLittlePos(pos));
        }
        this.updateComparatorOutputLevel(pos, getBlockState(pos).getBlock()); //Notify neighbors of changes
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        if (!this.isValid(pos)) return _default;
//        Chunk chunk = getChunkFromBlockCoords(pos); TODO ?
//        if (chunk == null || chunk.isEmpty()) return _default;
        return getBlockState(pos).getBlock().isSideSolid(this, pos, side);
    }

    @Override
    public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
        this.getParentWorld()
                .playSoundEffect(x / ConfigurationLib.littleBlocksSize,
                        y / ConfigurationLib.littleBlocksSize,
                        z / ConfigurationLib.littleBlocksSize,
                        s,
                        f,
                        f1);
    }

    @Override
    public void playRecord(BlockPos pos, String s) {
        super.playRecord(BlockUtil.getParentPos(pos), s);
    }

    @Override
    public void playAuxSFX(int a, BlockPos pos, int b) {
        super.playAuxSFX(a, BlockUtil.getParentPos(pos), b);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... array) {
        super.spawnParticle(particleType,
                xCoord / ConfigurationLib.littleBlocksSize,
                yCoord / ConfigurationLib.littleBlocksSize,
                zCoord / ConfigurationLib.littleBlocksSize, xOffset, yOffset, zOffset, array);
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(Vec3 u, Vec3 v, boolean a, boolean b, boolean c) {
        Vec3 u1 = new Vec3( u.xCoord * ConfigurationLib.littleBlocksSize,
                            u.yCoord * ConfigurationLib.littleBlocksSize,
                            u.zCoord * ConfigurationLib.littleBlocksSize);
        Vec3 v1 = new Vec3( v.xCoord * ConfigurationLib.littleBlocksSize,
                            v.yCoord * ConfigurationLib.littleBlocksSize,
                            v.zCoord * ConfigurationLib.littleBlocksSize);
        return super.rayTraceBlocks(u1, v1, a, b, c);
    }

    @Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8, isFlaming, isSmoking);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new LittleChunkProvider(this);
    }

    @Override
    public Entity getEntityByID(int entityId) {
        return null;
    }

    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        return null;
    }



//    @Override TODO ??
//    public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
//        return this.getClosestPlayer(x,
//                                     y,
//                                     z,
//                                     distance);
//    }

    @Override
    public World getParentWorld() {
        return LittleBlocks.proxy.getParentWorld(this,
                                                 this.parentDimension);
    }

    @Override
    public void markBlockForUpdate(BlockPos pos) {
        this.getParentWorld().markBlockForUpdate(BlockUtil.getParentPos(pos));
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
        this.getParentWorld().markBlockRangeForRenderUpdate(x >> 3,
                                                            y >> 3,
                                                            z >> 3,
                                                            x2 >> 3,
                                                            y2 >> 3,
                                                            z2 >> 3);
    }



//    @Override TODO ??
//    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
//        TileEntity tile = this.getParentWorld().getTileEntity(x >> 3,
//                                                              y >> 3,
//                                                              z >> 3);
//        if (tile != null && tile instanceof TileEntityLittleChunk) {
//            tile.markDirty();
//        }
//    }

    private int computeLightValue(int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky && this
                .canBlockSeeTheSky(par1,
                                   par2,
                                   par3)) {
            return 15;
        } else {
            Block block = this.getBlock(par1,
                                        par2,
                                        par3);
            int blockLight = (block == null ? 0 : block.getLightValue(this,
                                                                      par1,
                                                                      par2,
                                                                      par3));
            int i1 = par4EnumSkyBlock == EnumSkyBlock.Sky ? 0 : blockLight;
            int j1 = (block == null ? 0 : block.getLightOpacity(this,
                                                                par1,
                                                                par2,
                                                                par3));

            if (j1 >= 15 && blockLight > 0) {
                j1 = 1;
            }

            if (j1 < 1) {
                j1 = 1;
            }

            if (j1 >= 15) {
                return 0;
            } else if (i1 >= 14) {
                return i1;
            } else {
                for (int k1 = 0; k1 < 6; ++k1) {
                    int l1 = par1 + Facing.offsetsXForSide[k1];
                    int i2 = par2 + Facing.offsetsYForSide[k1];
                    int j2 = par3 + Facing.offsetsZForSide[k1];
                    int k2 = this.getSavedLightValue(par4EnumSkyBlock,
                                                     l1,
                                                     i2,
                                                     j2) - j1;

                    if (k2 > i1) {
                        i1 = k2;
                    }

                    if (i1 >= 14) {
                        return i1;
                    }
                }

                return i1;
            }
        }
    }

    int[] lightUpdateBlockList;

    public int getSavedLightValue(EnumSkyBlock enumskyblock, int x, int y, int z) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                           "getBlockMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
                           LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y < 0) {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                           "getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
                           LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y >= this.getHeight()) {
            LoggerLittleBlocks
                    .getInstance(Logger.filterClassName(this.getClass()
                            .toString()))
                    .write(this.getParentWorld().isRemote,
                           "getBlockMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this
                                   .getHeight() + "]",
                           LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        } else {
            Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                     z >> 7)
                    .getBlock((x & 0x7f) >> 3,
                              y >> 3,
                              (z & 0x7f) >> 3);
            int metadata = this.getParentWorld()
                    .getChunkFromChunkCoords(x >> 7,
                                             z >> 7)
                    .getBlockMetadata((x & 0x7f) >> 3,
                                      y >> 3,
                                      (z & 0x7f) >> 3);
            if (id == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this
                        .getParentWorld().getTileEntity(x >> 3,
                                                        y >> 3,
                                                        z >> 3);
                return tile.getSavedLightValue(x & 7,
                                               y & 7,
                                               z & 7);
            } else {
                return enumskyblock.defaultLightValue;
            }
        }
    }

    // TODO :: Lighting
    @Override
    public boolean updateLightByType(EnumSkyBlock enumskyblock, int x, int y, int z) {
        // this.getRealWorld().updateLightByType(enumSkyBlock,
        // x >> 3,
        // y >> 3,
        // z >> 3);
        if (this.doChunksNearChunkExist(x,
                                        y,
                                        z,
                                        17)) {
            int lightCount = 0;
            int changeCount = 0;
            int savedLight = this.getSavedLightValue(enumskyblock,
                                                     x,
                                                     y,
                                                     z);
            int computedLight = this.computeLightValue(x,
                                                       y,
                                                       z,
                                                       enumskyblock);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;

            if (computedLight > savedLight) {
                this.lightUpdateBlockList[changeCount++] = 133152;
            } else if (computedLight < savedLight) {
                this.lightUpdateBlockList[changeCount++] = 133152 | savedLight << 18;

                while (lightCount < changeCount) {
                    l1 = this.lightUpdateBlockList[lightCount++];
                    i2 = (l1 & 63) - 32 + x;
                    j2 = (l1 >> 6 & 63) - 32 + y;
                    k2 = (l1 >> 12 & 63) - 32 + z;
                    l2 = l1 >> 18 & 15;
                    i3 = this.getSavedLightValue(enumskyblock,
                                                 i2,
                                                 j2,
                                                 k2);

                    if (i3 == l2) {
                        this.setLightValue(enumskyblock,
                                           i2,
                                           j2,
                                           k2,
                                           0);

                        if (l2 > 0) {
                            j3 = MathHelper.abs_int(i2 - x);
                            l3 = MathHelper.abs_int(j2 - y);
                            k3 = MathHelper.abs_int(k2 - z);

                            if (j3 + l3 + k3 < 17) {
                                for (int i4 = 0; i4 < 6; ++i4) {
                                    int j4 = i2 + Facing.offsetsXForSide[i4];
                                    int k4 = j2 + Facing.offsetsYForSide[i4];
                                    int l4 = k2 + Facing.offsetsZForSide[i4];
                                    Block block = this.getBlock(j4,
                                                                k4,
                                                                l4);
                                    int blockOpacity = (block == null ? 0 : block
                                            .getLightOpacity(this,
                                                             j4,
                                                             k4,
                                                             l4));
                                    int i5 = Math.max(1,
                                                      blockOpacity);
                                    i3 = this.getSavedLightValue(enumskyblock,
                                                                 j4,
                                                                 k4,
                                                                 l4);

                                    if (i3 == l2 - i5 && changeCount < this.lightUpdateBlockList.length) {
                                        this.lightUpdateBlockList[changeCount++] = j4 - x + 32 | k4 - y + 32 << 6 | l4 - z + 32 << 12 | l2 - i5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                lightCount = 0;
            }

            while (lightCount < changeCount) {
                l1 = this.lightUpdateBlockList[lightCount++];
                i2 = (l1 & 63) - 32 + x;
                j2 = (l1 >> 6 & 63) - 32 + y;
                k2 = (l1 >> 12 & 63) - 32 + z;
                l2 = this.getSavedLightValue(enumskyblock,
                                             i2,
                                             j2,
                                             k2);
                i3 = this.computeLightValue(i2,
                                            j2,
                                            k2,
                                            enumskyblock);

                if (i3 != l2) {
                    this.setLightValue(enumskyblock,
                                       i2,
                                       j2,
                                       k2,
                                       i3);

                    if (i3 > l2) {
                        j3 = Math.abs(i2 - x);
                        l3 = Math.abs(j2 - y);
                        k3 = Math.abs(k2 - z);
                        boolean flag = changeCount < this.lightUpdateBlockList.length - 6;

                        if (j3 + l3 + k3 < 17 && flag) {
                            if (this.getSavedLightValue(enumskyblock,
                                                        i2 - 1,
                                                        j2,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 - 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2 + 1,
                                                        j2,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 + 1 - x + 32 + (j2 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2 - 1,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 - x + 32 + (j2 - 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2 + 1,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 - x + 32 + (j2 + 1 - y + 32 << 6) + (k2 - z + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2,
                                                        k2 - 1) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 - 1 - z + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2,
                                                        k2 + 1) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2 - x + 32 + (j2 - y + 32 << 6) + (k2 + 1 - z + 32 << 12);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
        ArrayList arraylist = new ArrayList();
        int minX = MathHelper
                .floor_double((axisalignedbb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxX = MathHelper
                .floor_double((axisalignedbb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int minZ = MathHelper
                .floor_double((axisalignedbb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxZ = MathHelper
                .floor_double((axisalignedbb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                // if (this.chunkExists( x,
                // z)) {
                // this.getChunkFromChunkCoords( x,
                // z).getEntitiesWithinAABBForEntity( entity,
                // axisalignedbb,
                // arraylist,
                // entitySelector);
                // }
            }
        }

        return arraylist;
    }

    @Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb) {
        return this
                .getEntitiesWithinAABBExcludingEntity(entity,
                                                      axisalignedbb,
                                                      (IEntitySelector) null);
    }

    @Override
    public List selectEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
        ArrayList arraylist = new ArrayList();
        int minX = MathHelper
                .floor_double((axisalignedbb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxX = MathHelper
                .floor_double((axisalignedbb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int minZ = MathHelper
                .floor_double((axisalignedbb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxZ = MathHelper
                .floor_double((axisalignedbb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                // if (this.chunkExists( x,
                // z)) {
                // this.getChunkFromChunkCoords( x,
                // z).getEntitiesOfTypeWithinAAAB(entityClass,
                // axisalignedbb,
                // arraylist,
                // entitySelector);
                // }
            }
        }

        return arraylist;
    }

    @Override
    public List getEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisAlignedBB) {
        return this.selectEntitiesWithinAABB(entityClass,
                                             axisAlignedBB,
                                             (IEntitySelector) null);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB axisalignedbb, Entity entity) {
        List list = this.getEntitiesWithinAABBExcludingEntity((Entity) null,
                                                              axisalignedbb);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (!entity1.isDead && entity1.preventEntitySpawning && entity1 != entity) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canPlaceEntityOnSide(Block block, int x, int y, int z, boolean flag, int side, Entity entityPlacing, ItemStack itemstack) {
        Block blockAt = this.getBlock(x,
                                      y,
                                      z);
        AxisAlignedBB axisalignedbb = block
                .getCollisionBoundingBoxFromPool(this,
                                                 x,
                                                 y,
                                                 z);

        if (flag) {
            axisalignedbb = null;
        }

        if (axisalignedbb != null && !this
                .checkNoEntityCollision(axisalignedbb,
                                        entityPlacing)) {
            return false;
        } else {
            if (blockAt != null && (blockAt == Blocks.flowing_water || blockAt == Blocks.water || blockAt == Blocks.flowing_lava || blockAt == Blocks.lava || blockAt == Blocks.fire || blockAt
                    .getMaterial().isReplaceable())) {
                blockAt = null;
            }

            if (blockAt != null && blockAt.isReplaceable(this,
                                                         x,
                                                         y,
                                                         z)) {
                blockAt = null;
            }

            return blockAt != null && blockAt.getMaterial() == Material.circuits && block == Blocks.anvil ? true : block != Blocks.air && blockAt == null && block
                    .canPlaceBlockOnSide(this,
                                         x,
                                         y,
                                         z,
                                         side);
        }
    }

    @Override
    public boolean isOutSideLittleWorld(int x, int y, int z) {
        Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                 z >> 7)
                .getBlock((x & 0x7f) >> 3,
                          y >> 3,
                          (z & 0x7f) >> 3);
        return id != ConfigurationLib.littleChunk;
    }

    @Override
    public void activeChunkPosition(ChunkPosition chunkposition, boolean forced) {
        if (this.activeChunkPosition.contains(chunkposition)) {
            this.activeChunkPosition.remove(chunkposition);
        }
        if (forced) {
            this.activeChunkPosition.add(chunkposition);
        }
    }

    @Override
    public List<TileEntity> getLoadedTileEntities() {
        return this.loadedTileEntityList;
    }

    @Override
    public void addLoadedTileEntity(TileEntity tileentity) {
        this.addTileEntity(tileentity);
    }
}
