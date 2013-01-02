package slimevoid.littleblocks.handlers;

import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import slimevoid.littleblocks.world.LittleWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class LittleBlocksRotationHandler {
	World world;
	LittleWorld littleWorld;
	EntityPlayer entityplayer;
	TileEntityLittleBlocks tile;
	int x, y, z, side;
	int[][][] newContent = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
	int[][][] newMetadata = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
	
	public LittleBlocksRotationHandler(World world, EntityPlayer entityplayer, TileEntityLittleBlocks tile, int x, int y, int z, int side) {
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
		int[][][] content = this.tile.getContent();
		int[][][] metadata = this.tile.getMetadata();
		int max = LBCore.littleBlocksSize-1;
		for (int y = 0; y < LBCore.littleBlocksSize; y++) {
			for (int x = 0; x < LBCore.littleBlocksSize; x++) {
				for (int z = 0; z < LBCore.littleBlocksSize; z++) {
					if (content[x][y][z] > 0) {
						this.newContent[max-z][y][x] = content[x][y][z];
						this.newMetadata[max-z][y][x] = metadata[x][y][z];
					}
				}
			}
		}
		for (int x = 0; x < LBCore.littleBlocksSize; x++) {
			for (int y = 0; y < LBCore.littleBlocksSize; y++) {
				for (int z = 0; z < LBCore.littleBlocksSize; z++) {
					this.tile.setContent(x, y, z, newContent[x][y][z], newMetadata[x][y][z]);
				}
			}
		}
	}

}
