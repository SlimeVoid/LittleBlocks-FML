package net.slimevoid.littleblocks.world;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.util.FakePlayer;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.world.chunk.LittleChunkProviderServer;

import java.util.Collection;
import java.util.List;

public class LittleWorldServer extends WorldServer implements ILittleWorld {

    private final LittleServerWorld littleWorld;

    public LittleWorldServer(World referenceWorld, MinecraftServer minecraftServer, ISaveHandler iSaveHandler, int dimensionId, Profiler profiler) {
        super(minecraftServer, iSaveHandler, referenceWorld.getWorldInfo(), dimensionId, profiler);
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
    public void initialize(WorldSettings worldSettings) {
    }

    @Override
    public void tick() {
        this.getLittleWorld().tick();
    }

    @Override
    public boolean tickUpdates(boolean tick) {
        return this.getLittleWorld().tickUpdates(tick);
    }

//    @Override TODO
//    protected void func_147456_g/* tickBlocksAndAmbiance */() {
//        this.getLittleWorld().func_147456_g/* tickBlocksAndAmbiance */();
//    }

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
    public boolean isBlockTickPending(BlockPos pos, Block blockType) {
        return this.getLittleWorld().isBlockTickPending(pos, blockType);
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
    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        this.getLittleWorld().addBlockEvent(pos, blockIn, eventID, eventParam);
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
    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
        this.getLittleWorld().scheduleUpdate(pos, blockIn, delay);
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     * with some Value
     */
//    @Override TODO
//    public void scheduleBlockUpdateWithPriority(int x, int y, int z, Block blockId, int tickRate, int someValue) {
//        this.getLittleWorld().scheduleBlockUpdateWithPriority(x,
//                                                              y,
//                                                              z,
//                                                              blockId,
//                                                              tickRate,
//                                                              someValue);
//    }

//    @Override TODO
//    public void func_147446_b/* scheduleBlockUpdateFromLoad */(int x, int y, int z, Block blockId, int tickRate, int par6) {
//        this.getLittleWorld().func_147446_b/* scheduleBlockUpdateFromLoad */(x,
//                                                                             y,
//                                                                             z,
//                                                                             blockId,
//                                                                             tickRate,
//                                                                             par6);
//    }

//    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) { TODO ??
//        return this.getLittleWorld().getSkyBlockTypeBrightness(enumskyblock,
//                                                               x,
//                                                               y,
//                                                               z);
//    }

    @Override
    public long getWorldTime() {
        return super.getWorldTime(); // this.getLittleWorld() != null ? this.getLittleWorld().getWorldTime() : super.getWorldTime();
    }

    @Override
    public long getTotalWorldTime() {
        return super.getTotalWorldTime(); // this.getLittleWorld() != null ? this.getLittleWorld().getTotalWorldTime() : super.getTotalWorldTime();
    }

    @Override
    public int getCombinedLight(BlockPos pos, int a) { // TODO = getLightBrightnessForSkyBlocks?
        return this.getLittleWorld().getCombinedLight(pos, a);
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        return this.getLittleWorld().getLightBrightness(pos);
    }

    @Override
    public int getLight(BlockPos pos) {
        return this.getLittleWorld().getLight(pos);
    }

//    @Override TODO
//    public int getFullBlockLightValue(int x, int y, int z) {
//        return this.getLittleWorld().getFullBlockLightValue(x,
//                                                            y,
//                                                            z);
//    }


    @Override
    public boolean canBlockSeeSky(BlockPos pos) {
        return this.getLittleWorld().canBlockSeeSky(pos);
    }

    @Override
    public void setInitialSpawnLocation() {
        this.getLittleWorld().setInitialSpawnLocation();
    }

//    @Override TODO ?
//    public boolean blockExists(int x, int y, int z) {
//        return this.getLittleWorld().blockExists(x,
//                                                 y,
//                                                 z);
//    }


    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.getLittleWorld().getBlockState(pos);
    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        return this.getLittleWorld().spawnEntityInWorld(entity);
    }

    @Override
    public int getHeight() {
        return this.getLittleWorld().getHeight();
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        return this.getLittleWorld().setBlockState(pos, newState, flags);
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box) {
        return this.getLittleWorld().isAreaLoaded(box);
    }

    @Override
    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
        return this.getLittleWorld().isAreaLoaded(box, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius) {
        return this.getLittleWorld().isAreaLoaded(center, radius);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
        return this.getLittleWorld().isAreaLoaded(center, radius, allowEmpty);
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.getLittleWorld().isAreaLoaded(from, to);
    }

    @Override
    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
        return this.getLittleWorld().isAreaLoaded(from, to, allowEmpty);
    }

