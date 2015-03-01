package net.slimevoid.littleblocks.blocks.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;

public class LittleChunkBucketEvent {

    @SubscribeEvent
    public void onBucketEvent(FillBucketEvent event) {
        if (event.current.getItem() == Items.bucket) {
            if (!event.entityPlayer.canPlayerEdit(event.target.getBlockPos(),
                                                  event.target.sideHit,
                                                  event.result)) {
                event.setResult(Event.Result.DENY);
            } else {
                IBlockState blockState = event.world.getBlockState(event.target.getBlockPos());
                if (blockState.getBlock().isAssociatedBlock(ConfigurationLib.littleChunk)) {
                    if (blockState.getBlock().onBlockActivated(event.world,
                                                     event.target.getBlockPos(),
                                                     blockState,
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
