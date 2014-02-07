package slimevoid.littleblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockLBDoor extends BlockDoor {

    public BlockLBDoor(int par1, Material par2Material) {
        super(par1, par2Material);
        this.disableStats();
    }

    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return par3 >= par1World.getHeight() ? false : par1World.doesBlockHaveSolidTopSurface(par2,
                                                                                              par3 - 1,
                                                                                              par4)
                                                       && Block.bedrock.canPlaceBlockAt(par1World,
                                                                                        par2,
                                                                                        par3,
                                                                                        par4)
                                                       && Block.bedrock.canPlaceBlockAt(par1World,
                                                                                        par2,
                                                                                        par3 + 1,
                                                                                        par4);
    }
}
