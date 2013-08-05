package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class BlockLittleChunkUseBucket {
	
	@ForgeSubscribe
	public void onUseBucket(FillBucketEvent event) {
		if (BlockUtil.isLittleBlock(event.world, event.target)) {
				if (!event.world.isRemote && event.current.getItem() instanceof ItemBucket) {
					System.out.println("Bucket");
					((BlockLittleChunk) Block.blocksList[LBCore.littleChunkID]).onServerBlockActivated(
							event.world,
							event.target.blockX >> 3,
							event.target.blockY >> 3,
							event.target.blockZ >> 3,
							event.entityPlayer,
							event.target.sideHit,
							(float) event.target.hitVec.xCoord,
							(float) event.target.hitVec.yCoord,
							(float) event.target.hitVec.zCoord,
							event.target.blockX & 7,
							event.target.blockY & 7,
							event.target.blockZ & 7,
							event.target.sideHit);
				}
		}
	}

}
