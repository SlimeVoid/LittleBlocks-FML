package net.slimevoid.littleblocks.blocks;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.api.ILittleWorld;

public class BlockLBPistonBase extends BlockPistonBase {

    public BlockLBPistonBase(int blockID, boolean isSticky) {
        super(isSticky);
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By
     * default, passes it on to the tile entity at this location. Args: world,
     * x, y, z, blockID, EventID, event parameter
     */
    public static boolean onEventReceived(World world, int x, int y, int z, int blockID, int eventID) {
        int xOffset = x + Facing.offsetsXForSide[eventID];
        int yOffset = y + Facing.offsetsYForSide[eventID];
        int zOffset = z + Facing.offsetsZForSide[eventID];
        if (world instanceof ILittleWorld) {
            if (((ILittleWorld) world).isOutSideLittleWorld(xOffset,
                                                            yOffset,
                                                            zOffset)) {
                return false;
            }
        }
        return Blocks.piston.onBlockEventReceived(world,
                                                  x,
                                                  y,
                                                  z,
                                                  blockID,
                                                  eventID);
    }
}