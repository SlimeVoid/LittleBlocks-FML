package littleblocks.blocks.core;


import net.minecraft.world.World;

// import net.minecraftforge.event.entity.block.BlockOrientationEvent;

public class PistonOrientation {

	// @ForgeSubscribe
	public void onPistonPlacement(/* BlockOrientationEvent event */) {
		int x, y, z;
		x = 0;/* event.blockX; */
		y = 0;/* event.blockY; */
		z = 0;/* event.blockZ; */
		LittleBlockCoordinates coords = new LittleBlockCoordinates(x, y, z);
		World world;
		world = null;/* event.world; */
		if (LittleBlockDataHandler.isLittleWorld(world)) {
			LittleBlockDataHandler.shiftCoordsRight(coords, 3);
			x = coords.x;
			y = coords.y;
			z = coords.z;
		}
		// event.blockX = x;
		// event.blockY = y;
		// event.blockZ = z;
	}
}