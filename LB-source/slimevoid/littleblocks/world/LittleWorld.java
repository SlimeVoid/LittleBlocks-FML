package slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeDirection;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eurysmods.data.Logger;

public class LittleWorld extends World {

	protected int ticksInWorld = 0;
	protected static final int MAX_TICKS_IN_WORLD = 5;

	protected World realWorld;

	@SideOnly(Side.CLIENT)
	public LittleWorld(World world) {
		super(
				world.getSaveHandler(),
					"LittleBlocksWorld",
					LBCore.littleProviderClient,
					new WorldSettings(world.getWorldInfo().getSeed(), world
							.getWorldInfo()
								.getGameType(), world
							.getWorldInfo()
								.isMapFeaturesEnabled(), world
							.getWorldInfo()
								.isHardcoreModeEnabled(), world
							.getWorldInfo()
								.getTerrainType()), null);
		this.realWorld = world;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"LittleWorld[" + world.toString() + "].("+ 
						world.getWorldInfo().getSeed() + ", " +
						world.getWorldInfo().getGameType() + ", " +
						world.getWorldInfo().isMapFeaturesEnabled() + ", " +
						world.getWorldInfo().isHardcoreModeEnabled() + ", " +
						world.getWorldInfo().getTerrainType() + ").realWorld[" + realWorld.toString() + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
	}

	public LittleWorld(World world, WorldProvider worldprovider) {
		super(world.getSaveHandler(), "LittleBlocksWorld", new WorldSettings(
				world.getWorldInfo().getSeed(),
					world.getWorldInfo().getGameType(),
					world.getWorldInfo().isMapFeaturesEnabled(),
					world.getWorldInfo().isHardcoreModeEnabled(),
					world.getWorldInfo().getTerrainType()), worldprovider, null);
		this.realWorld = world;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"LittleWorld[" + world.toString() + "].("+ 
						world.getWorldInfo().getSeed() + ", " +
						world.getWorldInfo().getGameType() + ", " +
						world.getWorldInfo().isMapFeaturesEnabled() + ", " +
						world.getWorldInfo().isHardcoreModeEnabled() + ", " +
						world.getWorldInfo().getTerrainType() + ").realWorld[" + realWorld.toString() + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
	}

	@Override
	public void tick() {
		super.tick();
		this.ticksInWorld++;
        this.func_82738_a(this.getTotalWorldTime() + 1L);
		this.setWorldTime(this.getWorldTime() + 1L);
		if (this.ticksInWorld >= MAX_TICKS_IN_WORLD) {
			this.ticksInWorld = 0;
		}
	}

	/**
	 * Runs through the list of updates to run and ticks them
	 */
	@Override
	public boolean tickUpdates(boolean tick) {
		return false;
	}

