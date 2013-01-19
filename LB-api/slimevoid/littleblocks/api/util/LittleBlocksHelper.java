package slimevoid.littleblocks.api.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.core.LBInit;

public class LittleBlocksHelper {

	public static int getBlockId(World world, int x, int y,
			int z) {
		if (world != null) {
			return getWorld(world, x, y, z).getBlockId(x, y, z);
		}
		return 0;
	}

	public static TileEntity getBlockTileEntity(World world, int x, int y, int z) {
		if (world != null) {
			return getWorld(world, x, y, z).getBlockTileEntity(x, y, z);	
		}
		return null; 
	}
	
	public static boolean targetExists(World world, int x, int y, int z) {
		if (world != null) {
			return getWorld(world, x, y, z).blockExists(x, y, z);
		}
		return false;
	}

	private static World getWorld(World world, int x, int y, int z) {
		if (isLittleBlock(world, x, y, z)) {
			return (World)((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(world, false);
		}
		return world;
	}

	private static boolean isLittleBlock(World world, int x, int y, int z) {
		if (world.getBlockTileEntity(x >> 3, y >> 3, z >> 3) instanceof ILittleBlocks) {
			return true;
		}
		return false;
	}
}
