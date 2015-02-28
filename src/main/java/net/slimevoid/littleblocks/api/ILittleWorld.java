package net.slimevoid.littleblocks.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public interface ILittleWorld extends IBlockAccess {

    public World getParentWorld();

    public boolean isOutdated(World world);

    public boolean isOutSideLittleWorld(int x, int y, int z);

    public void activeChunkPosition(BlockPos chunkposition, boolean forced);

    public List<TileEntity> getLoadedTileEntities();

    public void addLoadedTileEntity(TileEntity tile);
}
