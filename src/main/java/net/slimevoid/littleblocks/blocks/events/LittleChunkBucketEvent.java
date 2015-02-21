package net.slimevoid.littleblocks.blocks.events;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LittleChunkBucketEvent {

    @SubscribeEvent
    public void onBucketEvent(FillBucketEvent event) {
        if (event.current.getItem() == Items.bucket) {
            if (!event.entityPlayer./*canPlayerEdit*/func_175151_a(event.target.func_178782_a(),
                                                  event.target./*side*/field_178784_b,
                                                  event.result)) {
                event.setResult(Event.Result.DENY);
            } else {
                IBlockState blockState = event.world.getBlockState(event.target./*getBlockPos*/func_178782_a());
                if (blockState.getBlock().isAssociatedBlock(ConfigurationLib.littleChunk)) {
                    if (blockState.getBlock().onBlockActivated(event.world,
                                                     event.target./*getBlockPos*/func_178782_a(),
                                                     blockState,
                                                     event.entityPlayer,
                                                     event.target.field_178784_b,
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
