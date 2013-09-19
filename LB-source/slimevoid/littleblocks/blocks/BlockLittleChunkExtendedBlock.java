package slimevoid.littleblocks.blocks;

import slimevoid.littleblocks.blocks.core.LittleBlockCoordinates;

public class BlockLittleChunkExtendedBlock {
	private int						blockId;
	private int						metadata;
	private LittleBlockCoordinates	parentBlock;
	private LittleBlockCoordinates	littleBlock;

	public BlockLittleChunkExtendedBlock(int x, int y, int z) {
		this.littleBlock = new LittleBlockCoordinates(x, y, z);
	}

	public BlockLittleChunkExtendedBlock(int blockId, int metadata, int x, int y, int z) {
		this.blockId = blockId;
		this.metadata = metadata;
		this.littleBlock = new LittleBlockCoordinates(x, y, z);
	}

	public void setParentCoordinates(int x, int y, int z) {
		this.parentBlock = new LittleBlockCoordinates(x, y, z);
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
		return (this.parentBlock.x << 3) + this.littleBlock.x;
	}

	public int getWorldY() {
		return (this.parentBlock.y << 3) + this.littleBlock.y;
	}

	public int getWorldZ() {
		return (this.parentBlock.z << 3) + this.littleBlock.z;
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
