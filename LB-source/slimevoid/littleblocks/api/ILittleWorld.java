package slimevoid.littleblocks.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ILittleWorld extends IBlockAccess {

	World getRealWorld();

	void idModified(int lastId, int xCoord, int yCoord, int zCoord, int i,
			int x, int y, int z, int id, int j);

	void metadataModified(int xCoord, int yCoord, int zCoord, int i, int x,
			int y, int z, int blockId, int metadata);

	void setBlockTileEntity(int x, int y, int z,
			TileEntity tileentity);
}
