package net.slimevoid.littleblocks.api;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ILittleWorld extends IBlockAccess {

    public World getParentWorld();

    public boolean isOutdated(World world);

    public boolean isOutSideLittleWorld(int x, int y, int z);

    public void activeChunkPosition(ChunkPosition chunkposition, boolean forced);

    public List<TileEntity> getLoadedTileEntities();

    public void addLoadedTileEntity(TileEntity tile);
}
