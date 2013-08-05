package slimevoid.littleblocks.blocks.core;

/**import slimevoid.littleblocks.core.LBCore;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingOnLadderEvent;
import net.minecraft.util.AxisAlignedBB;**/

public class LittleLadderHandler {

	/**@ForgeSubscribe
	public void isLivingOnLadder(LivingOnLadderEvent event) {
		if (event.block == null || event.block.blockID != LBCore.littleChunkID) {
			World world = event.world;
			AxisAlignedBB par2AxisAlignedBB = event.entityLiving.boundingBox;
			int minX = MathHelper.floor_double(par2AxisAlignedBB.minX);
			int maxX = MathHelper.floor_double(par2AxisAlignedBB.maxX + 1.0D);
			int minY = MathHelper.floor_double(par2AxisAlignedBB.minY);
			int maxY = MathHelper.floor_double(par2AxisAlignedBB.maxY + 1.0D);
			int minZ = MathHelper.floor_double(par2AxisAlignedBB.minZ);
			int maxZ = MathHelper.floor_double(par2AxisAlignedBB.maxZ + 1.0D);
	
			for (int x = minX; x < maxX; ++x) {
				for (int z = minZ; z < maxZ; ++z) {
					if (world.blockExists(x, 64, z)) {
						for (int y = minY - 1; y < maxY; ++y) {
							Block block = Block.blocksList[world.getBlockId(x, y, z)];
	
							if (block != null && block.blockID == LBCore.littleChunkID) {
								if (block.isLadder(world, x, y, z, event.entityLiving)) {
									event.setLivingIsOnLadder();
								}
							}
						}
					}
				}
			}
		}
	}**/
}
