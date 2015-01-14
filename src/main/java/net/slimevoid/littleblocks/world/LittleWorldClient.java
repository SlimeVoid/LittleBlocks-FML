package net.slimevoid.littleblocks.world;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LittleWorldClient extends World implements ILittleWorld {

    private final LittleClientWorld littleWorld;

    public LittleWorldClient(World referenceWorld, ISaveHandler saveHandler, String worldName, WorldProvider provider, WorldSettings worldSettings, int difficultySetting, Profiler profiler) {
        super(saveHandler, worldName, provider, worldSettings, profiler);
        this.isRemote = true;
        String name = referenceWorld.getWorldInfo().getWorldName()
                      + ".littleWorld";
        this.finishSetup();
        this.littleWorld = new LittleClientWorld(referenceWorld, provider, name);
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(this));
    }

    @Override
    public void finishSetup() {
        this.provider.registerWorld(this);
        this.provider.dimensionId = this.provider.dimensionId;
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

    @Override
    public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
        return this.getLittleWorld().getSkyBlockTypeBrightness(enumskyblock,
                                                               x,
                                                               y,
                                                               z);
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
    	//System.out.println("Client: " + this + " : " + blockID + " | " + newmeta);
        return this.getLittleWorld().setBlock(x,
                                              y,
                                              z,
                                              blockID,
                                              newmeta,
                                              update);
    }

    @Override
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int newmeta, int update) {
    	//System.out.println("Client: " + this + " : " + this.getBlock(x, y, z) + " | " + newmeta);
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
    public void playAuxSFX(int x, int y, int z, int l, int i1) {
        this.getLittleWorld().playAuxSFX(x,
                                         y,
                                         z,
                                         l,
                                         i1);
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
        return this.getLittleWorld().createChunkProvider();
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
	/** getRenderViewDistance()**/
	protected int func_152379_p() {
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
