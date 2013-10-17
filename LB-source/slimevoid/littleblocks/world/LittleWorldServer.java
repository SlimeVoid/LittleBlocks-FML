package slimevoid.littleblocks.world;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.ForgeDirection;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.lib.PacketLib;

public class LittleWorldServer extends WorldServer implements ILittleWorld {

	private final World				realWorld;
	private final LittleServerWorld	littleWorld;

	public LittleWorldServer(World referenceWorld, MinecraftServer minecraftServer, ISaveHandler iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7iLogAgent) {
		super(minecraftServer, iSaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7iLogAgent);
		this.realWorld = referenceWorld;
		this.littleWorld = new LittleServerWorld(referenceWorld, this.provider);
	}

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	public LittleServerWorld getLittleWorld() {
		return this.littleWorld;
	}

	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
/*		int blockX = (x << 3) + littleX;
		int	blockY = (y << 3) + littleY;
		int blockZ = (z << 3) + littleZ;
													
		if (this.blockExists(blockX, blockY, blockZ)) {
			PacketLib.sendMetadata(	this,
									blockX,
									blockY,
									blockZ,
									blockId,
									side,
									metadata);
		}*/
	}

	@Override
	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
/*		int blockX = (x << 3) + littleX;
		int blockY = (y << 3) + littleY;
		int blockZ = (z << 3) + littleZ;
		
		if (lastBlockId != 0) {
			Block block = Block.blocksList[lastBlockId];
			if (block != null) {
				block.breakBlock(	this,
									blockX,
									blockY,
									blockZ,
									side,
									metadata);
				PacketLib.sendBreakBlock(	this.getLittleWorld(),
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
				block.onBlockAdded(	this,
									blockX,
									blockY,
									blockZ);
				PacketLib.sendBlockAdded(	this.getLittleWorld(),
											blockX,
											blockY,
											blockZ,
											side,
											blockId,
											metadata);
			}
		}*/
	}

	@Override
	public boolean isOutdated(World world) {
		return this.getLittleWorld().isOutdated(world);
	}

	public void littleTick() {
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
	protected void tickBlocksAndAmbiance() {
		this.getLittleWorld().tickBlocksAndAmbiance();
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
	public boolean isBlockTickScheduled(int x, int y, int z, int blockId) {
		return this.getLittleWorld().isBlockTickScheduled(	x,
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
		return this.getLittleWorld().newExplosion(	entity,
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
	public void addBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParam) {
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
	public void scheduleBlockUpdate(int x, int y, int z, int blockId, int tickRate) {
		this.func_82740_a(	x,
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
	public void func_82740_a(int x, int y, int z, int blockId, int tickRate, int someValue) {
		this.getLittleWorld().func_82740_a(	x,
											y,
											z,
											blockId,
											tickRate,
											someValue);
	}

	@Override
	public void scheduleBlockUpdateFromLoad(int x, int y, int z, int blockId, int tickRate, int par6) {
		this.getLittleWorld().scheduleBlockUpdateFromLoad(	x,
											y,
											z,
											blockId,
											tickRate,
											par6);
	}

	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
		return this.getLittleWorld().getSkyBlockTypeBrightness(	enumskyblock,
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
	public float getBrightness(int x, int y, int z, int l) {
		return this.getLittleWorld().getBrightness(	x,
													y,
													z,
													l);
	}

	@Override
	public int getBlockLightValue(int x, int y, int z) {
		return this.getLittleWorld().getBlockLightValue(x,
														y,
														z);
	}

	@Override
	public void setSpawnLocation() {
		this.getLittleWorld().setSpawnLocation();
	}

	@Override
	public boolean blockExists(int x, int y, int z) {
		return this.getLittleWorld().blockExists(	x,
													y,
													z);
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		return this.getLittleWorld().getBlockId(x,
												y,
												z);
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		return this.getLittleWorld().spawnEntityInWorld(entity);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return this.getLittleWorld().getBlockMetadata(	x,
														y,
														z);
	}

	@Override
	public int getHeight() {
		return this.getLittleWorld().getHeight();
	}

	// @Override
	// public boolean setBlock(int x, int y, int z, int blockID, int newmeta,
	// int update, boolean newTile) {
	// return this.getLittleWorld().setBlock(x, y, z, blockID, newmeta, update,
	// newTile);
	// }

	@Override
	public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update) {
		return this.getLittleWorld().setBlock(	x,
												y,
												z,
												blockID,
												newmeta,
												update);
	}

	@Override
	public boolean setBlockMetadataWithNotify(int x, int y, int z, int newmeta, int update) {
		return this.getLittleWorld().setBlockMetadataWithNotify(x,
																y,
																z,
																newmeta,
																update);
	}

	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return this.getLittleWorld().checkChunksExist(	minX,
														minY,
														minZ,
														maxX,
														maxY,
														maxZ);
	}

	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
		this.getLittleWorld().notifyBlocksOfNeighborChange(	x,
															y,
															z,
															blockId);
	}

	@Override
	public void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
		this.getLittleWorld().notifyBlockOfNeighborChange(	x,
															y,
															z,
															blockId);
	}

	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		return this.getLittleWorld().getBlockTileEntity(x,
														y,
														z);
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
		this.getLittleWorld().setBlockTileEntity(	x,
													y,
													z,
													tileentity);
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
		this.getLittleWorld().removeBlockTileEntity(x,
													y,
													z);
	}

	@Override
	public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
		return this.getLittleWorld().isBlockSolidOnSide(x,
														y,
														z,
														side,
														_default);
	}

	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		this.getLittleWorld().playSoundEffect(	x,
												y,
												z,
												s,
												f,
												f1);
	}

	@Override
	public void playRecord(String s, int x, int y, int z) {
		this.getLittleWorld().playRecord(	s,
											x,
											y,
											z);
	}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		this.getLittleWorld().playAuxSFX(	x,
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
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		return this.getLittleWorld().rayTraceBlocks_do_do(	Vec3,
															Vec31,
															flag,
															flag1);
	}

	@Override
	public Entity getEntityByID(int entityId) {
		return this.getLittleWorld().getEntityByID(entityId);
	}

	@Override
	public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
		return this.getLittleWorld().getClosestPlayer(	x,
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
		this.getLittleWorld().markBlockForUpdate(	x,
													y,
													z);
	}

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		this.getLittleWorld().markBlockForRenderUpdate(	x,
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
    public void updateTileEntityChunkAndDoNothing(int x, int y, int z, TileEntity tileentity) {
		this.getLittleWorld().updateTileEntityChunkAndDoNothing(x, y, z, tileentity);
	}

	@Override
	public void updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
		this.getLittleWorld().updateLightByType(enumSkyBlock,
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
	public boolean canPlaceEntityOnSide(int blockId, int x, int y, int z, boolean flag, int side, Entity entityPlacing, ItemStack itemstack) {
		return this.getLittleWorld().canPlaceEntityOnSide(blockId, x, y, z, flag, side, entityPlacing, itemstack);
	}
	
	@Override
	public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
		return this.getLittleWorld().getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb, entitySelector);
	}

	@Override
    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisalignedbb) {
		return this.getLittleWorld().getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb);
	}

	@Override
	public List selectEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisalignedbb, IEntitySelector entitySelector) {
		return this.getLittleWorld().selectEntitiesWithinAABB(entityClass, axisalignedbb, entitySelector);
	}
	
	@Override
    public List getEntitiesWithinAABB(Class entityClass, AxisAlignedBB axisAlignedBB) {
		return this.getLittleWorld().getEntitiesWithinAABB(entityClass, axisAlignedBB);
	}
	
	@Override
	public boolean checkNoEntityCollision(AxisAlignedBB axisalignedbb, Entity entity) {
		return this.getLittleWorld().checkNoEntityCollision(axisalignedbb, entity);
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
}
