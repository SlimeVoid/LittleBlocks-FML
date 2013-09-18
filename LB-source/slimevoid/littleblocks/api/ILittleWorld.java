package slimevoid.littleblocks.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ILittleWorld extends IBlockAccess {

	public World getRealWorld();

	public void idModified(int lastId, int xCoord, int yCoord, int zCoord, int i,
			int x, int y, int z, int id, int j);

	public void metadataModified(int xCoord, int yCoord, int zCoord, int i, int x,
			int y, int z, int blockId, int metadata);

	public void setBlockTileEntity(int x, int y, int z,
			TileEntity tileentity);
}
