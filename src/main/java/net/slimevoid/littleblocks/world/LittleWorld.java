package net.slimevoid.littleblocks.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.util.ForgeDirection;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.library.data.Logger;
import net.slimevoid.library.data.Logger.LogLevel;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import com.google.common.collect.ImmutableSetMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class LittleWorld extends World implements ILittleWorld {

    private List               addedTileEntityList             = new ArrayList<TileEntity>();
    private boolean           /* scanningTiles */field_147481_N;

    /** Entities marked for removal. */
    private List              /* tileRemoval */field_147483_b = new ArrayList();

    protected int              ticksInWorld                    = 0;
    protected static final int MAX_TICKS_IN_WORLD              = 5;
    public Set<ChunkPosition>  activeChunkPosition             = new HashSet<ChunkPosition>();

    private int                parentDimension;

    @SideOnly(Side.CLIENT)
    public LittleWorld(World world, WorldProvider worldprovider, String worldName) {
        super(world.getSaveHandler(), worldName, worldprovider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), null);
        this.lightUpdateBlockList = new int[32768];
        this.parentDimension = world.provider.dimensionId;
        this.isRemote = true;
    }

    public LittleWorld(World world, WorldProvider worldprovider) {
        super(world.getSaveHandler(), "LittleBlocksWorld", new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), worldprovider, null);
        this.lightUpdateBlockList = new int[32768];
        this.parentDimension = world.provider.dimensionId;
        this.isRemote = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.func_82738_a(this.getTotalWorldTime() + 1L);
        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }
        this.func_147456_g()/* tickBlocksAndAmbiance() */;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    @Override
    public boolean tickUpdates(boolean tick) {
        return false;
    }

    private final Set previousActiveChunkSet = new HashSet();

    @Override
    protected void func_147456_g()/* tickBlocksAndAmbiance() */{
        this.setActivePlayerChunksAndCheckLight();
    }

    @Override
    public ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
        return this.getParentWorld().getPersistentChunks();
    }

    @Override
    protected void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.activeChunkSet.addAll(this.getParentWorld().getPersistentChunks().keySet());
        int i;
        EntityPlayer entityplayer;
        int j;
        int k;

        for (i = 0; i < this.getParentWorld().playerEntities.size(); ++i) {
            entityplayer = (EntityPlayer) this.getParentWorld().playerEntities.get(i);
            j = MathHelper.floor_double(entityplayer.posX / 16.0D);
            k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
            byte b0 = 7;

            for (int l = -b0; l <= b0; ++l) {
                for (int i1 = -b0; i1 <= b0; ++i1) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(l + j, i1 + k));
                }
            }
        }
    }

    @Override
    public void updateEntities() {
        this.field_147481_N = true;
        Iterator<TileEntity> iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = iterator.next();

            if (!tileentity.isInvalid() && tileentity.hasWorldObj()
                && this.blockExists(tileentity.xCoord,
                                    tileentity.yCoord,
                                    tileentity.zCoord)) {
                try {
                    tileentity.updateEntity();
                } catch (Throwable t) {
                    SlimevoidCore.console(CoreLib.MOD_ID,
                                          t.getLocalizedMessage(),
                                          LogLevel.WARNING.ordinal());
                    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                             "updateEntities("
                                                                                                                     + tileentity.toString()
                                                                                                                     + ", "
                                                                                                                     + tileentity.xCoord
                                                                                                                     + ", "
                                                                                                                     + tileentity.yCoord
                                                                                                                     + ", "
                                                                                                                     + tileentity.zCoord
                                                                                                                     + ").["
                                                                                                                     + t.getLocalizedMessage()
                                                                                                                     + "]",
                                                                                                             LoggerLittleBlocks.LogLevel.DEBUG);
                }
            }

            if (tileentity.isInvalid()) {
                iterator.remove();

                TileEntity tileentitylb = this.getParentWorld().getTileEntity(tileentity.xCoord >> 3,
                                                                              tileentity.yCoord >> 3,
                                                                              tileentity.zCoord >> 3);
                if (tileentitylb != null
                    && tileentitylb instanceof TileEntityLittleChunk) {
                    ((TileEntityLittleChunk) tileentitylb).removeInvalidTileEntity(tileentity.xCoord & 7,
                                                                                   tileentity.yCoord & 7,
                                                                                   tileentity.zCoord & 7);
                }
            }
        }

        if (!this.field_147483_b.isEmpty()) {
            for (Object tile : field_147483_b) {
                ((TileEntity) tile).onChunkUnload();
            }
            this.loadedTileEntityList.removeAll(this.field_147483_b);
            this.field_147483_b.clear();
        }

        this.field_147481_N = false;

        if (!this.addedTileEntityList.isEmpty()) {
            for (int i = 0; i < this.addedTileEntityList.size(); ++i) {
                TileEntity tileentity = (TileEntity) this.addedTileEntityList.get(i);

                if (!tileentity.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity)) {
                        this.loadedTileEntityList.add(tileentity);
                    }
                } else {
                    TileEntity tileentitylb = this.getParentWorld().getTileEntity(tileentity.xCoord >> 3,
                                                                                  tileentity.yCoord >> 3,
                                                                                  tileentity.zCoord >> 3);
                    if (tileentitylb != null
                        && tileentitylb instanceof TileEntityLittleChunk) {
                        ((TileEntityLittleChunk) tileentitylb).removeInvalidTileEntity(tileentity.xCoord & 7,
                                                                                       tileentity.yCoord & 7,
                                                                                       tileentity.zCoord & 7);
                    }
                }
            }

            this.addedTileEntityList.clear();
        }
    }

    @Override
    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getSkyBlockTypeBrightness(enumskyblock,
                                                                   x >> 3,
                                                                   y >> 3,
                                                                   z >> 3);
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getSkyBlockTypeBrightness("
                                                                                                             + enumskyblock
                                                                                                             + ", "
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[null]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }

    @Override
    public long getWorldTime() {
        //if (this.getParentWorld() != null) {
        //    return this.getParentWorld().provider.getWorldTime();
        //} else {
        //    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
        //                                                                                             "getWorldTime().[null]",
        //                                                                                             LoggerLittleBlocks.LogLevel.DEBUG);
            return super.getWorldTime();
        //}
    }

    @Override
    public long getTotalWorldTime() {
        //if (this.getParentWorld() != null) {
        //    return this.getParentWorld().getWorldInfo().getWorldTotalTime();
        //} else {
        //    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
        //                                                                                             "getTotalWorldTime().[null]",
        //                                                                                             LoggerLittleBlocks.LogLevel.DEBUG);
            return super.getTotalWorldTime();
        //}
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getLightBrightnessForSkyBlocks(x >> 3,
                                                                        y >> 3,
                                                                        z >> 3,
                                                                        l);
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getLightBrightnessForSkyBlocks("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").["
                                                                                                             + l
                                                                                                             + "]:Null",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getLightBrightness(int x, int y, int z) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getLightBrightness(x >> 3,
                                                            y >> 3,
                                                            z >> 3);
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBrightness().[null]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }

    @Override
    public void setLightValue(EnumSkyBlock sky, int x, int y, int z, int value) {
        if (x >= 0xfe363c80 && z >= 0xfe363c80 && x < 0x1c9c380
            && z < 0x1c9c380) {
            if (y >= 0) {
                if (y >= this.getHeight()) {
                    Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                                z >> 7);
                    if (chunk.getBlock((x & 0x7f) >> 3,
                                       y >> 3,
                                       (z & 0x7f) >> 3) != ConfigurationLib.littleChunk
                        && this.isAirBlock(x,
                                           y,
                                           z)) {
                        this.getParentWorld().setLightValue(sky,
                                                            x >> 3,
                                                            y >> 3,
                                                            z >> 3,
                                                            MathHelper.floor_double(value / 8));
                    } else if (chunk.getBlock((x & 0x7f) >> 3,
                                              y >> 3,
                                              (z & 0x7f) >> 3) != ConfigurationLib.littleChunk) {
                        return;
                    }
                    TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                             y >> 3,
                                                                                                             z >> 3);
                    tile.setLightValue(x & 7,
                                       y & 7,
                                       z & 7,
                                       value);
                    this.markBlockForUpdate(x >> 3,
                                            y >> 3,
                                            z >> 3);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockLightValue(int x, int y, int z) {
        if (this.getParentWorld() != null) {
            return this.getParentWorld().getBlockLightValue(x >> 3,
                                                            y >> 3,
                                                            z >> 3);
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockLightValue("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[null]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
    }

    @Override
    public int getFullBlockLightValue(int x, int y, int z) {
        return this.getParentWorld().getFullBlockLightValue(x >> 3,
                                                            y >> 3,
                                                            z >> 3);
    }

    @Override
    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return this.getParentWorld().canBlockSeeTheSky(x >> 3,
                                                       y >> 3,
                                                       z >> 3);
    }

    @Override
    public void setSpawnLocation() {
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
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "isOutDated("
                                                                                                             + world.toString()
                                                                                                             + ").["
                                                                                                             + outdated
                                                                                                             + "]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
        }
        return outdated;
    }

    @Override
    public boolean blockExists(int x, int y, int z) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[Out of bounds]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return false;
        }
        if (y < 0) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y < 0]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return false;
        }
        if (y >= this.getHeight()) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y >= "
                                                                                                             + this.getHeight()
                                                                                                             + "]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return false;
        } else {
            Block block = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                        z >> 7).getBlock((x & 0x7f) >> 3,
                                                                                         y >> 3,
                                                                                         (z & 0x7f) >> 3);
            if (block.equals(ConfigurationLib.littleChunk)) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                         y >> 3,
                                                                                                         z >> 3);
                Block littleBlock = tile.getBlock(x & 7,
                                                  y & 7,
                                                  z & 7);
                return littleBlock != Blocks.air ? true : false;
            } else {
                return false;
            }
        }
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[Out of bounds]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return Blocks.air;
        }
        if (y < 0) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y < 0]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return Blocks.air;
        }
        if (y >= this.getHeight()) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockId("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y >= "
                                                                                                             + this.getHeight()
                                                                                                             + "]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return Blocks.air;
        } else {
            Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                     z >> 7).getBlock((x & 0x7f) >> 3,
                                                                                      y >> 3,
                                                                                      (z & 0x7f) >> 3);
            if (id.equals(ConfigurationLib.littleChunk)) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                         y >> 3,
                                                                                                         z >> 3);
                Block littleBlock = tile.getBlock(x & 7,
                                                  y & 7,
                                                  z & 7);
                return littleBlock;
            } else {
                return id;
            }
        }
    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        entity.setPosition(entity.posX / ConfigurationLib.littleBlocksSize,
                           entity.posY / ConfigurationLib.littleBlocksSize,
                           entity.posZ / ConfigurationLib.littleBlocksSize);
        entity.motionX /= ConfigurationLib.littleBlocksSize;
        entity.motionY /= ConfigurationLib.littleBlocksSize;
        entity.motionZ /= ConfigurationLib.littleBlocksSize;
        entity.worldObj = this.getParentWorld();
        LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                 "spawnEntityInWorld("
                                                                                                         + entity.getEntityId()
                                                                                                         + ").["
                                                                                                         + entity.posX
                                                                                                         + ", "
                                                                                                         + entity.posY
                                                                                                         + ", "
                                                                                                         + entity.posZ
                                                                                                         + "]",
                                                                                                 LoggerLittleBlocks.LogLevel.DEBUG);
        return this.getParentWorld().spawnEntityInWorld(entity);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[Out of bounds]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y < 0) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y < 0]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y >= this.getHeight()) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y >= "
                                                                                                             + this.getHeight()
                                                                                                             + "]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        } else {
            Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                     z >> 7).getBlock((x & 0x7f) >> 3,
                                                                                      y >> 3,
                                                                                      (z & 0x7f) >> 3);
            int metadata = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                         z >> 7).getBlockMetadata((x & 0x7f) >> 3,
                                                                                                  y >> 3,
                                                                                                  (z & 0x7f) >> 3);
            if (id.equals(ConfigurationLib.littleChunk)) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                         y >> 3,
                                                                                                         z >> 3);
                int littleMetaData = tile.getBlockMetadata(x & 7,
                                                           y & 7,
                                                           z & 7);
                return littleMetaData & 15;
            } else {
                return metadata;
            }
        }
    }

    @Override
    public int getHeight() {
        return super.getHeight() * ConfigurationLib.littleBlocksSize;
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block, int newmeta, int update) {
        if (x >= 0xfe363c80 && z >= 0xfe363c80 & x < 0x1c9c380 && z < 0x1c9c380) {
            if (y < 0) {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                         "setBlock("
                                                                                                                 + x
                                                                                                                 + ", "
                                                                                                                 + y
                                                                                                                 + ", "
                                                                                                                 + z
                                                                                                                 + ", "
                                                                                                                 + block.getLocalizedName()
                                                                                                                 + ", "
                                                                                                                 + newmeta
                                                                                                                 + ", "
                                                                                                                 + update
                                                                                                                 + ").[y < 0]",
                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
                return false;
            } else if (y >= this.getHeight()) {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                         "setBlock("
                                                                                                                 + x
                                                                                                                 + ", "
                                                                                                                 + y
                                                                                                                 + ", "
                                                                                                                 + z
                                                                                                                 + ", "
                                                                                                                 + block.getLocalizedName()
                                                                                                                 + ", "
                                                                                                                 + newmeta
                                                                                                                 + ", "
                                                                                                                 + update
                                                                                                                 + ").[y >= "
                                                                                                                 + this.getHeight()
                                                                                                                 + "]",
                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
                return false;
            } else {
                Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                            z >> 7);
                if (chunk.getBlock((x & 0x7f) >> 3,
                                   y >> 3,
                                   (z & 0x7f) >> 3) != ConfigurationLib.littleChunk
                    && this.isAirBlock(x,
                                       y,
                                       z)) {
                    this.getParentWorld().setBlock(x >> 3,
                                                   y >> 3,
                                                   z >> 3,
                                                   ConfigurationLib.littleChunk);
                } else if (chunk.getBlock((x & 0x7f) >> 3,
                                          y >> 3,
                                          (z & 0x7f) >> 3) != ConfigurationLib.littleChunk) {
                    return false;
                }
                if (!this.isRemote) {
	                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
	                                                                                                         y >> 3,
	                                                                                                         z >> 3);
	                Block originalId = null;
	
	                if ((update & 1) != 0) {
	                    originalId = tile.getBlock(x & 7,
	                                               y & 7,
	                                               z & 7);
	                }
	
	                boolean flag = tile.setBlockIDWithMetadata(x & 7,
	                                                           y & 7,
	                                                           z & 7,
	                                                           block,
	                                                           newmeta);
	
	                this.func_147451_t/* updateAllLightTypes */(x,
	                                                            y,
	                                                            z);
	
	                if (flag) {
	                    if ((update & 2) != 0
	                        && ((update & 4) == 0)) {
	                        this.markBlockForUpdate(x,
	                                                y,
	                                                z);
	                    }
	
	                    if ((update & 1) != 0) {
	                        this.notifyBlockChange(x,
	                                               y,
	                                               z,
	                                               originalId);
	
	                        if (block != null
	                            && block.hasComparatorInputOverride()) {
	                            this.func_147453_f(x,
	                                               y,
	                                               z,
	                                               block);
	                        }
	                    }
	                }
	                return flag;
                }
            }
        } else {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "setBlock("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ", "
                                                                                                             + block.getLocalizedName()
                                                                                                             + ", "
                                                                                                             + newmeta
                                                                                                             + ", "
                                                                                                             + update
                                                                                                             + ").["
                                                                                                             + block.getLocalizedName()
                                                                                                             + ", "
                                                                                                             + newmeta
                                                                                                             + "]:No Change",
                                                                                                     LoggerLittleBlocks.LogLevel.ERROR);
        }
        return false;
    }

    @Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int metadata, int update) {
        if (x >= 0xfe363c80 && z >= 0xfe363c80 && x < 0x1c9c380
            && z < 0x1c9c380) {
            if (y < 0) {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                         "setBlock("
                                                                                                                 + x
                                                                                                                 + ", "
                                                                                                                 + y
                                                                                                                 + ", "
                                                                                                                 + z
                                                                                                                 + ", "
                                                                                                                 + metadata
                                                                                                                 + ", "
                                                                                                                 + update
                                                                                                                 + ").[y < 0]",
                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
                return false;
            } else if (y >= this.getHeight()) {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                         "setBlock("
                                                                                                                 + x
                                                                                                                 + ", "
                                                                                                                 + y
                                                                                                                 + ", "
                                                                                                                 + z
                                                                                                                 + ", "
                                                                                                                 + metadata
                                                                                                                 + ", "
                                                                                                                 + update
                                                                                                                 + ").[y >= "
                                                                                                                 + this.getHeight()
                                                                                                                 + "]",
                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
                return false;
            } else {
                Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                            z >> 7);
                if (chunk.getBlock((x & 0x7f) >> 3,
                                   y >> 3,
                                   (z & 0x7f) >> 3) != ConfigurationLib.littleChunk
                    && this.isAirBlock(x,
                                       y,
                                       z)) {
                    this.getParentWorld().setBlock(x >> 3,
                                                   y >> 3,
                                                   z >> 3,
                                                   ConfigurationLib.littleChunk);
                } else if (chunk.getBlock((x & 0x7f) >> 3,
                                          y >> 3,
                                          (z & 0x7f) >> 3) != ConfigurationLib.littleChunk) {
                    return false;
                }
                if (!this.isRemote) {
	                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
	                                                                                                         y >> 3,
	                                                                                                         z >> 3);
	                boolean flag = tile.setBlockMetadata(x & 7,
	                                                     y & 7,
	                                                     z & 7,
	                                                     metadata);
	
	                if (flag) {
	                    Block block = tile.getBlock(x & 7,
	                                                y & 7,
	                                                z & 7);
	
	                    if ((update & 2) != 0
	                        && ((update & 4) == 0)) {
	                        this.markBlockForUpdate(x,
	                                                y,
	                                                z);
	                    }
	
	                    if ((update & 1) != 0) {
	                        this.notifyBlockChange(x,
	                                               y,
	                                               z,
	                                               block);
	
	                        if (block != null && block.hasComparatorInputOverride()) {
	                            this.func_147453_f(x,
	                                               y,
	                                               z,
	                                               block);
	                        }
	                    }
	                }
	                return flag;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int xDiff = (maxX - minX) >> 1, yDiff = (maxY - minY) >> 1, zDiff = (maxZ - minZ) >> 1;
        int xMid = (minX + maxX) >> 1, yMid = (minY + maxY) >> 1, zMid = (minZ + maxZ) >> 1;

        boolean flag = this.getParentWorld().checkChunksExist((xMid >> 3)
                                                                      - xDiff,
                                                              (yMid >> 3)
                                                                      - yDiff,
                                                              (zMid >> 3)
                                                                      - zDiff,
                                                              (xMid >> 3)
                                                                      + xDiff,
                                                              (yMid >> 3)
                                                                      + yDiff,
                                                              (zMid >> 3)
                                                                      + zDiff);

        return flag;
    }

    @Override
    public void notifyBlocksOfNeighborChange(int x, int y, int z, Block block) {
        LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                 "notifyBlocksOfNeighborChange("
                                                                                                         + x
                                                                                                         + ", "
                                                                                                         + y
                                                                                                         + ", "
                                                                                                         + z
                                                                                                         + ", "
                                                                                                         + block
                                                                                                         + ")",
                                                                                                 LoggerLittleBlocks.LogLevel.DEBUG);
        this.notifyBlockOfNeighborChange(x - 1,
                                         y,
                                         z,
                                         block);
        this.notifyBlockOfNeighborChange(x + 1,
                                         y,
                                         z,
                                         block);
        this.notifyBlockOfNeighborChange(x,
                                         y - 1,
                                         z,
                                         block);
        this.notifyBlockOfNeighborChange(x,
                                         y + 1,
                                         z,
                                         block);
        this.notifyBlockOfNeighborChange(x,
                                         y,
                                         z - 1,
                                         block);
        this.notifyBlockOfNeighborChange(x,
                                         y,
                                         z + 1,
                                         block);
    }

    @Override
    public void notifyBlockOfNeighborChange(int x, int y, int z, Block blockId) {
        World world;
        Block id = this.getParentWorld().getBlock(x >> 3,
                                                  y >> 3,
                                                  z >> 3);
        if (id == ConfigurationLib.littleChunk) {
            world = this;
        } else {
            x >>= 3;
            y >>= 3;
            z >>= 3;
            world = this.getParentWorld();
        }
        if (!world.isRemote) {
            Block block = world.getBlock(x,
                                         y,
                                         z);
            if (block != null) {
                try {
                    block.onNeighborBlockChange(world,
                                                x,
                                                y,
                                                z,
                                                blockId);
                } catch (Throwable thrown) {
                    LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                             "onNeighborBlockChange("
                                                                                                                     + x
                                                                                                                     + ", "
                                                                                                                     + y
                                                                                                                     + ", "
                                                                                                                     + z
                                                                                                                     + ", "
                                                                                                                     + blockId
                                                                                                                     + ").["
                                                                                                                     + block.getLocalizedName()
                                                                                                                     + "]",
                                                                                                             LoggerLittleBlocks.LogLevel.DEBUG);
                }
            } else {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                         "notifyBlockOfNeighborChange("
                                                                                                                 + x
                                                                                                                 + ", "
                                                                                                                 + y
                                                                                                                 + ", "
                                                                                                                 + z
                                                                                                                 + ", "
                                                                                                                 + blockId
                                                                                                                 + "):Null",
                                                                                                         LoggerLittleBlocks.LogLevel.DEBUG);
            }
        }
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getTileEntity("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[Out of bounds]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return null;
        }
        if (y < 0) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y < 0]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return null;
        }
        if (y >= this.getHeight()) {
            return null;
        } else {
            Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                        z >> 7);
            if (chunk.getBlock((x & 0x7f) >> 3,
                               y >> 3,
                               (z & 0x7f) >> 3) == ConfigurationLib.littleChunk) {
                TileEntity tileentity = null;
                int l;
                TileEntity tileentity1;

                if (this.field_147481_N) {
                    for (l = 0; l < this.addedTileEntityList.size(); ++l) {
                        tileentity1 = (TileEntity) this.addedTileEntityList.get(l);

                        if (!tileentity1.isInvalid() && tileentity1.xCoord == x
                            && tileentity1.yCoord == y
                            && tileentity1.zCoord == z) {
                            tileentity = tileentity1;
                            break;
                        }
                    }
                }

                if (tileentity == null) {
                    TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                             y >> 3,
                                                                                                             z >> 3);
                    if (tile != null) {
                        tileentity = tile.getChunkTileEntity(x & 7,
                                                             y & 7,
                                                             z & 7);
                    }
                }

                if (tileentity == null) {
                    for (l = 0; l < this.addedTileEntityList.size(); ++l) {
                        tileentity1 = (TileEntity) this.addedTileEntityList.get(l);

                        if (!tileentity1.isInvalid() && tileentity1.xCoord == x
                            && tileentity1.yCoord == y
                            && tileentity1.zCoord == z) {
                            tileentity = tileentity1;
                            break;
                        }
                    }
                }

                return tileentity;
            } else {
                return this.getParentWorld().getTileEntity(x >> 3,
                                                           y >> 3,
                                                           z >> 3);
            }
        }
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tileentity) {
        Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                    z >> 7);
        if (chunk.getBlock((x & 0x7f) >> 3,
                           y >> 3,
                           (z & 0x7f) >> 3) == ConfigurationLib.littleChunk) {
            if (tileentity == null || tileentity.isInvalid()) {
                return;
            }
            if (tileentity.canUpdate()) {
                if (field_147481_N) {
                    Iterator iterator = addedTileEntityList.iterator();
                    while (iterator.hasNext()) {
                        TileEntity tileentity1 = (TileEntity) iterator.next();

                        if (tileentity1.xCoord == x && tileentity1.yCoord == y
                            && tileentity1.zCoord == z) {
                            tileentity1.invalidate();
                            iterator.remove();
                        }
                    }
                    addedTileEntityList.add(tileentity);
                } else {
                    loadedTileEntityList.add(tileentity);
                }
            }
            TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                     y >> 3,
                                                                                                     z >> 3);
            if (tile != null) {
                tile.setTileEntity(x & 7,
                                             y & 7,
                                             z & 7,
                                             tileentity);
            }
            this.func_147453_f(x,
                               y,
                               z,
                               getBlock(x,
                                        y,
                                        z));
        } else {
            this.getParentWorld().setTileEntity(x >> 3,
                                                y >> 3,
                                                z >> 3,
                                                tileentity);
        }
    }

    @Override
    public void func_147448_a/* addTileEntity */(Collection par1Collection) {
        List dest = field_147481_N ? addedTileEntityList : loadedTileEntityList;
        for (Object entity : par1Collection) {
            if (((TileEntity) entity).canUpdate()) {
                dest.add(entity);
            }
        }
    }

    @Override
    public void addTileEntity(TileEntity tileentity) {
        List dest = field_147481_N ? addedTileEntityList : loadedTileEntityList;
        if (tileentity.canUpdate()) {
            dest.add(tileentity);
        }
    }

    @Override
    public void func_147457_a/* markTileEntityForDespawn */(TileEntity par1TileEntity) {
        this.field_147483_b.add(par1TileEntity);
    }

    @Override
    public void removeTileEntity(int x, int y, int z) {
        TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
                                                                                                 y >> 3,
                                                                                                 z >> 3);
        if (tile != null) {
        	tile.removeTileEntity(x & 7,
						                    y & 7,
						                    z & 7);
        }
        this.func_147453_f(x, y, z, this.getBlock(x, y, z));
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            return _default;
        }

        Chunk chunk = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                    z >> 7);
        if (chunk == null || chunk.isEmpty()) {
            return _default;
        }

        Block block = this.getBlock(x,
                                    y,
                                    z);
        if (block == null) {
            return false;
        }

        return block.isSideSolid(this,
                                 x,
                                 y,
                                 z,
                                 side);
    }

    @Override
    public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
        this.getParentWorld().playSoundEffect(x
                                                      / ConfigurationLib.littleBlocksSize,
                                              y
                                                      / ConfigurationLib.littleBlocksSize,
                                              z
                                                      / ConfigurationLib.littleBlocksSize,
                                              s,
                                              f,
                                              f1);
    }

    @Override
    public void playRecord(String s, int x, int y, int z) {
        this.getParentWorld().playRecord(s,
                                         x >> 3,
                                         y >> 3,
                                         z >> 3);
    }

    @Override
    public void playAuxSFX(int soundID, int x, int y, int z, int blockIDWithMetadata) {
        this.getParentWorld().playAuxSFX(soundID,
                                         x >> 3,
                                         y >> 3,
                                         z >> 3,
                                         blockIDWithMetadata);
    }

    @Override
    public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
        this.getParentWorld().spawnParticle(s,
                                            x
                                                    / ConfigurationLib.littleBlocksSize,
                                            y
                                                    / ConfigurationLib.littleBlocksSize,
                                            z
                                                    / ConfigurationLib.littleBlocksSize,
                                            d3,
                                            d4,
                                            d5);
    }

    @Override
    public MovingObjectPosition func_147447_a/* rayTraceBlocks_do_do */(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1, boolean flag2) {
        Vec3.xCoord *= ConfigurationLib.littleBlocksSize;
        Vec3.yCoord *= ConfigurationLib.littleBlocksSize;
        Vec3.zCoord *= ConfigurationLib.littleBlocksSize;

        Vec31.xCoord *= ConfigurationLib.littleBlocksSize;
        Vec31.yCoord *= ConfigurationLib.littleBlocksSize;
        Vec31.zCoord *= ConfigurationLib.littleBlocksSize;
        return super.func_147447_a/* rayTraceBlocks_do_do */(Vec3,
                                                             Vec31,
                                                             flag,
                                                             flag1,
                                                             flag2);
    }

    @Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        Explosion explosion = new Explosion(this, entity, x, y, z, strength / 8);
        explosion.isFlaming = isFlaming;
        explosion.isSmoking = isSmoking;
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

    @Override
    public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
        return this.getClosestPlayer(x,
                                     y,
                                     z,
                                     distance);
    }

    @Override
    public World getParentWorld() {
        return LittleBlocks.proxy.getParentWorld(this,
                                                 this.parentDimension);
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
        this.getParentWorld().markBlockForUpdate(x >> 3,
                                                 y >> 3,
                                                 z >> 3);
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

    @Override
    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
        TileEntity tile = this.getParentWorld().getTileEntity(x >> 3,
                                                              y >> 3,
                                                              z >> 3);
        if (tile != null && tile instanceof TileEntityLittleChunk) {
            tile.markDirty();
        }
    }

    private int computeLightValue(int par1, int par2, int par3, EnumSkyBlock par4EnumSkyBlock) {
        if (par4EnumSkyBlock == EnumSkyBlock.Sky
            && this.canBlockSeeTheSky(par1,
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
        if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
            || z >= 0x1c9c380) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[Out of bounds]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y < 0) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y < 0]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        }
        if (y >= this.getHeight()) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(this.getParentWorld().isRemote,
                                                                                                     "getBlockMetadata("
                                                                                                             + x
                                                                                                             + ", "
                                                                                                             + y
                                                                                                             + ", "
                                                                                                             + z
                                                                                                             + ").[y >= "
                                                                                                             + this.getHeight()
                                                                                                             + "]",
                                                                                                     LoggerLittleBlocks.LogLevel.DEBUG);
            return 0;
        } else {
            Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                     z >> 7).getBlock((x & 0x7f) >> 3,
                                                                                      y >> 3,
                                                                                      (z & 0x7f) >> 3);
            int metadata = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                         z >> 7).getBlockMetadata((x & 0x7f) >> 3,
                                                                                                  y >> 3,
                                                                                                  (z & 0x7f) >> 3);
            if (id == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getParentWorld().getTileEntity(x >> 3,
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
                                    int blockOpacity = (block == null ? 0 : block.getLightOpacity(this,
                                                                                                  j4,
                                                                                                  k4,
                                                                                                  l4));
                                    int i5 = Math.max(1,
                                                      blockOpacity);
                                    i3 = this.getSavedLightValue(enumskyblock,
                                                                 j4,
                                                                 k4,
                                                                 l4);

                                    if (i3 == l2 - i5
                                        && changeCount < this.lightUpdateBlockList.length) {
                                        this.lightUpdateBlockList[changeCount++] = j4
                                                                                   - x
                                                                                   + 32
                                                                                   | k4
                                                                                     - y
                                                                                     + 32 << 6
                                                                                   | l4
                                                                                     - z
                                                                                     + 32 << 12
                                                                                   | l2
                                                                                     - i5 << 18;
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
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           - 1
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              - z
                                                                              + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2 + 1,
                                                        j2,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           + 1
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              - z
                                                                              + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2 - 1,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              - 1
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              - z
                                                                              + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2 + 1,
                                                        k2) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              + 1
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              - z
                                                                              + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2,
                                                        k2 - 1) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              - 1
                                                                              - z
                                                                              + 32 << 12);
                            }

                            if (this.getSavedLightValue(enumskyblock,
                                                        i2,
                                                        j2,
                                                        k2 + 1) < i3) {
                                this.lightUpdateBlockList[changeCount++] = i2
                                                                           - x
                                                                           + 32
                                                                           + (j2
                                                                              - y
                                                                              + 32 << 6)
                                                                           + (k2
                                                                              + 1
                                                                              - z
                                                                              + 32 << 12);
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
        int minX = MathHelper.floor_double((axisalignedbb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxX = MathHelper.floor_double((axisalignedbb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int minZ = MathHelper.floor_double((axisalignedbb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxZ = MathHelper.floor_double((axisalignedbb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

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
        return this.getEntitiesWithinAABBExcludingEntity(entity,
                                                         axisalignedbb,
                                                         (IEntitySelector) null);
    }

    @Override
    public List selectEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
        ArrayList arraylist = new ArrayList();
        int minX = MathHelper.floor_double((axisalignedbb.minX - MAX_ENTITY_RADIUS) / 16.0D);
        int maxX = MathHelper.floor_double((axisalignedbb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
        int minZ = MathHelper.floor_double((axisalignedbb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
        int maxZ = MathHelper.floor_double((axisalignedbb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);

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

            if (!entity1.isDead && entity1.preventEntitySpawning
                && entity1 != entity) {
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
        AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this,
                                                                            x,
                                                                            y,
                                                                            z);

        if (flag) {
            axisalignedbb = null;
        }

        if (axisalignedbb != null
            && !this.checkNoEntityCollision(axisalignedbb,
                                            entityPlacing)) {
            return false;
        } else {
            if (blockAt != null
                && (blockAt == Blocks.flowing_water || blockAt == Blocks.water
                    || blockAt == Blocks.flowing_lava || blockAt == Blocks.lava
                    || blockAt == Blocks.fire || blockAt.getMaterial().isReplaceable())) {
                blockAt = null;
            }

            if (blockAt != null && blockAt.isReplaceable(this,
                                                         x,
                                                         y,
                                                         z)) {
                blockAt = null;
            }

            return blockAt != null
                   && blockAt.getMaterial() == Material.circuits
                   && block == Blocks.anvil ? true : block != Blocks.air
                                                     && blockAt == null
                                                     && block.canPlaceBlockOnSide(this,
                                                                                  x,
                                                                                  y,
                                                                                  z,
                                                                                  side);
        }
    }

    @Override
    public boolean isOutSideLittleWorld(int x, int y, int z) {
        Block id = this.getParentWorld().getChunkFromChunkCoords(x >> 7,
                                                                 z >> 7).getBlock((x & 0x7f) >> 3,
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
