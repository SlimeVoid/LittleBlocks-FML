package slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LittleBlocks;

public class ItemInLittleWorldManager extends ItemInWorldManager {

	public ItemInLittleWorldManager(World world, EntityPlayerMP entityplayer) {
		super((World) LittleBlocks.proxy.getLittleWorld(world,
														false));
		this.thisPlayerMP = entityplayer;
	}

	@Override
	public void onBlockClicked(int x, int y, int z, int side) {
		if (!this.theWorld.extinguishFire(	(EntityPlayer) null,
											x,
											y,
											z,
											side)) {
			this.tryHarvestBlock(	x,
									y,
									z);
		}
	}

	@Override
	public boolean isCreative() {
		return this.thisPlayerMP.capabilities.isCreativeMode;
	}

	public boolean tryHarvestBlock(int x, int y, int z) {
		int blockId = this.theWorld.getBlockId(	x,
												y,
												z);
		int metadata = this.theWorld.getBlockMetadata(	x,
														y,
														z);
		this.theWorld.playAuxSFXAtEntity(	this.thisPlayerMP,
											2001,
											x,
											y,
											z,
											blockId
													+ (this.theWorld.getBlockMetadata(	x,
																						y,
																						z) << 12));
		boolean blockHarvested = false;

		if (this.isCreative()) {
			blockHarvested = this.removeBlock(	x,
												y,
												z);
		} else {
			ItemStack playerHeldItem = this.thisPlayerMP.getCurrentEquippedItem();
			boolean canHarvest = false;
			Block block = Block.blocksList[blockId];
			if (block != null) {
				canHarvest = true;
			}

			if (playerHeldItem != null) {
				playerHeldItem.onBlockDestroyed(this.theWorld,
												blockId,
												x,
												y,
												z,
												this.thisPlayerMP);
			}

			blockHarvested = this.removeBlock(	x,
												y,
												z);
			if (blockHarvested && canHarvest) {
				Block.blocksList[blockId].harvestBlock(	this.theWorld,
														this.thisPlayerMP,
														x,
														y,
														z,
														metadata);
			}
		}
		return blockHarvested;
	}

	private boolean removeBlock(int x, int y, int z) {
		Block littleBlock = Block.blocksList[this.theWorld.getBlockId(	x,
																		y,
																		z)];
		int metadata = this.theWorld.getBlockMetadata(	x,
														y,
														z);

		if (littleBlock != null) {
			littleBlock.onBlockHarvested(	this.theWorld,
											x,
											y,
											z,
											metadata,
											this.thisPlayerMP);
		}

		boolean blockIsRemoved = (littleBlock != null && littleBlock.removeBlockByPlayer(	theWorld,
																							thisPlayerMP,
																							x,
																							y,
																							z));

		if (littleBlock != null && blockIsRemoved) {
			littleBlock.onBlockDestroyedByPlayer(	this.theWorld,
													x,
													y,
													z,
													metadata);
		}

		return blockIsRemoved;
	}
}
