package slimevoid.littleblocks.blocks.events;

import static net.minecraftforge.event.Event.Result.ALLOW;
import static net.minecraftforge.event.Event.Result.DENY;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.ConfigurationLib;

public class LittleChunkBucketEvent {

    @ForgeSubscribe
    public void onBucketEvent(FillBucketEvent event) {
        if (event.current.itemID == Item.bucketEmpty.itemID) {
            if (!event.entityPlayer.canPlayerEdit(event.target.blockX,
                                                  event.target.blockY,
                                                  event.target.blockZ,
                                                  event.target.sideHit,
                                                  event.result)) {
                event.setResult(DENY);
            } else {
                if (event.world.getBlockId(event.target.blockX,
                                           event.target.blockY,
                                           event.target.blockZ) == ConfigurationLib.littleChunkID) {
                    BlockLittleChunk littleChunk = ((BlockLittleChunk) Block.blocksList[ConfigurationLib.littleChunkID]);
                    if (littleChunk.onBlockActivated(event.world,
                                                     event.target.blockX,
                                                     event.target.blockY,
                                                     event.target.blockZ,
                                                     event.entityPlayer,
                                                     event.target.sideHit,
                                                     (float) BlockLittleChunk.hitVec.xCoord,
                                                     (float) BlockLittleChunk.hitVec.yCoord,
                                                     (float) BlockLittleChunk.hitVec.zCoord)) {
                        event.setResult(ALLOW);
                    }
                }
            }
        }
    }

}
