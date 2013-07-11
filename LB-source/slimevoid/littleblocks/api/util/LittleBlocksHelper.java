package slimevoid.littleblocks.api.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import slimevoid.lib.ICommonProxy;
import slimevoid.lib.ISlimevoidHelper;
import slimevoid.lib.util.SlimevoidHelper;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LittleBlocksHelper implements ISlimevoidHelper {
	
	private static boolean initialized = false;
	private ICommonProxy proxy;
	private int size;
	
	/**
	 * Constructor
	 * 
	 * @param littleProxy the LB Proxy
	 * @param littleBlocksSize the size of LB
	 */
	public LittleBlocksHelper(ICommonProxy littleProxy, int littleBlocksSize) {
		this.proxy = littleProxy;
		this.size = littleBlocksSize;
	}

	/**
	 * Initialized the Helper
	 */
	public static void init(ICommonProxy littleProxy, int littleBlocksSize) {
		if (!initialized) {
			ISlimevoidHelper littleBlocksHelper = new LittleBlocksHelper(littleProxy, littleBlocksSize);
			SlimevoidHelper.registerHelper(littleBlocksHelper);
			initialized = true;
		}
	}

	public int getBlockId(World world, int x, int y,
			int z) {
		if (world != null) {
			return getWorld(world, x, y, z).getBlockId(x, y, z);
		}
		return 0;
	}

	public TileEntity getBlockTileEntity(World world, int x, int y, int z) {
		if (world != null) {
			return getWorld(world, x, y, z).getBlockTileEntity(x, y, z);	
		}
		return null; 
	}
	
	public boolean targetExists(World world, int x, int y, int z) {
		if (world != null) {
			return getWorld(world, x, y, z).blockExists(x, y, z);
		}
		return false;
	}

	private World getWorld(World world, int x, int y, int z) {
		if (isLittleBlock(world, x, y, z)) {
			return (World)((ILBCommonProxy)proxy).getLittleWorld(world, false);
		}
		return world;
	}

	private boolean isLittleBlock(World world, int x, int y, int z) {
		if (world instanceof ILittleWorld) {
			return true;
		}
		if (world.getBlockTileEntity(x >> 3, y >> 3, z >> 3) instanceof ILittleBlocks) {
			return true;
		}
		return false;
	}
	
	public boolean isUseableByPlayer(
			World world, 
			EntityPlayer player,
			int xCoord,
			int yCoord,
			int zCoord,
			double xDiff, 
			double yDiff, 
			double zDiff, 
			double distance) {
		if (isLittleBlock(world, xCoord, yCoord, zCoord)) {
			return player.getDistanceSq((xCoord / size) + xDiff, (yCoord / size) + yDiff, (zCoord / size) + zDiff) <= distance;
		}
		return false;
	}

	@Override
	public String getHelperName() {
		return "LittleBlocks Helper";
	}

	@Override
	public boolean isLadder(World world, int x, int y, int z,
			EntityLivingBase entity) {
		TileEntityLittleChunk tile = (TileEntityLittleChunk) world
				.getBlockTileEntity(x, y, z);
		double i = entity.boundingBox.minX + 0.001D;
		double j = entity.boundingBox.minY + 0.001D;
		double k = entity.boundingBox.minZ + 0.001D;
		double l = entity.boundingBox.maxX - 0.001D;
		double i1 = entity.boundingBox.maxY - 0.001D;
		double j1 = entity.boundingBox.maxZ - 0.001D;
		if (world.checkChunksExist(MathHelper.floor_double(i),
				MathHelper.floor_double(j), MathHelper.floor_double(k),
				MathHelper.floor_double(l), MathHelper.floor_double(i1),
				MathHelper.floor_double(j1))) {
			for (double k1 = i; k1 <= l; k1 += (1.0 / 8.0)) {
				for (double l1 = j; l1 <= i1; l1 += (1.0 / 8.0)) {
					for (double i2 = k; i2 <= j1; i2 += (1.0 / 8.0)) {
						int j2 = tile.getBlockID(MathHelper
								.floor_double((k1 - MathHelper
										.floor_double(k1)) * 100 / 8),
								MathHelper.floor_double((l1 - MathHelper
										.floor_double(l1)) * 100 / 8),
								MathHelper.floor_double((i2 - MathHelper
										.floor_double(i2)) * 100 / 8));
						if (j2 > 0) {
							int xx = (x << 3)
									+ MathHelper
											.floor_double((entity.boundingBox.minX - MathHelper
													.floor_double(entity.boundingBox.minX)) * 100 / 8)
									- 3, yy = (y << 3)
									+ MathHelper
											.floor_double((entity.boundingBox.minY - MathHelper
													.floor_double(entity.boundingBox.minY)) * 100 / 8), zz = (z << 3)
									+ MathHelper
											.floor_double((entity.boundingBox.minZ - MathHelper
													.floor_double(entity.boundingBox.minZ)) * 100 / 8)
									- 1;
							return Block.blocksList[j2].isLadder(
									(World) tile.getLittleWorld(),
									xx,
									yy,
									zz,
									entity);
						}
					}
				}
			}
		}
		return false;
	}
	

/*    public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity) {
		
		TileEntityLittleChunk tile = (TileEntityLittleChunk) world
				.getBlockTileEntity(x, y, z);
		
		int minXHit = entity.boundingBox.minX + 0.001D < x ? 0 : MathHelper
				.floor_double((entity.boundingBox.minX + 0.001D)
				* (double) tile.size)
				% tile.size;
		
		int yHit = MathHelper.floor_double(entity.boundingBox.minY
				* (double) tile.size)
				% tile.size;
		
		int minZHit = entity.boundingBox.minZ + 0.001D < z ? 0 : MathHelper
				.floor_double((entity.boundingBox.minZ + 0.001D)
				* (double) tile.size)
				% tile.size;
		
		int maxXHit = entity.boundingBox.maxX - 0.001D > (x + 1) ? 7
				: MathHelper.floor_double((entity.boundingBox.maxX - 0.001D)
				* (double) tile.size)
				% tile.size;
		
		int maxZHit = entity.boundingBox.maxZ - 0.001D > (z + 1) ? 7
				: MathHelper.floor_double((entity.boundingBox.maxZ - 0.001D)
				* (double) tile.size)
				% tile.size;
		
		boolean result = false;
		if (!cpw.mods.fml.common.Loader.isModLoaded("GulliverForged")
				|| (entity.boundingBox.maxY - entity.boundingBox.minY <= 1)) {
			//System.out.println("isVanillaLadder");
			int blockid = 0;
			for (int xx = minXHit; xx <= maxXHit; xx++) {
				blockid = tile.getBlockID(xx, yHit, minZHit);

				if (blockid > 0) {

					//System.out.println("isMinZLadder(" + xx + ", " + yHit + ", " + minZHit + ", " + entity.getEntityName());
					result = Block.blocksList[blockid].isLadder(
							(World) tile.getLittleWorld(), xx,
							yHit, minZHit, entity);
					if (result)
						break;
				}
				blockid = tile.getBlockID(xx, yHit, maxZHit);
				if (blockid > 0) {
					//System.out.println("isMaxZLadder(" + xx + ", " + yHit + ", " + minZHit + ", " + entity.getEntityName());
					result = Block.blocksList[blockid].isLadder(
							(World) tile.getLittleWorld(), xx,
							yHit, maxZHit, entity);
					if (result)
						break;
				}
			}

			if (!result) {

				for (int zz = minZHit; zz <= maxZHit; zz++) {
					blockid = tile.getBlockID(minXHit, yHit, zz);
					if (blockid > 0) {
						//System.out.println("isMinXLadder(" + minXHit + ", " + yHit + ", " + zz + ", " + entity.getEntityName());
						result = Block.blocksList[blockid].isLadder(
								(World) tile.getLittleWorld(), minXHit,
								yHit, zz, entity);
						if (result)
							break;
					}
					blockid = tile.getBlockID(maxXHit, yHit, zz);
					if (blockid > 0) {
						//System.out.println("isMinXLadder(" + maxXHit + ", " + yHit + ", " + zz + ", " + entity.getEntityName());
						result = Block.blocksList[blockid].isLadder(
								(World) tile.getLittleWorld(), maxXHit,
								yHit, zz, entity);
						if (result)
							break;
					}
				}
			}
		}

		return result;
	}*/
}
