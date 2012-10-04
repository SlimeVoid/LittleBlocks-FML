package littleblocks.blocks;

import littleblocks.blocks.core.LittleBlockCoordinates;
import littleblocks.blocks.core.LittleBlockDataHandler;

public class BlockLittleBlocksBlock {
	private int blockId;
	private int metadata;
	private LittleBlockCoordinates parentBlock;
	private LittleBlockCoordinates littleBlock;
	private LittleBlockCoordinates littleWorld;
	
	public BlockLittleBlocksBlock(int blockId, int metadata, int x, int y, int z) {
		this.blockId = blockId;
		this.metadata = metadata;
		this.littleBlock = new LittleBlockCoordinates(x, y, z);
	}
	
	public void setGlobalBlockCoords(int superX, int superY, int superZ) {
		this.parentBlock = new LittleBlockCoordinates(superX, superY, superZ);
		this.littleWorld = new LittleBlockCoordinates(superX, superY, superZ);
		LittleBlockDataHandler.shiftCoordsLeft(this.littleWorld, 3);
	}
	
	public int getParentX() {
		return this.parentBlock.x;
	}
	
	public int getParentY() {
		return this.parentBlock.y;
	}
	
	public int getParentZ() {
		return this.parentBlock.z;
	}
	
	public int getWorldX() {
		return this.littleWorld.x;
	}
	
	public int getWorldY() {
		return this.littleWorld.y;
	}
	
	public int getWorldZ() {
		return this.littleWorld.z;
	}
	
	public int getBlockId() {
		return this.blockId;
	}
	
	public int getMetaData() {
		return this.metadata;
	}

	public int getLittleX() {
		return this.littleBlock.x;
	}

	public int getLittleY() {
		return this.littleBlock.y;
	}

	public int getLittleZ() {
		return this.littleBlock.z;
	}
}
