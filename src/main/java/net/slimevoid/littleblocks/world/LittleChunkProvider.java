package net.slimevoid.littleblocks.world;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.slimevoid.littleblocks.api.ILittleWorld;

public class LittleChunkProvider implements IChunkProvider {
    private World realWorld;
    private World littleWorld;

    public LittleChunkProvider(ILittleWorld littleWorld) {
        this.realWorld = littleWorld.getParentWorld();
        this.littleWorld = (World) littleWorld;
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    @Override
    public Chunk loadChunk(int x, int y) {
        return this.provideChunk(x,
                                 y);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it
     * will generates all the blocks for the specified chunk from the map seed
     * and chunk seed
     */
    @Override
    public Chunk provideChunk(int x, int y) {
        Block[] bytes = new Block[32768];
        Chunk chunk = new Chunk(this.littleWorld, bytes, x, y);
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    @Override
    public boolean chunkExists(int par1, int par2) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If
     * passed false, save up to two chunks. Return true if all chunks have been
     * saved.
     */
    @Override
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    @Override
    public boolean canSave() {
        return false;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
    public String makeString() {
        return "[LB]" + realWorld.getProviderName();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the
     * given location.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
        return null;
    }

    /**
     * Returns the location of the closest structure of the specified type. If
     * not found returns null.
     */
    @Override
    public ChunkPosition func_147416_a/* findClosestStructure */(World par1World, String par2Str, int par3, int par4, int par5) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 1;
    }

    @Override
    public void recreateStructures(int var1, int var2) {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public void saveExtraData() {
        // TODO :: Auto-generated method stub

    }
}
