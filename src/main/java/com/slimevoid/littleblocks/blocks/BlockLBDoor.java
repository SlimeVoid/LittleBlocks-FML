package com.slimevoid.littleblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockLBDoor extends BlockDoor {

    public BlockLBDoor(int blockID, Material material) {
        super(blockID, material);
        this.disableStats();
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return y >= world.getHeight() ? false : world.doesBlockHaveSolidTopSurface(x,
                                                                                   y - 1,
                                                                                   z)
                                                && Block.bedrock.canPlaceBlockAt(world,
                                                                                 x,
                                                                                 y,
                                                                                 z)
                                                && Block.bedrock.canPlaceBlockAt(world,
                                                                                 x,
                                                                                 y + 1,
                                                                                 z);
    }
}
