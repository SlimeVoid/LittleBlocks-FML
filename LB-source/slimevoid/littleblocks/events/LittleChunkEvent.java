package slimevoid.littleblocks.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LittleChunkEvent {

    @ForgeSubscribe
    public void onChunkLoad(Load event) {
        Chunk chunk = event.getChunk();
        for (Object obj : chunk.chunkTileEntityMap.values()) {
            TileEntity tileentity = (TileEntity) obj;
            if (tileentity instanceof TileEntityLittleChunk) {
                ChunkPosition chunkpos = new ChunkPosition(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                LittleBlocks.proxy.getLittleWorld(event.world,
                                                  false).activeChunkPosition(chunkpos,
                                                                             true);
            }
        }
    }

    @ForgeSubscribe
    public void onChunkUnload(Unload event) {
        Chunk chunk = event.getChunk();
        for (Object obj : chunk.chunkTileEntityMap.values()) {
            TileEntity tileentity = (TileEntity) obj;
            if (tileentity instanceof TileEntityLittleChunk) {
                ChunkPosition chunkpos = new ChunkPosition(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                LittleBlocks.proxy.getLittleWorld(event.world,
                                                  false).activeChunkPosition(chunkpos,
                                                                             false);
            }
        }
    }

}
