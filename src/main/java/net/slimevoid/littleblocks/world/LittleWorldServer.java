package net.slimevoid.littleblocks.world;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.slimevoid.littleblocks.api.ILittleWorld;

public class LittleWorldServer extends WorldServer implements ILittleWorld {

    private final LittleServerWorld littleWorld;

    public LittleWorldServer(World referenceWorld, MinecraftServer minecraftServer, ISaveHandler iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler) {
        super(minecraftServer, iSaveHandler, par3Str, par4, par5WorldSettings, par6Profiler);
        this.littleWorld = new LittleServerWorld(referenceWorld, this.provider);
    }

    @Override
    public World getParentWorld() {
        return this.getLittleWorld().getParentWorld();
    }

    public LittleServerWorld getLittleWorld() {
        return this.littleWorld;
    }

    @Override
    public boolean isOutdated(World world) {
        return this.getLittleWorld().isOutdated(world);
    }

    @Override
    protected void initialize(WorldSettings worldSettings) {
    }

    @Override
    public void tick() {
        this.getLittleWorld().tick();
    }

    @Override
    public boolean tickUpdates(boolean tick) {
        return this.getLittleWorld().tickUpdates(tick);
    }

    @Override
    protected void func_147456_g/* tickBlocksAndAmbiance */() {
        this.getLittleWorld().func_147456_g/* tickBlocksAndAmbiance */();
    }

    public void updateLittleEntities() {
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
    public boolean isBlockTickScheduledThisTick(int x, int y, int z, Block blockId) {
        return this.getLittleWorld().isBlockTickScheduledThisTick(x,
                                                                  y,
                                                                  z,
                                                                  blockId);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is
     * not finished)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
        return this.getLittleWorld().newExplosion(entity,
                                                  x,
                                                  y,
                                                  z,
                                                  strength,
                                                  isFlaming,
                                                  isSmoking);
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the
     * next tick(), the block specified will have its onBlockEvent handler
     * called with the given parameters. Args: X,Y,Z, BlockID, EventID,
     * EventParameter
     */
    @Override
    public void addBlockEvent(int x, int y, int z, Block blockID, int eventID, int eventParam) {
        this.getLittleWorld().addBlockEvent(x,
                                            y,
                                            z,
                                            blockID,
                                            eventID,
                                            eventParam);
    }

    @Override
    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk, boolean forceRemove) {
        return this.getLittleWorld().getPendingBlockUpdates(chunk,
                                                            forceRemove);
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
    public void scheduleBlockUpdateWithPriority(int x, int y, int z, Block blockId, int tickRate, int someValue) {
        this.getLittleWorld().scheduleBlockUpdateWithPriority(x,
                                                              y,
                                                              z,
                                                              blockId,
                                                              tickRate,
                                                              someValue);
    }

    @Override
    public void func_147446_b/* scheduleBlockUpdateFromLoad */(int x, int y, int z, Block blockId, int tickRate, int par6) {
        this.getLittleWorld().func_147446_b/* scheduleBlockUpdateFromLoad */(x,
                                                                             y,
                                                                             z,
                                                                             blockId,
                                                                             tickRate,
                                                                             par6);
    }

    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
        return this.getLittleWorld().getSkyBlockTypeBrightness(enumskyblock,
                                                               x,
                                                               y,
                                                               z);
    }

    @Override
    public long getWorldTime() {
        return super.getWorldTime(); // this.getLittleWorld() != null ? this.getLittleWorld().getWorldTime() : super.getWorldTime();
    }

    @Override
    public long getTotalWorldTime() {
        return super.getTotalWorldTime(); // this.getLittleWorld() != null ? this.getLittleWorld().getTotalWorldTime() : super.getTotalWorldTime();
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
        return this.getLittleWorld().getLightBrightnessForSkyBlocks(x,
                                                                    y,
                                                                    z,
                                                                    l);
    }

    @Override
    public float getLightBrightness(int x, int y, int z) {
        return this.getLittleWorld().getLightBrightness(x,
                                                        y,
                                                        z);
    }

    @Override
    public int getBlockLightValue(int x, int y, int z) {
        return this.getLittleWorld().getBlockLightValue(x,
                                                        y,
                                                        z);
    }

    @Override
    public int getFullBlockLightValue(int x, int y, int z) {
        return this.getLittleWorld().getFullBlockLightValue(x,
                                                            y,
                                                            z);
    }

