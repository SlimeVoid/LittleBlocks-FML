package net.slimevoid.littleblocks.world.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.slimevoid.littleblocks.core.lib.CoreLib;

/**
 * Duplicating vanilla @ExtendedBlockStorage
 * Allows us to store 512 blocks instead of 4096, we have no need (currently)
 * for a baseY
 * LittleBlockChunks are TileEntities with a given Y
 * 
 * Original Byte Data
 * 4096 2048 1024 512 256 128 64 32 16 8 4 2 1
 * 
 * New Byte Data
 * 256 128 64 32 16 8 4 2 1
 * we have 512 possible locations of x, y, z so 1, 1, 1 would translate to y <<
 * 6, z << 3, x
 * providing a given index of 64 + 8 + 1 = 73
 * Likewise if we have a coordinate of 7, 7, 7 (max location) would provide an
 * index of
 * 256 + 128 + 64 + 32 + 16 + 8 + 4 + 2 + 1 = 512 (easy peasy)
 * 
 * @author Greg
 * 
 */

public class ExtendedLittleBlockStorage {
    /**
     * A total count of the number of non-air blocks in this block storage's
     * Chunk.
     */
    private int    blockRefCount;
    /**
     * Contains the number of blocks in this block storage's parent chunk that
     * require random ticking. Used to cull the
     * Chunk from random tick updates for performance reasons.
     */
    private int    tickRefCount;
    private char[] data;
    private int size;

    public ExtendedLittleBlockStorage(int defaultSize) {
        this.data = new char[defaultSize^3];
        this.size = defaultSize;
    }

    public IBlockState get(int x, int y, int z) {
        IBlockState iblockstate = (IBlockState) Block.BLOCK_STATE_IDS
                .getByValue(this.data[y << 6 | z << 3 | x]);
        return iblockstate != null ? iblockstate : Blocks.air.getDefaultState();
    }

    public void set(int x, int y, int z, IBlockState newState) {
        IBlockState state = this.get(x,
                                           y,
                                           z);
        Block block = state.getBlock();
        Block newBlock = newState.getBlock();

        if (block != Blocks.air) {
            --this.blockRefCount;

            if (block.getTickRandomly()) {
                --this.tickRefCount;
            }
        }

        if (newBlock != Blocks.air) {
            ++this.blockRefCount;

            if (newBlock.getTickRandomly()) {
                ++this.tickRefCount;
            }
        }

        this.data[y << 6 | z << 3 | x] = (char) Block.BLOCK_STATE_IDS
                .get(state);
    }

    /**
     * Returns the block for a location in a chunk, with the extended ID merged
     * from a byte array and a NibbleArray to
     * form a full 12-bit block ID.
     */
    public Block getBlockByExtId(int x, int y, int z) {
        return this.get(x,
                        y,
                        z).getBlock();
    }

    /**
     * Returns the metadata associated with the block at the given coordinates
     * in this ExtendedBlockStorage.
     */
    public int getExtBlockMetadata(int x, int y, int z) {
        IBlockState iblockstate = this.get(x,
                                           y,
                                           z);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }

    /**
     * Returns whether or not this block storage's Chunk is fully empty, based
     * on its internal reference count.
     */
    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    /**
     * Returns whether or not this block storage's Chunk will require random
     * ticking, used to avoid looping through
     * random block ticks when there are no blocks that would randomly tick.
     */
    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    public void removeInvalidBlocks() {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int i = 0; i < this.size; ++i) {
            for (int j = 0; j < this.size; ++j) {
                for (int k = 0; k < this.size; ++k) {
                    Block block = this.getBlockByExtId(i,
                                                       j,
                                                       k);

                    if (block != Blocks.air) {
                        ++this.blockRefCount;

                        if (block.getTickRandomly()) {
                            ++this.tickRefCount;
                        }
                    }
                }
            }
        }
    }

    public char[] getData() {
        return this.data;
    }

    public void setData(char[] dataArray) {
        this.data = dataArray;
    }

}
