package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LBCore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import static net.minecraftforge.event.Event.Result.ALLOW;
import static net.minecraftforge.event.Event.Result.DENY;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class BlockLittleChunkBucketEvent {
	
	@ForgeSubscribe
	public void onBucketEvent(FillBucketEvent event) {
		if (event.current.itemID == Item.bucketEmpty.itemID) {
			if (!event.entityPlayer.canPlayerEdit(event.target.blockX,event.target.blockY,event.target.blockZ, event.target.sideHit, event.result)) {
				event.setResult(DENY);
			} else {
				if (event.world.getBlockId(event.target.blockX,event.target.blockY,event.target.blockZ) == LBCore.littleChunkID) {
					BlockLittleChunk littleChunk = ((BlockLittleChunk)Block.blocksList[LBCore.littleChunkID]);
					littleChunk.onBlockActivated(event.world, event.target.blockX, event.target.blockY, event.target.blockZ, event.entityPlayer, event.target.sideHit, 0, 0, 0);
					event.setResult(ALLOW);
				}
			}
		}
	}

}