    @Override
    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return this.getLittleWorld().canBlockSeeTheSky(x,
                                                       y,
                                                       z);
    }

    @Override
    public void setSpawnLocation() {
        this.getLittleWorld().setSpawnLocation();
    }

    @Override
    public boolean blockExists(int x, int y, int z) {
        return this.getLittleWorld().blockExists(x,
                                                 y,
                                                 z);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return this.getLittleWorld().getBlock(x,
                                              y,
                                              z);
    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        return this.getLittleWorld().spawnEntityInWorld(entity);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return this.getLittleWorld().getBlockMetadata(x,
                                                      y,
                                                      z);
    }

    @Override
    public int getHeight() {
        return this.getLittleWorld().getHeight();
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block blockID, int newmeta, int update) {
    	//System.out.println("Server: " + this + " : " + blockID + " | " + newmeta);
        return this.getLittleWorld().setBlock(x,
                                              y,
                                              z,
                                              blockID,
                                              newmeta,
                                              update);
    }

    @Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int newmeta, int update) {
    	//System.out.println("Server: " + this + " : " + this.getBlock(x, y, z) + " | " + newmeta);
        return this.getLittleWorld().setBlockMetadataWithNotify(x,
                                                                y,
                                                                z,
                                                                newmeta,
                                                                update);
    }

    @Override
    public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return this.getLittleWorld().checkChunksExist(minX,
                                                      minY,
                                                      minZ,
                                                      maxX,
                                                      maxY,
                                                      maxZ);
    }

    @Override
    public void notifyBlocksOfNeighborChange(int x, int y, int z, Block blockId) {
        this.getLittleWorld().notifyBlocksOfNeighborChange(x,
                                                           y,
                                                           z,
                                                           blockId);
    }

    @Override
    public void notifyBlockOfNeighborChange(int x, int y, int z, Block blockId) {
        this.getLittleWorld().notifyBlockOfNeighborChange(x,
                                                          y,
                                                          z,
                                                          blockId);
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return this.getLittleWorld().getTileEntity(x,
                                                   y,
                                                   z);
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tileentity) {
        this.getLittleWorld().setTileEntity(x,
                                            y,
                                            z,
                                            tileentity);
    }

    @Override
    public void func_147448_a/* addTileEntity */(Collection collection) {
        this.getLittleWorld().func_147448_a/* addTileEntity */(collection);
    }

    @Override
    public void addTileEntity(TileEntity tileentity) {
        this.getLittleWorld().addTileEntity(tileentity);
    }

    @Override
    public void func_147457_a/* markTileEntityForDespawn */(TileEntity tileentity) {
        this.getLittleWorld().func_147457_a/* markTileEntityForDespawn */(tileentity);
    }

    @Override
    public void removeTileEntity(int x, int y, int z) {
        this.getLittleWorld().removeTileEntity(x,
                                               y,
                                               z);
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return this.getLittleWorld().isSideSolid(x,
                                                 y,
                                                 z,
                                                 side,
                                                 _default);
    }

    @Override
    public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
        this.getLittleWorld().playSoundEffect(x,
                                              y,
                                              z,
                                              s,
                                              f,
                                              f1);
    }

    @Override
    public void playRecord(String s, int x, int y, int z) {
        this.getLittleWorld().playRecord(s,
                                         x,
                                         y,
                                         z);
    }

    @Override
    public void playAuxSFX(int soundID, int x, int y, int z, int blockIDWithMetadata) {
        this.getLittleWorld().playAuxSFX(soundID,
                                         x,
                                         y,
                                         z,
                                         blockIDWithMetadata);
    }

    @Override
    public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
        this.getLittleWorld().spawnParticle(s,
                                            x,
                                            y,
                                            z,
                                            d3,
                                            d4,
                                            d5);
    }

    @Override
    public MovingObjectPosition func_147447_a/* rayTraceBlocks_do_do */(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1, boolean flag2) {
        return this.getLittleWorld().func_147447_a/* rayTraceBlocks_do_do */(Vec3,
                                                                             Vec31,
                                                                             flag,
                                                                             flag1,
                                                                             flag2);
    }

    @Override
    public Entity getEntityByID(int entityId) {
        return this.getLittleWorld().getEntityByID(entityId);
    }

    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        int blockX = MathHelper.floor_double(x);
        int blockY = MathHelper.floor_double(y);
        int blockZ = MathHelper.floor_double(z);
        EntityPlayer entityplayer = this.getParentWorld().getClosestPlayer(blockX >> 3,
                                                                           blockY >> 3,
                                                                           blockZ >> 3,
                                                                           distance);
        if (entityplayer != null) {
            FakePlayer player = new FakePlayer((WorldServer) this, entityplayer.getGameProfile());
            player.posX = MathHelper.floor_double(entityplayer.posX) << 3;
            player.posY = MathHelper.floor_double(entityplayer.posY) << 3;
            player.posZ = MathHelper.floor_double(entityplayer.posZ) << 3;
            return player;
        }
        return null;
    }

    // @Override
    // public EntityPlayer getClosestPlayer(double x, double y, double z, double
    // distance) {
    //
    // return this.getLittleWorld().getClosestPlayer(x,
    // y,
    // z,
    // distance);
    // }

    @Override
    public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
        return this.getLittleWorld().getClosestVulnerablePlayer(x,
                                                                y,
                                                                z,
                                                                distance);
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
        this.getLittleWorld().markBlockForUpdate(x,
                                                 y,
                                                 z);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
        this.getLittleWorld().markBlockRangeForRenderUpdate(x,
                                                            y,
                                                            z,
                                                            x2,
                                                            y2,
                                                            z2);
    }

    @Override
    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
        this.getLittleWorld().markTileEntityChunkModified(x,
                                                          y,
                                                          z,
                                                          tileentity);
    }

    @Override
    public boolean updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
        return this.getLittleWorld().updateLightByType(enumSkyBlock,
                                                       x,
                                                       y,
                                                       z);
    }

    // @Override
    // protected IChunkProvider createChunkProvider() {
    // return new LittleChunkProvider(this);
    // }

    @Override
    public void saveAllChunks(boolean par1, IProgressUpdate par2IProgressUpdate) throws MinecraftException {

    }

    @Override
    public boolean canPlaceEntityOnSide(Block blockId, int x, int y, int z, boolean flag, int side, Entity entityPlacing, ItemStack itemstack) {
        return this.getLittleWorld().canPlaceEntityOnSide(blockId,
                                                          x,
                                                          y,
                                                          z,
                                                          flag,
                                                          side,
                                                          entityPlacing,
                                                          itemstack);
    }

    @Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
        return this.getLittleWorld().getEntitiesWithinAABBExcludingEntity(entity,
                                                                          axisalignedbb,
                                                                          entitySelector);
    }

    @Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.getLittleWorld().getEntitiesWithinAABBExcludingEntity(entity,
                                                                          axisalignedbb);
    }

    @Override
    public List selectEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
        return this.getLittleWorld().selectEntitiesWithinAABB(entityClass,
                                                              axisalignedbb,
                                                              entitySelector);
    }

    @Override
    public List getEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisAlignedBB) {
        return this.getLittleWorld().getEntitiesWithinAABB(entityClass,
                                                           axisAlignedBB);
    }

    @Override
    public boolean checkNoEntityCollision(AxisAlignedBB axisalignedbb, Entity entity) {
        return this.getLittleWorld().checkNoEntityCollision(axisalignedbb,
                                                            entity);
    }

    @Override
    public boolean isOutSideLittleWorld(int x, int y, int z) {
        return this.getLittleWorld().isOutSideLittleWorld(x,
                                                          y,
                                                          z);
    }

    //
    // @Override
    // public void func_104140_m() {
    //
    // }
    //
    // @Override
    // protected void saveLevel() throws MinecraftException {
    //
    // }
    //

    @Override
    public void activeChunkPosition(ChunkPosition chunkposition, boolean forced) {
        this.getLittleWorld().activeChunkPosition(chunkposition,
                                                  forced);
    }

    @Override
    public List<TileEntity> getLoadedTileEntities() {
        return this.getLittleWorld().getLoadedTileEntities();
    }

    @Override
    public void addLoadedTileEntity(TileEntity tileentity) {
        this.getLittleWorld().addLoadedTileEntity(tileentity);
    }
	
	@Override
    public WorldSavedData loadItemData(Class itemClass, String itemName) {
		WorldSavedData yourMum = null;
		return yourMum;
	}
	
	@Override
    public void setItemData(String itemName, WorldSavedData yourMum) {
		// Your Mum!
	}

	@Override
    public int getUniqueDataId(String itemName) {
		int yourMum = 0;
		return yourMum;
	}
}
