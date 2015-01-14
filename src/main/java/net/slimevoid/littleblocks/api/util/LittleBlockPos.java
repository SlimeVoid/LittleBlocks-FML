package net.slimevoid.littleblocks.api.util;

import net.minecraft.util.BlockPos;

public class LittleBlockPos extends BlockPos {

	public LittleBlockPos(int x, int y, int z) {
		super(x, y, z);
	}
	
	public int getShiftedX() {
		return this.getX() << 3;
	}
	
	public int getShiftedY() {
		return this.getY() << 3;
	}
	
	public int getShiftedZ() {
		return this.getZ() << 3;
	}

}
