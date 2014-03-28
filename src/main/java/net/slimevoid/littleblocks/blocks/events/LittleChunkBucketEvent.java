package net.slimevoid.littleblocks.blocks.events;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LittleChunkBucketEvent {

    @SubscribeEvent
    public void onBucketEvent(FillBucketEvent event) {
        if (event.current.getItem() == Items.bucket) {
            if (!event.entityPlayer.canPlayerEdit(event.target.blockX,
                                                  event.target.blockY,
                                                  event.target.blockZ,
                                                  event.target.sideHit,
                                                  event.result)) {
                event.setResult(Event.Result.DENY);
            } else {
                if (event.world.getBlock(event.target.blockX,
                                         event.target.blockY,
                                         event.target.blockZ) == ConfigurationLib.littleChunk) {
                    BlockLittleChunk littleChunk = ((BlockLittleChunk) ConfigurationLib.littleChunk);
                    if (littleChunk.onBlockActivated(event.world,
                                                     event.target.blockX,
                                                     event.target.blockY,
                                                     event.target.blockZ,
                                                     event.entityPlayer,
                                                     event.target.sideHit,
                                                     (float) BlockLittleChunk.hitVec.xCoord,
                                                     (float) BlockLittleChunk.hitVec.yCoord,
                                                     (float) BlockLittleChunk.hitVec.zCoord)) {
                        event.setResult(Event.Result.ALLOW);
                    }
                }
            }
        }
    }

}
