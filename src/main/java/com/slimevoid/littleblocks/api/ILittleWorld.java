package com.slimevoid.littleblocks.api;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ILittleWorld extends IBlockAccess {

    public World getParentWorld();

    boolean isOutdated(World world);

    public boolean isOutSideLittleWorld(int x, int y, int z);

    public void activeChunkPosition(ChunkPosition chunkposition, boolean forced);
}
