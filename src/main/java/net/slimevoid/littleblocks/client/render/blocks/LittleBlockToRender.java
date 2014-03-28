package net.slimevoid.littleblocks.client.render.blocks;

import net.minecraft.block.Block;

public class LittleBlockToRender {
    public Block block;
    public int   x, y, z;

    public LittleBlockToRender(Block block, int x, int y, int z) {
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
