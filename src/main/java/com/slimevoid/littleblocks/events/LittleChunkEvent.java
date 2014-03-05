package com.slimevoid.littleblocks.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;

import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LittleChunkEvent {

    @SubscribeEvent
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

    @SubscribeEvent
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
