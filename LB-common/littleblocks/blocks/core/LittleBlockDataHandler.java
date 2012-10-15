package littleblocks.blocks.core;

import littleblocks.world.LittleWorld;
import net.minecraft.src.World;

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
}