	@Override
	public int getSkyBlockTypeBrightness(EnumSkyBlock enumskyblock, int x, int y, int z) {
		if (this.realWorld != null) {
			return this.realWorld.getSkyBlockTypeBrightness(enumskyblock, x >> 3, y >> 3, z >> 3);
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getSkyBlockTypeBrightness(" +
					enumskyblock + ", " + 
					x + ", " + y + ", " + z + ").[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	@Override
    public long getWorldTime() {
		if (this.realWorld != null) {
			return this.realWorld.provider.getWorldTime();
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getWorldTime().[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
    }

	@Override
	public long getTotalWorldTime() {
		if (this.realWorld != null) {
			return this.realWorld.getWorldInfo().getWorldTotalTime();
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getTotalWorldTime().[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int l) {
		if (this.realWorld != null) {
			return realWorld.getLightBrightnessForSkyBlocks(
				x >> 3,
				y >> 3,
				z >> 3,
				l);	
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getLightBrightnessForSkyBlocks(" + x + ", " + y + ", " + z + ").[" + l + "]:Null",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getBrightness(int x, int y, int z, int l) {
		if (realWorld != null) {
			return realWorld.getBrightness(x >> 3, y >> 3, z >> 3, l);
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBrightness().[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBlockLightValue(int x, int y, int z) {
		if (realWorld != null) {
			return realWorld.getBlockLightValue(x >> 3, y >> 3, z >> 3);
		} else {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockLightValue(" + x + ", " + y + ", " + z + ").[null]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
	}

	@Override
	public void setSpawnLocation() {
	}

	public boolean isOutdated(World world) {
		boolean outdated = !realWorld.equals(world);
		if (outdated) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isOutDated(" + world.toString() + ").[" +
					outdated + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return outdated;
	}
	
	@Override
	public boolean blockExists(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y >= "+ this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleBlockId = tile.getContent(x & 7, y & 7, z & 7);
				return littleBlockId > 0 ? true : false;
			} else {
				return false;
			}
		}
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockId(" + x + ", " + y + ", " + z + ").[y >= "+ this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleBlockId = tile.getContent(x & 7, y & 7, z & 7);
				return littleBlockId;
			} else {
				return id;
			}
		}
	}

	@Override
	public boolean spawnEntityInWorld(Entity entity) {
		entity.setPosition(
				entity.posX / LBCore.littleBlocksSize,
				entity.posY / LBCore.littleBlocksSize,
				entity.posZ / LBCore.littleBlocksSize);
		entity.motionX /= LBCore.littleBlocksSize;
		entity.motionY /= LBCore.littleBlocksSize;
		entity.motionZ /= LBCore.littleBlocksSize;
		entity.worldObj = this.realWorld;
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"spawnEntityInWorld(" + entity.entityId + ").[" +
						entity.posX + ", " +
						entity.posY + ", " +
						entity.posZ + "]",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
		return this.realWorld.spawnEntityInWorld(entity);
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return 0;
		} else {
			int id = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3);
			int metadata = realWorld
					.getChunkFromChunkCoords(x >> 7, z >> 7)
						.getBlockMetadata(
								(x & 0x7f) >> 3,
								y >> 3,
								(z & 0x7f) >> 3);
			if (id == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int littleBlockId = tile.getContent(x & 7, y & 7, z & 7);
				int littleMetaData = tile.getMetadata(x & 7, y & 7, z & 7);
				return littleMetaData;
			} else {
				return metadata;
			}
		}
	}

	@Override
	public int getHeight() {
		return super.getHeight() * LBCore.littleBlocksSize;
	}

	@Override
	public boolean setBlockAndMetadata(int x, int y, int z, int id, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						x >> 3,
						y >> 3,
						z >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
			if (currentId != id || currentData != metadata) {
				tile.setContent(x & 7, y & 7, z & 7, id, metadata);
				flag = true;
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlockAndMetadata(" + x + ", " + y + ", " + z + ").[" + id + ", "+ metadata + "]:No Change",
						LoggerLittleBlocks.LogLevel.ERROR
				);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return flag;
		}
	}

	/**
	 * Sets the block ID and metadata of a block, optionally marking it as
	 * needing update. Args: X,Y,Z, blockID, metadata, needsUpdate
	 */
	@Override
	public boolean setBlockAndMetadataWithUpdate(int x, int y, int z, int blockId, int metadata, boolean needsUpdate) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			boolean flag = false;
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntity tileentity = realWorld.getBlockTileEntity(
					x >> 3,
					y >> 3,
					z >> 3);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tileentitylb = (TileEntityLittleBlocks) tileentity;
				int currentId = tileentitylb.getContent(x & 7, y & 7, z & 7);
				int currentData = tileentitylb.getMetadata(x & 7, y & 7, z & 7);
				if (currentId != blockId || currentData != metadata) {
					tileentitylb.setContent(
							x & 7,
							y & 7,
							z & 7,
							blockId,
							metadata);
					flag = true;
				} else {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							this.isRemote,
							"setBlockAndMetadataWithUpdate(" + x + ", " + y + ", " + z + ").[" + blockId + ", " + metadata + "]:No Change",
							LoggerLittleBlocks.LogLevel.DEBUG
					);
				}
				this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
				this.markBlockForRenderUpdate(x, y, z);
			}
			return flag;
		}
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlock(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			boolean flag = false;
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) != LBCore.littleBlocksID) {
				realWorld.setBlockWithNotify(
						(x) >> 3,
						y >> 3,
						(z) >> 3,
						LBCore.littleBlocksID);
			}
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
					.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			int currentId = tile.getContent(x & 7, y & 7, z & 7);
			if (currentId != id) {
				tile.setContent(x & 7, y & 7, z & 7, id);
				flag = true;
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlock(" + x + ", " + y + ", " + z + ").[" + id + "]:No Change",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return flag;
		}
	}

	@Override
	public boolean setBlockMetadata(int x, int y, int z, int metadata) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		}
		if (y >= this.getHeight()) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"setBlockMetadata(" + x + ", " + y + ", " + z + ").[y >= " + this.getHeight() + "]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return false;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				int id = tile.getContent(x & 7, y & 7, z & 7);
				int currentData = tile.getMetadata(x & 7, y & 7, z & 7);
				if (currentData != metadata) {
					tile.setMetadata(x & 7, y & 7, z & 7, metadata);
				} else {
					LoggerLittleBlocks.getInstance(
							Logger.filterClassName(
									this.getClass().toString()
							)
					).write(
							this.isRemote,
							"setBlockMetadata(" + x + ", " + y + ", " + z + ").[" + id + ", " + metadata + "]:No Change",
							LoggerLittleBlocks.LogLevel.DEBUG
					);
				}
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"setBlockMetadata(" + x + ", " + y + ", " + z + ").[chunkData]",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
				chunk.setBlockMetadata(
						(x & 0x7f) >> 3,
						y >> 3,
						(z & 0x7f) >> 3,
						metadata);
			}
			this.realWorld.updateAllLightTypes(x >> 3, y >> 3, z >> 3);
			this.markBlockForRenderUpdate(x, y, z);
			return true;
		}
	}

	@Override
	public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		int xDiff = (maxX - minX) >> 1, yDiff = (maxY - minY) >> 1, zDiff = (maxZ - minZ) >> 1;
		int xMid = (minX + maxX) >> 1, yMid = (minY + maxY) >> 1, zMid = (minZ + maxZ) >> 1;

		boolean flag = realWorld.checkChunksExist(
				(xMid >> 3) - xDiff,
				(yMid >> 3) - yDiff,
				(zMid >> 3) - zDiff,
				(xMid >> 3) + xDiff,
				(yMid >> 3) + yDiff,
				(zMid >> 3) + zDiff);

		return flag;
	}

	@Override
	public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
		LoggerLittleBlocks.getInstance(
				Logger.filterClassName(
						this.getClass().toString()
				)
		).write(
				this.isRemote,
				"notifyBlocksOfNeighborChange(" + x + ", " + y + ", " + z + ", " + blockId + ")",
				LoggerLittleBlocks.LogLevel.DEBUG
		);
		notifyBlockOfNeighborChange(x - 1, y, z, blockId);
		notifyBlockOfNeighborChange(x + 1, y, z, blockId);
		notifyBlockOfNeighborChange(x, y - 1, z, blockId);
		notifyBlockOfNeighborChange(x, y + 1, z, blockId);
		notifyBlockOfNeighborChange(x, y, z - 1, blockId);
		notifyBlockOfNeighborChange(x, y, z + 1, blockId);
	}

	private void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
		World world;
		int id = this.realWorld.getBlockId(x >> 3, y >> 3, z >> 3);
		if (id == LBCore.littleBlocksID) {
			world = this;
		} else {
			x >>= 3;
			y >>= 3;
			z >>= 3;
			world = this.realWorld;
		}
		if (!realWorld.editingBlocks && !world.isRemote) {
			Block block = Block.blocksList[world.getBlockId(x, y, z)];
			if (block != null) {
				block.onNeighborBlockChange(world, x, y, z, blockId);
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"onNeighborBlockChange(" + x + ", " + y + ", " + z + ", " + blockId + ").[" + block.getBlockName() + "]",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
				world.markBlockForRenderUpdate(x, y, z);
			} else {
				LoggerLittleBlocks.getInstance(
						Logger.filterClassName(
								this.getClass().toString()
						)
				).write(
						this.isRemote,
						"notifyBlockOfNeighborChange(" + x + ", " + y + ", " + z + ", " + blockId + "):Null",
						LoggerLittleBlocks.LogLevel.DEBUG
				);
			}
		}
	}

	public void idModified(int lastBlockId, int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);
		if (this.realWorld != null) {
			this.realWorld.updateAllLightTypes(x, y, z);
			this.realWorld.markBlockForRenderUpdate(x, y, z);
		}
	}

	public void metadataModified(int x, int y, int z, int side, int littleX, int littleY, int littleZ, int blockId, int metadata) {
		int blockX = (x << 3) + littleX,
			blockY = (y << 3) + littleY,
			blockZ = (z << 3) + littleZ;
		this.notifyBlockChange(
				blockX,
				blockY,
				blockZ,
				blockId);
		if (this.realWorld != null) {
			this.realWorld.updateAllLightTypes(x, y, z);
			this.realWorld.markBlockForRenderUpdate(x, y, z);
		}
	}

	@Override
	public TileEntity getBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockTileEntity(" + x + ", " + y + ", " + z + ").[Out of bounds]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return null;
		}
		if (y < 0) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"getBlockMetadata(" + x + ", " + y + ", " + z + ").[y < 0]",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
			return null;
		}
		if (y >= this.getHeight()) {
			return null;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				return tile.getTileEntity(x & 7, y & 7, z & 7);
			} else {
				return realWorld.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
			}
		}
	}

	@Override
	public void setBlockTileEntity(int x, int y, int z, TileEntity tileEntity) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= this.getHeight()) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.setTileEntity(x & 7, y & 7, z & 7, tileEntity);
			} else {
				realWorld
						.setBlockTileEntity(x >> 3, y >> 3, z >> 3, tileEntity);
			}
			return;
		}
	}

	@Override
	public void removeBlockTileEntity(int x, int y, int z) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return;
		}
		if (y < 0) {
			return;
		}
		if (y >= this.getHeight()) {
			return;
		} else {
			Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
			if (chunk.getBlockID((x & 0x7f) >> 3, y >> 3, (z & 0x7f) >> 3) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) realWorld
						.getBlockTileEntity(x >> 3, y >> 3, z >> 3);
				tile.removeTileEntity(x & 7, y & 7, z & 7);
			}
		}
	}

	@Override
	public boolean isBlockSolidOnSide(int x, int y, int z, ForgeDirection side, boolean _default) {
		if (x < 0xfe363c80 || z < 0xfe363c80 || x >= 0x1c9c380 || z >= 0x1c9c380) {
			return _default;
		}

		Chunk chunk = realWorld.getChunkFromChunkCoords(x >> 7, z >> 7);
		if (chunk == null || chunk.isEmpty()) {
			return _default;
		}

		Block block = Block.blocksList[getBlockId(x, y, z)];
		if (block == null) {
			return false;
		}

		return block.isBlockSolidOnSide(this, x, y, z, side);
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		this.realWorld.markBlockForUpdate(x >> 3, y >> 3, z >> 3);
	}

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		this.realWorld.markBlockForRenderUpdate(x >> 3, y >> 3, z >> 3);
	}
	
	@Override
    public void markBlockRangeForRenderUpdate(int x, int y, int z, int x2, int y2, int z2) {
		this.realWorld.markBlockRangeForRenderUpdate(x >> 3, y >> 3, z >> 3, x2 >> 3, y2 >> 3, z2 >> 3);
	}

	@Override
	public void playSoundEffect(double x, double y, double z, String s, float f, float f1) {
		this.realWorld.playSoundEffect(
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				s,
				f,
				f1);
	}

	@Override
	public void playRecord(String s, int x, int y, int z) {
		this.realWorld.playRecord(s, x, y, z);
	}

	@Override
	public void playAuxSFX(int x, int y, int z, int l, int i1) {
		this.realWorld.playAuxSFX(
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				l,
				i1);
	}

	@Override
	public void spawnParticle(String s, double x, double y, double z, double d3, double d4, double d5) {
		this.realWorld.spawnParticle(
				s,
				x / LBCore.littleBlocksSize,
				y / LBCore.littleBlocksSize,
				z / LBCore.littleBlocksSize,
				d3,
				d4,
				d5);
	}

	@Override
	public MovingObjectPosition rayTraceBlocks_do_do(Vec3 Vec3, Vec3 Vec31, boolean flag, boolean flag1) {
		Vec3.xCoord *= LBCore.littleBlocksSize;
		Vec3.yCoord *= LBCore.littleBlocksSize;
		Vec3.zCoord *= LBCore.littleBlocksSize;

		Vec31.xCoord *= LBCore.littleBlocksSize;
		Vec31.yCoord *= LBCore.littleBlocksSize;
		Vec31.zCoord *= LBCore.littleBlocksSize;
		return super.rayTraceBlocks_do_do(Vec3, Vec31, flag, flag1);
	}

	@Override
    public Explosion newExplosion(Entity entity, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
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

	public World getRealWorld() {
		return this.realWorld;
	}

	@Override
	public Entity getEntityByID(int entityId) {
		return null;
	}

	@Override
	public boolean isBlockProvidingPowerTo(int x, int y, int z, int direction) {
		boolean flag = super.isBlockProvidingPowerTo(x, y, z, direction);
		/*int blockId = this.realWorld.getBlockId(x >> 3, y >> 3, z >> 3);
		boolean flag;
		if (blockId == LBCore.littleBlocksID) {
			int littleBlockId = this.getBlockId(x, y, z);
			flag = littleBlockId == 0 ? false : Block.blocksList[littleBlockId].isProvidingStrongPower(this, x, y, z, direction);
		} else {
			x >>= 3;
			y >>= 3;
			z >>= 3;
			flag = blockId == 0 ? false : Block.blocksList[blockId].isProvidingStrongPower(this.realWorld, x, y, z, direction);
		}*/
		if (flag) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isBlockProvidingPowerTo(" + x + ", " + y + ", " + z + ", " + direction + "):" + flag,
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return flag;
	}

	@Override
	public boolean isBlockGettingPowered(int x, int y, int z) {
		boolean flag = this.isBlockProvidingPowerTo(x, y - 1, z, 0) ? true : (this.isBlockProvidingPowerTo(x, y + 1, z, 1) ? true : (this.isBlockProvidingPowerTo(x, y, z - 1, 2) ? true : (this.isBlockProvidingPowerTo(x, y, z + 1, 3) ? true : (this.isBlockProvidingPowerTo(x - 1, y, z, 4) ? true : this.isBlockProvidingPowerTo(x + 1, y, z, 5)))));
		if (flag) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isBlockGettingPowered(" + x + ", " + y + ", " + z + ", " + "):" + flag,
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return flag;
	}

	@Override
	public boolean isBlockIndirectlyProvidingPowerTo(int x, int y, int z, int direction) {
		boolean flag;
		if (this.isBlockNormalCube(x, y, z)) {
			flag = this.isBlockGettingPowered(x, y, z);
		} else {
			int blockId = this.getBlockId(x, y, z);
			if (blockId > 0) {
				Block block = Block.blocksList[blockId];
				flag = block.isProvidingWeakPower(this, x, y, z, direction);
			} else {
				flag = false;
			}
		}
		if (flag) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isBlockIndirectlyProvidingPowerTo(" + x + ", " + y + ", " + z + ", " + direction + "):" + flag,
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return flag;
    }
    
	@Override
	public boolean isBlockIndirectlyGettingPowered(int x, int y, int z) {
		boolean flag = this.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0) ? true : (this.isBlockIndirectlyProvidingPowerTo(x, y + 1, z, 1) ? true : (this.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2) ? true : (this.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3) ? true : (this.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4) ? true : this.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5)))));
		if (flag) {
			LoggerLittleBlocks.getInstance(
					Logger.filterClassName(
							this.getClass().toString()
					)
			).write(
					this.isRemote,
					"isBlockIndirectlyGettingPowered(" + x + ", " + y + ", " + z + "):" + flag,
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		return flag;
	}
	
	@Override
    public EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7) {
		return this.realWorld.getClosestPlayer(par1, par3, par5, par7);
	}
	
	@Override
    public EntityPlayer getClosestVulnerablePlayer(double par1, double par3, double par5, double par7) {
		return this.realWorld.getClosestPlayer(par1, par3, par5, par7);
	}
}