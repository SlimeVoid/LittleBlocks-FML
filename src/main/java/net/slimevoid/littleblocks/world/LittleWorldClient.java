package net.slimevoid.littleblocks.world;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.world.chunk.LittleChunkProvider;

import java.util.Collection;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LittleWorldClient extends WorldClient implements ILittleWorld {

    private final LittleClientWorld littleWorld;

    public LittleWorldClient(World referenceWorld, WorldSettings settings, int dimensionId, EnumDifficulty difficulty) {
        super(null, settings, dimensionId, difficulty, referenceWorld.theProfiler);
        String name = referenceWorld.getWorldInfo().getWorldName()
                      + ".littleWorld";
        this.finishSetup();
        this.littleWorld = new LittleClientWorld(referenceWorld, provider, false);
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(this));
    }

    public void finishSetup() {
        this.provider.registerWorld(this);
        this.provider.setDimension(this.provider.getDimensionId());
    }

    @Override
    public World getParentWorld() {
        return this.getLittleWorld().getParentWorld();
    }

    public LittleWorld getLittleWorld() {
        return this.littleWorld;
    }

    @Override
    public boolean isOutdated(World world) {
        return this.getLittleWorld().isOutdated(world);
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
    public void updateEntities() {
        this.getLittleWorld().updateEntities();
    }



//    @Override TODO Dahell is that?
//    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
//        return this.getLittleWorld().getSkyBlockTypeBrightness(enumskyblock,
//                                                               x,
//                                                               y,
//                                                               z);
//    }

    @Override
    public long getWorldTime() {
        return this.getLittleWorld().getWorldTime();
    }

    @Override
    public long getTotalWorldTime() {
        return this.getLittleWorld().getTotalWorldTime();
    }

    @Override
    public int getCombinedLight(BlockPos pos, int a) { // TODO = getLightBrightnessForSkyBlocks ?
        return this.getLittleWorld().getCombinedLight(pos, a);
    }

    @Override
    public float getLightBrightness(BlockPos pos) {
        return this.getLittleWorld().getLightBrightness(pos);
    }

    @Override
    public int getBlockLightOpacity(BlockPos pos) {
        return this.getLittleWorld().getBlockLightOpacity(pos);
    }

//    @Override // TODO ??
//    public int getFullBlockLightValue(int x, int y, int z) {
//        return this.getLittleWorld().getFullBlockLightValue(x,
//                                                            y,
//                                                            z);
//    }


    @Override
    public boolean canSeeSky(BlockPos pos) {
        return this.getLittleWorld().canSeeSky(pos);
    }

    @Override
    public void setInitialSpawnLocation() {
        this.getLittleWorld().setInitialSpawnLocation();
    }

//    @Override TODO ??
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



//    @Override TODO ??
//    public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
//        return this.getLittleWorld().checkChunksExist(minX,
//                                                      minY,
//                                                      minZ,
//                                                      maxX,
//                                                      maxY,
//                                                      maxZ);
//    }


    @Override
    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType) {
        this.getLittleWorld().notifyNeighborsOfStateChange(pos, blockType);
    }

    @Override
    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
        this.getLittleWorld().notifyNeighborsOfStateExcept(pos, blockType, skipSide);
    }

    @Override
    public void notifyBlockOfStateChange(BlockPos pos, Block blockIn) {
        this.getLittleWorld().notifyBlockOfStateChange(pos, blockIn);
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
        this.getLittleWorld().playRecord(p, s);
    }

    @Override
    public void playAuxSFX(int a, BlockPos p, int b) {
        this.getLittleWorld().playAuxSFX(a, p, b);
    }

    @Override
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175688_14_) {
        this.getLittleWorld().spawnParticle(particleType, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175688_14_);
    }

    @Override
    public MovingObjectPosition rayTraceBlocks(Vec3 u, Vec3 v, boolean a, boolean b, boolean c) {
        return this.getLittleWorld().rayTraceBlocks(u, v, a, b, c);
    }

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

    @Override
    protected IChunkProvider createChunkProvider() {
        return new LittleChunkProvider(this);
    }

    @Override
    public Entity getEntityByID(int entityId) {
        return this.getLittleWorld().getEntityByID(entityId);
    }

    @Override
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        return this.getLittleWorld().getClosestPlayer(x,
                                                      y,
                                                      z,
                                                      distance);
    }

//    @Override TODO ??
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



//    @Override TODO ??
//    public void markTileEntityChunkModified(int x, int y, int z, TileEntity tileentity) {
//        this.getLittleWorld().markTileEntityChunkModified(x,
//                                                          y,
//                                                          z,
//                                                          tileentity);
//    }

//    @Override TODO ??
//    public boolean updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
//        return this.getLittleWorld().updateLightByType(enumSkyBlock,
//                                                       x,
//                                                       y,
//                                                       z);
//    }

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
    protected int getRenderDistanceChunks() {
        return FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks;
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
