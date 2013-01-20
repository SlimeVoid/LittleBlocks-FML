package slimevoid.littleblocks.api.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.ICommonProxy;
import slimevoid.lib.ISlimevoidHelper;
import slimevoid.lib.core.SlimevoidHelper;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleBlocks;

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
}
