package net.slimevoid.littleblocks.world.chunk;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

/**
 * Created by Greg on 02/03/15.
 */
public class LittleChunkProviderServer extends ChunkProviderServer {

    public LittleChunkProviderServer(WorldServer world, IChunkLoader loader, IChunkProvider provider) {
        super(world, loader, provider);
    }
}
