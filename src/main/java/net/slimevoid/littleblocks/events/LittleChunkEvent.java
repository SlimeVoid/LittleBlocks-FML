package net.slimevoid.littleblocks.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LittleChunkEvent {

    @SubscribeEvent
    public void onChunkLoad(Load event) {
        Chunk chunk = event.getChunk();
        for (Object obj : chunk.chunkTileEntityMap.values()) {
            TileEntity tileentity = (TileEntity) obj;
            if (tileentity instanceof TileEntityLittleChunk) {
                ChunkPosition chunkpos = new ChunkPosition(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
                this.setActiveChunkPosition(event.world, LittleBlocks.proxy.getLittleWorld(event.world,
                                                                              false),
                                            chunkpos,
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
                this.setActiveChunkPosition(event.world, LittleBlocks.proxy.getLittleWorld(event.world,
                                                                              false),
                                            chunkpos,
                                            false);
            }
        }
    }

    private void setActiveChunkPosition(World referenceWorld, ILittleWorld littleworld, ChunkPosition chunkposition, boolean forced) {
        if (littleworld != null) {
            littleworld.activeChunkPosition(chunkposition,
                                            forced);
        } else {
            SlimevoidCore.console(CoreLib.MOD_ID,
                                  "Could not load a little chunk within ["
                                          + referenceWorld != null ? referenceWorld.getWorldInfo().getWorldName() : "Unknown Dimension" + "]",
                                  2);
        }
    }

}
