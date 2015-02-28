package net.slimevoid.littleblocks.events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.ChunkEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LittleChunkEvent {

    @SubscribeEvent
    public void onChunkLoad(Load event) {
        Chunk chunk = event.getChunk();
        for (Object obj : chunk.getTileEntityMap().values()) {
            TileEntity tileentity = (TileEntity) obj;
            if (tileentity instanceof TileEntityLittleChunk) {
                this.setActiveChunkPosition(event.world, LittleBlocks.proxy.getLittleWorld(event.world,
                                                                              false),
                                            tileentity.getPos(),
                                            true);
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(Unload event) {
        Chunk chunk = event.getChunk();
        for (Object obj : chunk.getTileEntityMap().values()) {
            TileEntity tileentity = (TileEntity) obj;
            if (tileentity instanceof TileEntityLittleChunk) {
                this.setActiveChunkPosition(event.world, LittleBlocks.proxy.getLittleWorld(event.world,
                                                                              false),
                                            tileentity.getPos(),
                                            false);
            }
        }
    }

    private void setActiveChunkPosition(World referenceWorld, ILittleWorld littleworld, BlockPos pos, boolean forced) {
        if (littleworld != null) {
            littleworld.activeChunkPosition(pos,
                                            forced);
        } else {
            SlimevoidCore.console(CoreLib.MOD_ID,
                                  "Could not load a little chunk within ["
                                          + referenceWorld != null ? referenceWorld.getWorldInfo().getWorldName() : "Unknown Dimension" + "]",
                                  2);
        }
    }

}
