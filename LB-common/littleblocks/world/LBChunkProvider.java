package littleblocks.world;

import java.util.List;

import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.World;

public class LBChunkProvider implements IChunkProvider {
	private World worldObj;

	public LBChunkProvider(World realWorld) {
		this.worldObj = realWorld;
	}

	private void generate(byte[] par1ArrayOfByte) {
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int par1, int par2) {
		return this.provideChunk(par1, par2);
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it
	 * will generates all the blocks for the specified chunk from the map seed
	 * and chunk seed
	 */
	public Chunk provideChunk(int par1, int par2) {
		byte[] var3 = new byte[32768];
		this.generate(var3);
		Chunk var4 = new Chunk(this.worldObj, var3, par1, par2);
		return var4;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	public boolean chunkExists(int par1, int par2) {
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go. If
	 * passed false, save up to two chunks. Return true if all chunks have been
	 * saved.
	 */
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
		return false;
	}

	/**
	 * Unloads the 100 oldest chunks from memory, due to a bug with
	 * chunkSet.add() never being called it thinks the list is always empty and
	 * will not remove any chunks.
	 */
	public boolean unload100OldestChunks() {
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	public boolean canSave() {
		return false;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString() {
		return worldObj.getProviderName() + "[LB]";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the
	 * given location.
	 */
	public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType,
			int par2, int par3, int par4) {
		return null;
	}

	/**
	 * Returns the location of the closest structure of the specified type. If
	 * not found returns null.
	 */
	public ChunkPosition findClosestStructure(World par1World, String par2Str,
			int par3, int par4, int par5) {
		return null;
	}

	public int getLoadedChunkCount() {
		return 0;
	}
}
