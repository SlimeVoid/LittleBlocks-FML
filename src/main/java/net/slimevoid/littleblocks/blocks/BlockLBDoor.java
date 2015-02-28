package net.slimevoid.littleblocks.blocks;

public class BlockLBDoor { /**extends BlockDoor {

    public BlockLBDoor(int blockID, Material material) {
        super(material);
        this.disableStats();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return y >= world.getHeight() ? false : World.doesBlockHaveSolidTopSurface(world,
                                                                                   x,
                                                                                   y - 1,
                                                                                   z)
                                                && Blocks.bedrock.canPlaceBlockAt(world,
                                                                                  x,
                                                                                  y,
                                                                                  z)
                                                && Blocks.bedrock.canPlaceBlockAt(world,
                                                                                  x,
                                                                                  y + 1,
                                                                                  z);
    }**/
}
