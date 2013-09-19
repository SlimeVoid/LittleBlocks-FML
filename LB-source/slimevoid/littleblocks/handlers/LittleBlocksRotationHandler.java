package slimevoid.littleblocks.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LittleBlocksRotationHandler {
	World					world;
	ILittleWorld			littleWorld;
	EntityPlayer			entityplayer;
	TileEntityLittleChunk	tile;
	int						x, y, z, side;
	int[][][]				newContent	= new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
	int[][][]				newMetadata	= new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];

	public LittleBlocksRotationHandler(World world, EntityPlayer entityplayer, TileEntityLittleChunk tile, int x, int y, int z, int side) {
		this.world = world;
		this.littleWorld = tile.getLittleWorld();
		this.entityplayer = entityplayer;
		this.tile = tile;
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	public void rotateTile() {
		int[][][] content = this.tile.getContents();
		int[][][] metadata = this.tile.getMetadatas();
		int max = ConfigurationLib.littleBlocksSize - 1;
		for (int y = 0; y < ConfigurationLib.littleBlocksSize; y++) {
			for (int x = 0; x < ConfigurationLib.littleBlocksSize; x++) {
				for (int z = 0; z < ConfigurationLib.littleBlocksSize; z++) {
					if (content[x][y][z] > 0) {
						this.newContent[max - z][y][x] = content[x][y][z];
						this.newMetadata[max - z][y][x] = metadata[x][y][z];
					}
				}
			}
		}
		for (int x = 0; x < ConfigurationLib.littleBlocksSize; x++) {
			for (int y = 0; y < ConfigurationLib.littleBlocksSize; y++) {
				for (int z = 0; z < ConfigurationLib.littleBlocksSize; z++) {
					this.tile.setBlockIDWithMetadata(	x,
														y,
														z,
														newContent[x][y][z],
														newMetadata[x][y][z]);
				}
			}
		}
	}

}
