package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.world.LittleWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class LittleBlockDataHandler {

	public static LittleBlockCoordinates shiftCoordsRight(LittleBlockCoordinates coordinates, int shiftLength) {
		coordinates.x = coordinates.x >> shiftLength;
		coordinates.y = coordinates.y >> shiftLength;
		coordinates.z = coordinates.z >> shiftLength;
		return coordinates;
	}

	public static LittleBlockCoordinates shiftCoordsLeft(LittleBlockCoordinates coordinates, int shiftLength) {
		coordinates.x = coordinates.x << shiftLength;
		coordinates.y = coordinates.y << shiftLength;
		coordinates.z = coordinates.z << shiftLength;
		return coordinates;
	}

	public static boolean isLittleWorld(World world) {
		return world instanceof LittleWorld ? true : false;
	}

	public static LittleBlockCoordinates AND(LittleBlockCoordinates coordinates, int valueAND) {
		coordinates.x = coordinates.x & valueAND;
		coordinates.y = coordinates.y & valueAND;
		coordinates.z = coordinates.z & valueAND;
		return coordinates;
	}
	
	public static boolean isUseableByPlayer(TileEntity tileentity, EntityPlayer entityplayer) {
		if (tileentity != null && entityplayer != null) {
			return tileentity.worldObj.getBlockTileEntity(
					tileentity.xCoord,
					tileentity.yCoord,
					tileentity.zCoord) != tileentity ? false : 
				entityplayer.getDistanceSq(
						tileentity.xCoord / 8D + 0.5D,
						tileentity.yCoord / 8D + 0.5D,
						tileentity.zCoord / 8D + 0.5D) <= 64.0D;
		}
		return false;
	}
	
	public static boolean isUseableByPlayer(EntityPlayer entityplayer, World world, int blockId, int x, int y, int z) {
		return world.getBlockId(
				x,
				y,
				z) != blockId ? false :
					entityplayer.getDistanceSq(
							x / 8D + 0.5D,
							y / 8D + 0.5D,
							z / 8D + 0.5D) <= 64.0D;
	}
}