    @Override
    public void notifyBlockOfStateChange(BlockPos pos, Block blockIn) {
        this.getLittleWorld().notifyBlockOfStateChange(pos, blockIn);
    }

    @Override
    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
        this.getLittleWorld().notifyNeighborsOfStateChange(pos, blockType);
    }

    @Override
    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        this.getLittleWorld().notifyNeighborsOfStateExcept(pos, blockType, skipSide);
    }

    @Override
    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType) {
        this.getLittleWorld().notifyNeighborsRespectDebug(pos, blockType);
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.getLittleWorld().getTileEntity(pos);
    }

    @Override
    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        this.getLittleWorld().setTileEntity(pos, tileEntityIn);
    }

    @Override
    public void addTileEntities(Collection tileEntityCollection) {
        this.getLittleWorld().addTileEntities(tileEntityCollection);
    }

    @Override
    public boolean addTileEntity(TileEntity tileentity) {
        return this.getLittleWorld().addTileEntity(tileentity);
    }


    @Override
    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.getLittleWorld().markTileEntityForRemoval(tileEntityIn);
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        this.getLittleWorld().removeTileEntity(pos);
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return this.getLittleWorld().isSideSolid(pos, side, _default);
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
    public void playRecord(BlockPos p, String s) {
        super.playRecord(p, s);
    }

    @Override
    public void playAuxSFX(int a, BlockPos p, int b) {
        super.playAuxSFX(a, p, b);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, boolean p_175682_2_, double p_175682_3_, double p_175682_5_, double p_175682_7_, double p_175682_9_, double p_175682_11_, double p_175682_13_, int... p_175682_15_) {
        this.getLittleWorld().spawnParticle(particleType, p_175682_2_, p_175682_3_, p_175682_5_, p_175682_7_, p_175682_9_, p_175682_11_, p_175682_13_, p_175682_15_);
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(Vec3 p_72933_1_, Vec3 p_72933_2_) {
        return this.getLittleWorld().rayTraceBlocks(p_72933_1_, p_72933_2_);
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

//    @Override
//    public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
//        return this.getLittleWorld().getClosestVulnerablePlayer(x,
//                                                                y,
//                                                                z,
//                                                                distance);
//    }


    @Override
    public void markBlockForUpdate(BlockPos pos) {
        this.getLittleWorld().markBlockForUpdate(pos);
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

//    @Override TODO
//    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
//        this.getLittleWorld().markTileEntityChunkModified(x,
//                                                          y,
//                                                          z,
//                                                          tileentity);
//    }
//
//    @Override TODO
//    public boolean updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
//        return this.getLittleWorld().updateLightByType(enumSkyBlock,
//                                                       x,
//                                                       y,
//                                                       z);
//    }

    @Override
    public void saveAllChunks(boolean par1, IProgressUpdate par2IProgressUpdate) throws MinecraftException {

    }

    @Override
    public boolean canBlockBePlaced(Block blockId, BlockPos pos, boolean flag, EnumFacing side, Entity entityPlacing, ItemStack itemstack) {
        return this.getLittleWorld().canBlockBePlaced(
                blockId,
                pos,
                flag,
                side,
                entityPlacing,
                itemstack);
    }

    @Override
    public List getEntitiesInAABBexcluding(Entity entity, AxisAlignedBB axisalignedbb, Predicate entitySelector) {
        return this.getLittleWorld().getEntitiesInAABBexcluding(entity,
                axisalignedbb,
                entitySelector);
    }

    @Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.getLittleWorld().getEntitiesWithinAABBExcludingEntity(entity,
                                                                          axisalignedbb);
    }

    @Override
    public List getEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisalignedbb, Predicate entitySelector) {
        return this.getLittleWorld().getEntitiesWithinAABB(entityClass,
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
    public void activeChunkPosition(BlockPos pos, boolean forced) {
        this.getLittleWorld().activeChunkPosition(pos, forced);
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

    @Override
    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.provider);
        this.theChunkProviderServer = new LittleChunkProviderServer(this, ichunkloader, this.provider.createChunkGenerator());
        return this.theChunkProviderServer;
    }

    public java.io.File getChunkSaveLocation() {
        return ((net.minecraft.world.chunk.storage.AnvilChunkLoader)theChunkProviderServer.chunkLoader).chunkSaveLocation;
    }
}
