package slimevoid.littleblocks.api.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoidlib.ICommonProxy;
import slimevoidlib.ISlimevoidHelper;
import slimevoidlib.util.helpers.SlimevoidHelper;

public class LittleBlocksHelper implements ISlimevoidHelper {

	private static boolean	initialized	= false;
	private ICommonProxy	proxy;
	private int				size;

	/**
	 * Constructor
	 * 
	 * @param littleProxy
	 *            the LB Proxy
	 * @param littleBlocksSize
	 *            the size of LB
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

	public int getBlockId(World world, int x, int y, int z) {
		if (world != null) {
			return getWorld(world,
							x,
							y,
							z).getBlockId(	x,
											y,
											z);
		}
		return 0;
	}

	@Override
	public TileEntity getBlockTileEntity(IBlockAccess world, int x, int y, int z) {
		if (world != null) {
			IBlockAccess newWorld = this.getWorld(	world,
													x,
													y,
													z);
			if (newWorld != null) {
				TileEntity tileentity = newWorld.getBlockTileEntity(x,
																	y,
																	z);
				return tileentity;
			}
		}
		return null;
	}

	@Override
	public boolean targetExists(World world, int x, int y, int z) {
		if (world != null) {
			return ((World) getWorld(	world,
										x,
										y,
										z)).blockExists(x,
														y,
														z);
		}
		return false;
	}

	private IBlockAccess getWorld(IBlockAccess world, int x, int y, int z) {
		if (isLittleBlock(	world,
							x,
							y,
							z)) {
			return (World) ((ILBCommonProxy) proxy).getLittleWorld(	world,
																	false);
		}
		return world;
	}

	private boolean isLittleBlock(IBlockAccess world, int x, int y, int z) {
		if (world instanceof ILittleWorld) {
			return true;
		}
		if (world.getBlockTileEntity(	x >> 3,
										y >> 3,
										z >> 3) instanceof ILittleBlocks) {
			return true;
		}
		return false;
	}

	public boolean isUseableByPlayer(World world, EntityPlayer player, int xCoord, int yCoord, int zCoord, double xDiff, double yDiff, double zDiff, double distance) {
		if (isLittleBlock(	world,
							xCoord,
							yCoord,
							zCoord)) {
			return player.getDistanceSq((xCoord / size) + xDiff,
										(yCoord / size) + yDiff,
										(zCoord / size) + zDiff) <= distance;
		}
		return false;
	}

	@Override
	public String getHelperName() {
		return "LittleBlocks Helper";
	}

	/**
	 * Retrieves whether or not the block the entity has collided with is a
	 * Ladder (Mainly used in Gulliver)
	 * 
	 * Credits : Tarig and Unclemion
	 */
	@Override
	public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity) {

		TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getBlockTileEntity(	x,
																						y,
																						z);
		double minX = entity.boundingBox.minX + 0.001D;

		double minY = entity.boundingBox.minY + 0.001D;

		double minZ = entity.boundingBox.minZ + 0.001D;

		double maxX = entity.boundingBox.maxX - 0.001D;

		double maxZ = entity.boundingBox.maxZ - 0.001D;

		if (world.checkChunksExist(	MathHelper.floor_double(minX),
									MathHelper.floor_double(minY),
									MathHelper.floor_double(minZ),
									MathHelper.floor_double(maxX),
									MathHelper.floor_double(minY),
									MathHelper.floor_double(maxZ))) {

			boolean result = false;
			// X/8 = .125 solve for the floor of X .125 * 8 = 1 the floor of 1
			// is 1
			// X/8 = .123 solve for the floor of X .123 * 8 < 1 the floor of <1
			// is 0
			minX = MathHelper.floor_double((minX - MathHelper.floor_double(minX))
											* tile.size)
					+ ((MathHelper.floor_double(minX) - x) * tile.size);
			minY = MathHelper.floor_double((minY - MathHelper.floor_double(minY))
											* tile.size)
					+ ((MathHelper.floor_double(minY) - y) * tile.size);
			minZ = MathHelper.floor_double((minZ - MathHelper.floor_double(minZ))
											* tile.size)
					+ ((MathHelper.floor_double(minZ) - z) * tile.size);
			maxX = MathHelper.floor_double((maxX - MathHelper.floor_double(maxX))
											* tile.size)
					+ ((MathHelper.floor_double(maxX) - x) * tile.size);
			maxZ = MathHelper.floor_double((maxZ - MathHelper.floor_double(maxZ))
											* tile.size)
					+ ((MathHelper.floor_double(maxZ) - z) * tile.size);
			for (int littleX = (int) minX; littleX <= maxX; littleX++) {
				for (int littleZ = (int) minZ; littleZ <= maxZ; littleZ++) {
					int blockID = tile.getBlockID(	littleX,
													(int) minY,
													littleZ);
					if (blockID > 0) {
						int xx = (x << 3) + littleX, yy = (y << 3) + (int) minY, zz = (z << 3)
																						+ littleZ;
						if (Block.blocksList[blockID].isLadder(	(World) tile.getLittleWorld(),
																xx,
																yy,
																zz,
																entity)) {
							return true;
						}
					}
				}

			}
		}
		return false;
	}

	/*
	 * public boolean isLadder(World world, int x, int y, int z,
	 * EntityLivingBase entity) { TileEntityLittleChunk tile =
	 * (TileEntityLittleChunk) world .getBlockTileEntity(x, y, z); int minXHit =
	 * entity.boundingBox.minX + 0.001D < x ? 0 : MathHelper
	 * .floor_double((entity.boundingBox.minX + 0.001D) (double) tile.size) %
	 * tile.size; int yHit = MathHelper.floor_double(entity.boundingBox.minY
	 * (double) tile.size) % tile.size; int minZHit = entity.boundingBox.minZ +
	 * 0.001D < z ? 0 : MathHelper .floor_double((entity.boundingBox.minZ +
	 * 0.001D) (double) tile.size) % tile.size; int maxXHit =
	 * entity.boundingBox.maxX - 0.001D > (x + 1) ? 7 :
	 * MathHelper.floor_double((entity.boundingBox.maxX - 0.001D) (double)
	 * tile.size) % tile.size; int maxZHit = entity.boundingBox.maxZ - 0.001D >
	 * (z + 1) ? 7 : MathHelper.floor_double((entity.boundingBox.maxZ - 0.001D)
	 * (double) tile.size) % tile.size; boolean result = false; if
	 * (!cpw.mods.fml.common.Loader.isModLoaded("GulliverForged") ||
	 * (entity.boundingBox.maxY - entity.boundingBox.minY <= 1)) {
	 * //System.out.println("isVanillaLadder"); int blockid = 0; for (int xx =
	 * minXHit; xx <= maxXHit; xx++) { blockid = tile.getBlockID(xx, yHit,
	 * minZHit); if (blockid > 0) { //System.out.println("isMinZLadder(" + xx +
	 * ", " + yHit + ", " + minZHit + ", " + entity.getEntityName()); result =
	 * Block.blocksList[blockid].isLadder( (World) tile.getLittleWorld(), xx,
	 * yHit, minZHit, entity); if (result) break; } blockid =
	 * tile.getBlockID(xx, yHit, maxZHit); if (blockid > 0) {
	 * //System.out.println("isMaxZLadder(" + xx + ", " + yHit + ", " + minZHit
	 * + ", " + entity.getEntityName()); result =
	 * Block.blocksList[blockid].isLadder( (World) tile.getLittleWorld(), xx,
	 * yHit, maxZHit, entity); if (result) break; } } if (!result) { for (int zz
	 * = minZHit; zz <= maxZHit; zz++) { blockid = tile.getBlockID(minXHit,
	 * yHit, zz); if (blockid > 0) { //System.out.println("isMinXLadder(" +
	 * minXHit + ", " + yHit + ", " + zz + ", " + entity.getEntityName());
	 * result = Block.blocksList[blockid].isLadder( (World)
	 * tile.getLittleWorld(), minXHit, yHit, zz, entity); if (result) break; }
	 * blockid = tile.getBlockID(maxXHit, yHit, zz); if (blockid > 0) {
	 * //System.out.println("isMinXLadder(" + maxXHit + ", " + yHit + ", " + zz
	 * + ", " + entity.getEntityName()); result =
	 * Block.blocksList[blockid].isLadder( (World) tile.getLittleWorld(),
	 * maxXHit, yHit, zz, entity); if (result) break; } } } } return result; }
	 */
}
