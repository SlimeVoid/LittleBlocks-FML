package littleblocks.handlers;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;

public class LittleBlocksRotationHandler {
	TileEntityLittleBlocks tile;
	int[][][] newContent = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
	int[][][] newMetadata = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
	
	public LittleBlocksRotationHandler(TileEntityLittleBlocks tile) {
		this.tile = tile;
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
		this.tile.onInventoryChanged();
		tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
	}

}
