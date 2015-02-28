package net.slimevoid.littleblocks.world;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.slimevoid.littleblocks.api.ILittleWorld;

import java.util.List;

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
    public Chunk provideChunk(BlockPos pos) {
        return this.provideChunk(pos.getX(),
                                 pos.getZ());
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it
     * will generates all the blocks for the specified chunk from the map seed
     * and chunk seed
     */
    @Override
    public Chunk provideChunk(int x, int z) {
        ChunkPrimer chunkprimer = new ChunkPrimer();
        Chunk chunk = new Chunk(this.littleWorld, chunkprimer, x, z);
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(IChunkProvider chunkProvider, int x, int z) {
    }

    @Override
    public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_) {
        return false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If
     * passed false, save up to two chunks. Return true if all chunks have been
     * saved.
     */
    @Override
    public boolean saveChunks(boolean par1, IProgressUpdate progressUpdate) {
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
    public List func_177458_a/*getPossibleCreatures*/(EnumCreatureType enumCreatureType, BlockPos pos) {
        return null;
    }

    /**
     * Returns the location of the closest structure of the specified type. If
     * not found returns null.
     */
    @Override
    public BlockPos getStrongholdGen(World world, String structureName, BlockPos pos) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 1;
    }

    @Override
    public void recreateStructures(Chunk chunk, int var1, int var2) {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public void saveExtraData() {
    }
}
