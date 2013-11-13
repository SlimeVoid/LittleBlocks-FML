package slimevoid.littleblocks.world;

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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeDirection;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoidlib.data.Logger;

import com.google.common.collect.ImmutableSetMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LittleWorld extends World implements ILittleWorld {

	protected int				ticksInWorld		= 0;
	protected static final int	MAX_TICKS_IN_WORLD	= 5;

	private World				realWorld;

	@SideOnly(Side.CLIENT)
	public LittleWorld(World world, WorldProvider worldprovider, String worldName) {
		super(world.getSaveHandler(), worldName, worldprovider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), null, null);
		this.realWorld = world;
		this.isRemote = true;
		LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																									"LittleWorld["
																											+ world.toString()
																											+ "].("
																											+ world.getWorldInfo().getSeed()
																											+ ", "
																											+ world.getWorldInfo().getGameType()
																											+ ", "
																											+ world.getWorldInfo().isMapFeaturesEnabled()
																											+ ", "
																											+ world.getWorldInfo().isHardcoreModeEnabled()
																											+ ", "
																											+ world.getWorldInfo().getTerrainType()
																											+ ").realWorld["
																											+ this.getRealWorld().toString()
																											+ "]",
																									LoggerLittleBlocks.LogLevel.DEBUG);
	}

	public LittleWorld(World world, WorldProvider worldprovider) {
		super(world.getSaveHandler(), "LittleBlocksWorld", new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), worldprovider, null, null);
		this.realWorld = world;
		this.isRemote = false;
		LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																									"LittleWorld["
																											+ world.toString()
																											+ "].("
																											+ world.getWorldInfo().getSeed()
																											+ ", "
																											+ world.getWorldInfo().getGameType()
																											+ ", "
																											+ world.getWorldInfo().isMapFeaturesEnabled()
																											+ ", "
																											+ world.getWorldInfo().isHardcoreModeEnabled()
																											+ ", "
																											+ world.getWorldInfo().getTerrainType()
																											+ ").realWorld["
																											+ this.getRealWorld().toString()
																											+ "]",
																									LoggerLittleBlocks.LogLevel.DEBUG);
	}

	@Override
	public void tick() {
		super.tick();
		this.func_82738_a(this.getTotalWorldTime() + 1L);
		if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
			this.setWorldTime(this.getWorldTime() + 1L);
		}
		this.tickBlocksAndAmbiance();
	}

	/**
	 * Runs through the list of updates to run and ticks them
	 */
	@Override
	public boolean tickUpdates(boolean tick) {
		return false;
	}

	private final Set	previousActiveChunkSet	= new HashSet();

	@Override
	protected void tickBlocksAndAmbiance() {
		/*
		 * super.tickBlocksAndAmbiance();
		 * this.previousActiveChunkSet.retainAll(this.activeChunkSet); if
		 * (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
		 * this.previousActiveChunkSet.clear(); } int i = 0;
		 * Iterator<ChunkCoordIntPair> iterator =
		 * this.activeChunkSet.iterator(); while (iterator.hasNext()) {
		 * ChunkCoordIntPair chunkcoordintpair = iterator.next(); if
		 * (!this.previousActiveChunkSet.contains(chunkcoordintpair)) { int x =
		 * chunkcoordintpair.chunkXPos * 16; int z = chunkcoordintpair.chunkZPos
		 * * 16; //this.theProfiler.startSection("getChunk"); Chunk chunk =
		 * this.getChunkFromChunkCoords( chunkcoordintpair.chunkXPos,
		 * chunkcoordintpair.chunkZPos); boolean flag = false;
		 * Iterator<TileEntity> tiles =
		 * chunk.chunkTileEntityMap.values().iterator(); while (tiles.hasNext())
		 * { TileEntity tile = tiles.next(); if (!flag && tile instanceof
		 * TileEntityLittleChunk) { flag = true; } }
		 * //this.theProfiler.endSection(); if (flag) {
		 * this.previousActiveChunkSet.add(chunkcoordintpair); } ++i; if (i >=
		 * 10) { return; } } }
		 */
	}

	@Override
	public ImmutableSetMultimap<ChunkCoordIntPair, Ticket> getPersistentChunks() {
		return this.getRealWorld().getPersistentChunks();
	}

	@Override
	protected void setActivePlayerChunksAndCheckLight() {
		this.activeChunkSet.clear();
		this.activeChunkSet.addAll(this.getPersistentChunks().keySet());

		// this.theProfiler.startSection("buildList");
		int playerIndex;
		EntityPlayer entityplayer;
		int x;
		int z;

		for (playerIndex = 0; playerIndex < this.getRealWorld().playerEntities.size(); playerIndex++) {

			entityplayer = (EntityPlayer) this.getRealWorld().playerEntities.get(playerIndex);
			x = MathHelper.floor_double(entityplayer.posX / 16.0D);
			z = MathHelper.floor_double(entityplayer.posZ / 16.0D);
			byte b0 = 7;

			for (int max = -b0; max <= b0; ++max) {
				for (int i1 = -b0; i1 <= b0; ++i1) {
					this.activeChunkSet.add(new ChunkCoordIntPair(max + x, i1
																			+ z));
				}
			}
		}
		// this.theProfiler.endSection();
	}

	public List<TileEntity>		loadedTiles	= new ArrayList<TileEntity>();
	private List<TileEntity>	addedTiles	= new ArrayList<TileEntity>();
	private boolean				scanningTiles;

	/** Entities marked for removal. */
	private List				tileRemoval	= new ArrayList();

	@Override
	public void updateEntities() {
		this.scanningTiles = true;
		Iterator<TileEntity> loadedTile = this.loadedTiles.iterator();

		while (loadedTile.hasNext()) {
			TileEntity tileentity = loadedTile.next();

			if (!tileentity.isInvalid() && tileentity.canUpdate()
				&& this.blockExists(tileentity.xCoord,
									tileentity.yCoord,
									tileentity.zCoord)) {
				try {
					tileentity.updateEntity();
				} catch (Throwable t) {
				}
			}

			if (tileentity.isInvalid()) {
				loadedTile.remove();

				TileEntity tileentitylb = this.getRealWorld().getBlockTileEntity(	tileentity.xCoord >> 3,
																					tileentity.yCoord >> 3,
																					tileentity.zCoord >> 3);
				if (tileentitylb != null
					&& tileentitylb instanceof TileEntityLittleChunk) {
					((TileEntityLittleChunk) tileentitylb).cleanChunkBlockTileEntity(	tileentity.xCoord & 7,
																						tileentity.yCoord & 7,
																						tileentity.zCoord & 7);
				}
			}
		}

		if (!this.tileRemoval.isEmpty()) {
			for (Object tile : tileRemoval) {
				((TileEntity) tile).onChunkUnload();
			}
			this.loadedTiles.removeAll(this.tileRemoval);
			this.tileRemoval.clear();
		}

		this.scanningTiles = false;

		if (!this.addedTiles.isEmpty()) {
			for (int i = 0; i < this.addedTiles.size(); ++i) {
				TileEntity tileentity = this.addedTiles.get(i);

				if (!tileentity.isInvalid()) {
					if (!this.loadedTiles.contains(tileentity)) {
						this.loadedTiles.add(tileentity);
					}
				} else {
					if (this.chunkExists(	tileentity.xCoord >> 4,
											tileentity.zCoord >> 4)) {
						Chunk var15 = this.getChunkFromChunkCoords(	tileentity.xCoord >> 4,
																	tileentity.zCoord >> 4);

						if (var15 != null) {
							var15.cleanChunkBlockTileEntity(tileentity.xCoord & 15,
															tileentity.yCoord,
															tileentity.zCoord & 15);
						}
					}
				}
			}

			this.addedTiles.clear();
		}
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
		if (this.getRealWorld() != null) {
			return this.getRealWorld().getSkyBlockTypeBrightness(	enumskyblock,
																	x >> 3,
																	y >> 3,
																	z >> 3);
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
		if (this.getRealWorld() != null) {
			return this.getRealWorld().provider.getWorldTime();
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"getWorldTime().[null]",
																										LoggerLittleBlocks.LogLevel.DEBUG);
			return 0;
		}
	}

	@Override
	public long getTotalWorldTime() {
		if (this.getRealWorld() != null) {
			return this.getRealWorld().getWorldInfo().getWorldTotalTime();
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"getTotalWorldTime().[null]",
																										LoggerLittleBlocks.LogLevel.DEBUG);
			return 0;
		}
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
		if (this.getRealWorld() != null) {
			return this.getRealWorld().getLightBrightnessForSkyBlocks(	x >> 3,
																		y >> 3,
																		z >> 3,
																		l);
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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

	// @SideOnly(Side.CLIENT)
	// @Override
	// public float getBrightness(int x, int y, int z, int l) {
	// if (realWorld != null) {
	// return this.getRealWorld().getBrightness( x >> 3,
	// y >> 3,
	// z >> 3,
	// l);
	// } else {
	// LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(
	// this.getRealWorld().isRemote,
	// "getBrightness().[null]",
	// LoggerLittleBlocks.LogLevel.DEBUG);
	// return 0;
	// }
	// }

	@Override
	public void setLightValue(EnumSkyBlock sky, int x, int y, int z, int value) {
		if (x >= 0xfe363c80 && z >= 0xfe363c80 && x < 0x1c9c380
			&& z < 0x1c9c380) {
			if (y >= 0) {
				if (y >= this.getHeight()) {
					Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																				z >> 7);
					if (chunk.getBlockID(	(x & 0x7f) >> 3,
											y >> 3,
											(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID
						&& this.isAirBlock(	x,
											y,
											z)) {
						this.getRealWorld().setLightValue(	sky,
															x >> 3,
															y >> 3,
															z >> 3,
															MathHelper.floor_double(value / 8));
					} else if (chunk.getBlockID((x & 0x7f) >> 3,
												y >> 3,
												(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID) {
						return;
					}
					TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getRealWorld().getBlockTileEntity(x >> 3,
																												y >> 3,
																												z >> 3);
					tile.setLightValue(	x & 7,
										y & 7,
										z & 7,
										value);
					this.markBlockForRenderUpdate(	x >> 3,
													y >> 3,
													z >> 3);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBlockLightValue(int x, int y, int z) {
		if (this.getRealWorld() != null) {
			return this.getRealWorld().getBlockLightValue(	x >> 3,
															y >> 3,
															z >> 3);
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
		boolean outdated = !this.getRealWorld().equals(world);
		if (outdated) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			int id = realWorld.getChunkFromChunkCoords(	x >> 7,
														z >> 7).getBlockID(	(x & 0x7f) >> 3,
																			y >> 3,
																			(z & 0x7f) >> 3);
			if (id == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) realWorld.getBlockTileEntity(	x >> 3,
																									y >> 3,
																									z >> 3);
				int littleBlockId = tile.getBlockID(x & 7,
													y & 7,
													z & 7);
				return littleBlockId > 0 ? true : false;
			} else {
				return false;
			}
		}
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
			|| z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"getBlockId("
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"getBlockId("
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			return 0;
		} else {
			int id = realWorld.getChunkFromChunkCoords(	x >> 7,
														z >> 7).getBlockID(	(x & 0x7f) >> 3,
																			y >> 3,
																			(z & 0x7f) >> 3);
			if (id == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) realWorld.getBlockTileEntity(	x >> 3,
																									y >> 3,
																									z >> 3);
				int littleBlockId = tile.getBlockID(x & 7,
													y & 7,
													z & 7);
				return littleBlockId;
			} else {
				/** POSSIBLE FIX FOR PISTON BLOCK DUPE BUG **/
				if (BlockUtil.isPistonExtending()) return Block.bedrock.blockID;
				return id;
				/********************************************/
			}
		}
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		entity.setPosition(	entity.posX / ConfigurationLib.littleBlocksSize,
							entity.posY / ConfigurationLib.littleBlocksSize,
							entity.posZ / ConfigurationLib.littleBlocksSize);
		entity.motionX /= ConfigurationLib.littleBlocksSize;
		entity.motionY /= ConfigurationLib.littleBlocksSize;
		entity.motionZ /= ConfigurationLib.littleBlocksSize;
		entity.worldObj = this.getRealWorld();
		LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																									"spawnEntityInWorld("
																											+ entity.entityId
																											+ ").["
																											+ entity.posX
																											+ ", "
																											+ entity.posY
																											+ ", "
																											+ entity.posZ
																											+ "]",
																									LoggerLittleBlocks.LogLevel.DEBUG);
		return this.getRealWorld().spawnEntityInWorld(entity);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
			|| z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			int id = realWorld.getChunkFromChunkCoords(	x >> 7,
														z >> 7).getBlockID(	(x & 0x7f) >> 3,
																			y >> 3,
																			(z & 0x7f) >> 3);
			int metadata = realWorld.getChunkFromChunkCoords(	x >> 7,
																z >> 7).getBlockMetadata(	(x & 0x7f) >> 3,
																							y >> 3,
																							(z & 0x7f) >> 3);
			if (id == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) realWorld.getBlockTileEntity(	x >> 3,
																									y >> 3,
																									z >> 3);
				int littleMetaData = tile.getBlockMetadata(	x & 7,
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

	public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update, boolean newTile) {
		return this.setBlock(	x,
								y,
								z,
								blockID,
								newmeta,
								update);
	}

	@Override
	public boolean setBlock(int x, int y, int z, int blockID, int newmeta, int update) {
		if (x >= 0xfe363c80 && z >= 0xfe363c80 & x < 0x1c9c380 && z < 0x1c9c380) {
			if (y < 0) {
				LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																											"setBlock("
																													+ x
																													+ ", "
																													+ y
																													+ ", "
																													+ z
																													+ ", "
																													+ blockID
																													+ ", "
																													+ newmeta
																													+ ", "
																													+ update
																													+ ").[y < 0]",
																											LoggerLittleBlocks.LogLevel.DEBUG);
				return false;
			} else if (y >= this.getHeight()) {
				LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																											"setBlock("
																													+ x
																													+ ", "
																													+ y
																													+ ", "
																													+ z
																													+ ", "
																													+ blockID
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
				Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																			z >> 7);
				if (chunk.getBlockID(	(x & 0x7f) >> 3,
										y >> 3,
										(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID
					&& this.isAirBlock(	x,
										y,
										z)) {
					this.getRealWorld().setBlock(	x >> 3,
													y >> 3,
													z >> 3,
													ConfigurationLib.littleChunkID);
				} else if (chunk.getBlockID((x & 0x7f) >> 3,
											y >> 3,
											(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID) {
					return false;
				}
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getRealWorld().getBlockTileEntity(x >> 3,
																											y >> 3,
																											z >> 3);
				int originalId = 0;

				if ((update & 1) != 0) {
					originalId = tile.getBlockID(	x & 7,
													y & 7,
													z & 7);
				}

				tile.checkForLittleBlock(	x & 7,
											y & 7,
											z & 7);
				boolean flag = tile.setBlockIDWithMetadata(	x & 7,
															y & 7,
															z & 7,
															blockID,
															newmeta);

				this.updateAllLightTypes(	x,
											y,
											z);

				if (flag) {
					if ((update & 2) != 0
						&& (!this.getRealWorld().isRemote || (update & 4) == 0)) {
						this.markBlockForUpdate(x,
												y,
												z);
					}

					if (!this.getRealWorld().isRemote && (update & 1) != 0) {
						this.notifyBlockChange(	x,
												y,
												z,
												originalId);
						Block block = Block.blocksList[blockID];

						if (block != null && block.hasComparatorInputOverride()) {
							this.func_96440_m(	x,
												y,
												z,
												blockID);
						}
					}
				}
				return flag;
			}
		} else {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"setBlock("
																												+ x
																												+ ", "
																												+ y
																												+ ", "
																												+ z
																												+ ", "
																												+ blockID
																												+ ", "
																												+ newmeta
																												+ ", "
																												+ update
																												+ ").["
																												+ blockID
																												+ ", "
																												+ newmeta
																												+ "]:No Change",
																										LoggerLittleBlocks.LogLevel.ERROR);
			return false;
		}
	}

	@Override
	public boolean setBlockMetadataWithNotify(int x, int y, int z, int metadata, int update) {
		if (x >= 0xfe363c80 && z >= 0xfe363c80 && x < 0x1c9c380
			&& z < 0x1c9c380) {
			if (y < 0) {
				LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
				LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
				Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																			z >> 7);
				if (chunk.getBlockID(	(x & 0x7f) >> 3,
										y >> 3,
										(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID
					&& this.isAirBlock(	x,
										y,
										z)) {
					this.getRealWorld().setBlock(	x >> 3,
													y >> 3,
													z >> 3,
													ConfigurationLib.littleChunkID);
				} else if (chunk.getBlockID((x & 0x7f) >> 3,
											y >> 3,
											(z & 0x7f) >> 3) != ConfigurationLib.littleChunkID) {
					return false;
				}
				TileEntityLittleChunk tile = (TileEntityLittleChunk) realWorld.getBlockTileEntity(	x >> 3,
																									y >> 3,
																									z >> 3);
				boolean flag = tile.setBlockMetadata(	x & 7,
														y & 7,
														z & 7,
														metadata);

				if (flag) {
					int blockId = tile.getBlockID(	x & 7,
													y & 7,
													z & 7);

					if ((update & 2) != 0
						&& (!this.getRealWorld().isRemote || (update & 4) == 0)) {
						this.markBlockForUpdate(x,
												y,
												z);
					}

					if (!this.getRealWorld().isRemote && (update & 1) != 0) {
						this.notifyBlockChange(	x,
												y,
												z,
												blockId);
						Block block = Block.blocksList[blockId];

						if (block != null && block.hasComparatorInputOverride()) {
							this.func_96440_m(	x,
												y,
												z,
												blockId);
						}
					}
				}
				return flag;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		int xDiff = (maxX - minX) >> 1, yDiff = (maxY - minY) >> 1, zDiff = (maxZ - minZ) >> 1;
		int xMid = (minX + maxX) >> 1, yMid = (minY + maxY) >> 1, zMid = (minZ + maxZ) >> 1;

		boolean flag = this.getRealWorld().checkChunksExist((xMid >> 3) - xDiff,
															(yMid >> 3) - yDiff,
															(zMid >> 3) - zDiff,
															(xMid >> 3) + xDiff,
															(yMid >> 3) + yDiff,
															(zMid >> 3) + zDiff);

		return flag;
	}

	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
		// System.out.println("InitialWorld[" + this.toString() + "]" +
		// " | BlockID[" + blockId + "]" +
		// " | x[" + x + "]" + " | y[" + y + "]" + " | z[" + z + "]");
		LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																									"notifyBlocksOfNeighborChange("
																											+ x
																											+ ", "
																											+ y
																											+ ", "
																											+ z
																											+ ", "
																											+ blockId
																											+ ")",
																									LoggerLittleBlocks.LogLevel.DEBUG);
		this.notifyBlockOfNeighborChange(	x - 1,
											y,
											z,
											blockId);
		this.notifyBlockOfNeighborChange(	x + 1,
											y,
											z,
											blockId);
		this.notifyBlockOfNeighborChange(	x,
											y - 1,
											z,
											blockId);
		this.notifyBlockOfNeighborChange(	x,
											y + 1,
											z,
											blockId);
		this.notifyBlockOfNeighborChange(	x,
											y,
											z - 1,
											blockId);
		this.notifyBlockOfNeighborChange(	x,
											y,
											z + 1,
											blockId);
	}

	@Override
	public void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
		World world;
		int id = this.getRealWorld().getBlockId(x >> 3,
												y >> 3,
												z >> 3);
		if (id == ConfigurationLib.littleChunkID) {
			world = this;
		} else {
			x >>= 3;
			y >>= 3;
			z >>= 3;
			world = this.getRealWorld();
		}
		if (!world.isRemote) {
			// System.out.println("World[" + world.toString() + "]" +
			// " | BlockID[" + blockId + "]" +
			// " | x[" + x + "]" + " | y[" + y + "]" + " | z[" + z + "]");
			Block block = Block.blocksList[world.getBlockId(x,
															y,
															z)];
			if (block != null) {
				try {
					block.onNeighborBlockChange(world,
												x,
												y,
												z,
												blockId);
				} catch (Throwable thrown) {
					LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
				LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
			|| z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
																										"getBlockTileEntity("
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
			LoggerLittleBlocks.getInstance(Logger.filterClassName(this.getClass().toString())).write(	this.getRealWorld().isRemote,
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
			Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																		z >> 7);
			if (chunk.getBlockID(	(x & 0x7f) >> 3,
									y >> 3,
									(z & 0x7f) >> 3) == ConfigurationLib.littleChunkID) {
				TileEntity tileentity = null;
				int l;
				TileEntity tileentity1;

				if (this.scanningTiles) {
					for (l = 0; l < this.addedTiles.size(); ++l) {
						tileentity1 = (TileEntity) this.addedTiles.get(l);

						if (!tileentity1.isInvalid() && tileentity1.xCoord == x
							&& tileentity1.yCoord == y
							&& tileentity1.zCoord == z) {
							tileentity = tileentity1;
							break;
						}
					}
				}

				if (tileentity == null) {
					TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getRealWorld().getBlockTileEntity(x >> 3,
																												y >> 3,
																												z >> 3);
					tileentity = tile.getChunkBlockTileEntity(	x & 7,
																y & 7,
																z & 7);
				}

				if (tileentity == null) {
					for (l = 0; l < this.addedTiles.size(); ++l) {
						tileentity1 = (TileEntity) this.addedTiles.get(l);

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
				return this.getRealWorld().getBlockTileEntity(	x >> 3,
																y >> 3,
																z >> 3);
			}
		}
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileentity) {
		Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																	z >> 7);
		if (chunk.getBlockID(	(x & 0x7f) >> 3,
								y >> 3,
								(z & 0x7f) >> 3) == ConfigurationLib.littleChunkID) {
			if (tileentity == null || tileentity.isInvalid()) {
				return;
			}
			// System.out.println("Setting TileEntity: " +
			// tileentity.toString());
			if (tileentity.canUpdate()) {
				if (scanningTiles) {
					Iterator iterator = addedTiles.iterator();
					while (iterator.hasNext()) {
						TileEntity tileentity1 = (TileEntity) iterator.next();

						if (tileentity1.xCoord == x && tileentity1.yCoord == y
							&& tileentity1.zCoord == z) {
							tileentity1.invalidate();
							iterator.remove();
						}
					}
					addedTiles.add(tileentity);
				} else {
					loadedTiles.add(tileentity);
				}
			}
			TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getRealWorld().getBlockTileEntity(x >> 3,
																										y >> 3,
																										z >> 3);
			if (tile != null) {
				tile.setChunkBlockTileEntity(	x & 7,
												y & 7,
												z & 7,
												tileentity);
			}
			this.func_96440_m(	x,
								y,
								z,
								0);
		} else {
			this.getRealWorld().setBlockTileEntity(	x >> 3,
													y >> 3,
													z >> 3,
													tileentity);
		}
	}

	@Override
	public void addTileEntity(Collection par1Collection) {
		List dest = scanningTiles ? addedTiles : loadedTiles;
		for (Object entity : par1Collection) {
			if (((TileEntity) entity).canUpdate()) {
				dest.add(entity);
			}
		}
	}

	@Override
	public void addTileEntity(TileEntity tileentity) {
		// System.out.println("Adding TileEntity: " + tileentity.toString());
		List<TileEntity> dest = scanningTiles ? addedTiles : loadedTiles;
		if (tileentity.canUpdate()) {
			dest.add(tileentity);
		}
	}

	@Override
	public void markTileEntityForDespawn(TileEntity par1TileEntity) {
		this.tileRemoval.add(par1TileEntity);
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
			|| z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= this.getHeight()) {
			return;
		} else {
			Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																		z >> 7);
			if (chunk.getBlockID(	(x & 0x7f) >> 3,
									y >> 3,
									(z & 0x7f) >> 3) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) realWorld.getBlockTileEntity(	x >> 3,
																									y >> 3,
																									z >> 3);
				tile.removeChunkBlockTileEntity(x & 7,
												y & 7,
												z & 7);
			}
		}
	}

	@Override
	public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380
			|| z >= 0x1c9c380) {
			return _default;
		}

		Chunk chunk = this.getRealWorld().getChunkFromChunkCoords(	x >> 7,
																	z >> 7);
		if (chunk == null || chunk.isEmpty()) {
			return _default;
		}

		Block block = Block.blocksList[this.getBlockId(	x,
														y,
														z)];
		if (block == null) {
			return false;
		}

		return block.isBlockSolidOnSide(this,
										x,
										y,
										z,
										side);
	}

	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		this.getRealWorld().playSoundEffect(x
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
		this.getRealWorld().playRecord(	s,
										x >> 3,
										y >> 3,
										z >> 3);
	}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		this.getRealWorld().playAuxSFX(	x / ConfigurationLib.littleBlocksSize,
										y / ConfigurationLib.littleBlocksSize,
										z / ConfigurationLib.littleBlocksSize,
										l,
										i1);
	}

	@Override
	public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
		this.getRealWorld().spawnParticle(	s,
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
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		Vec3.xCoord *= ConfigurationLib.littleBlocksSize;
		Vec3.yCoord *= ConfigurationLib.littleBlocksSize;
		Vec3.zCoord *= ConfigurationLib.littleBlocksSize;

		Vec31.xCoord *= ConfigurationLib.littleBlocksSize;
		Vec31.yCoord *= ConfigurationLib.littleBlocksSize;
		Vec31.zCoord *= ConfigurationLib.littleBlocksSize;
		return super.rayTraceBlocks_do_do(	Vec3,
											Vec31,
											flag,
											flag1);
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
		return this.getRealWorld().getClosestPlayer(x
															/ ConfigurationLib.littleBlocksSize,
													y
															/ ConfigurationLib.littleBlocksSize,
													z
															/ ConfigurationLib.littleBlocksSize,
													distance
															/ ConfigurationLib.littleBlocksSize);
	}

	@Override
	public EntityPlayer getClosestVulnerablePlayer(double x, double y, double z, double distance) {
		return this.getClosestPlayer(	x,
										y,
										z,
										distance);
	}

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		this.getRealWorld().markBlockForUpdate(	x >> 3,
												y >> 3,
												z >> 3);
	}

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		this.getRealWorld().markBlockForRenderUpdate(	x >> 3,
														y >> 3,
														z >> 3);
	}

	@Override
	public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
		this.getRealWorld().markBlockRangeForRenderUpdate(	x >> 3,
															y >> 3,
															z >> 3,
															x2 >> 3,
															y2 >> 3,
															z2 >> 3);
	}

	@Override
	public void updateTileEntityChunkAndDoNothing(int x, int y, int z, TileEntity tileentity) {
		TileEntity tile = this.getRealWorld().getBlockTileEntity(	x >> 3,
																	y >> 3,
																	z >> 3);
		if (tile != null && tile instanceof TileEntityLittleChunk) {
			tile.onInventoryChanged();
		}
	}

	@Override
	public void idModified(int lastId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		/*
		 * int blockX = (x << 3) + littleX, blockY = (y << 3) + littleY, blockZ
		 * = (z << 3) + littleZ; this.notifyBlockChange( blockX, blockY, blockZ,
		 * blockId);
		 */
		if (this.getRealWorld() != null) {
			this.getRealWorld().updateAllLightTypes(x,
													y,
													z);
			this.getRealWorld().markBlockForRenderUpdate(	x,
															y,
															z);
		}
	}

	@Override
	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		/*
		 * int blockX = (x << 3) + littleX, blockY = (y << 3) + littleY, blockZ
		 * = (z << 3) + littleZ; this.notifyBlockChange( blockX, blockY, blockZ,
		 * blockId);
		 */
		if (this.getRealWorld() != null) {
			this.getRealWorld().updateAllLightTypes(x,
													y,
													z);
			this.getRealWorld().markBlockForRenderUpdate(	x,
															y,
															z);
		}
	}

	@Override
	public void updateLightByType(EnumSkyBlock enumSkyBlock, int x, int y, int z) {
		this.getRealWorld().updateLightByType(	enumSkyBlock,
												x >> 3,
												y >> 3,
												z >> 3);
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
		return this.getEntitiesWithinAABBExcludingEntity(	entity,
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
		return this.selectEntitiesWithinAABB(	entityClass,
												axisAlignedBB,
												(IEntitySelector) null);
	}

	@Override
	public boolean checkNoEntityCollision(AxisAlignedBB axisalignedbb, Entity entity) {
		List list = this.getEntitiesWithinAABBExcludingEntity(	(Entity) null,
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
	public boolean canPlaceEntityOnSide(int blockId, int x, int y, int z, boolean flag, int side, Entity entityPlacing, ItemStack itemstack) {
		int blockIdAt = this.getBlockId(x,
										y,
										z);
		Block blockAt = Block.blocksList[blockIdAt];
		Block block = Block.blocksList[blockId];
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
				&& (blockAt == Block.waterMoving || blockAt == Block.waterStill
					|| blockAt == Block.lavaMoving
					|| blockAt == Block.lavaStill || blockAt == Block.fire || blockAt.blockMaterial.isReplaceable())) {
				blockAt = null;
			}

			if (blockAt != null && blockAt.isBlockReplaceable(	this,
																x,
																y,
																z)) {
				blockAt = null;
			}

			return blockAt != null
					&& blockAt.blockMaterial == Material.circuits
					&& block == Block.anvil ? true : blockId > 0
														&& blockAt == null
														&& block.canPlaceBlockOnSide(	this,
																						x,
																						y,
																						z,
																						side,
																						itemstack);
		}
	}
}
