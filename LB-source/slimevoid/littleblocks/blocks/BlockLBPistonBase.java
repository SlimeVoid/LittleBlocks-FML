package slimevoid.littleblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleWorld;

public class BlockLBPistonBase extends BlockPistonBase {

	public BlockLBPistonBase(int blockID, boolean isSticky) {
		super(blockID, isSticky);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		int metadata = determineOrientation(	world,
										x,
										y,
										z,
										entity);
		world.setBlockMetadataWithNotify(	x,
												y,
												z,
												metadata,
												2);

		if (!world.isRemote) {
			this.updatePistonState(	world,
									x,
									y,
									z);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		if (!world.isRemote) {
			this.updatePistonState(	world,
									x,
									y,
									z);
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote && world.getBlockTileEntity(x,
																y,
																z) == null) {
			this.updatePistonState(	world,
									x,
									y,
									z);
		}
	}

	/**
	 * handles attempts to extend or retract the piston.
	 */
	private void updatePistonState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(	x,
											y,
											z);
		int orientation = getOrientation(metadata);

		if (orientation != 7) {
			boolean flag = this.isIndirectlyPowered(world,
													x,
													y,
													z,
													orientation);

			if (flag && !isExtended(metadata)) {
				if (canExtend(	world,
								x,
								y,
								z,
								orientation)) {
					world.addBlockEvent(x,
											y,
											z,
											this.blockID,
											0,
											orientation);
				}
			} else if (!flag && isExtended(metadata)) {
				world.setBlockMetadataWithNotify(	x,
														y,
														z,
														orientation,
														2);
				world.addBlockEvent(x,
										y,
										z,
										this.blockID,
										1,
										orientation);
			}
		}
	}

	private boolean isIndirectlyPowered(World world, int x, int y, int z, int side) {
		return side != 0 && world.getIndirectPowerOutput(	x,
																y - 1,
																z,
																0) ? true : (side != 1
																				&& world.getIndirectPowerOutput(x,
																													y + 1,
																													z,
																													1) ? true : (side != 2
																																	&& world.getIndirectPowerOutput(x,
																																										y,
																																										z - 1,
																																										2) ? true : (side != 3
																																														&& world.getIndirectPowerOutput(x,
																																																							y,
																																																							z + 1,
																																																							3) ? true : (side != 5
																																																											&& world.getIndirectPowerOutput(x + 1,
																																																																				y,
																																																																				z,
																																																																				5) ? true : (side != 4
																																																																								&& world.getIndirectPowerOutput(x - 1,
																																																																																	y,
																																																																																	z,
																																																																																	4) ? true : (world.getIndirectPowerOutput(	x,
																																																																																													y,
																																																																																													z,
																																																																																													0) ? true : (world.getIndirectPowerOutput(	x,
																																																																																																									y + 2,
																																																																																																									z,
																																																																																																									1) ? true : (world.getIndirectPowerOutput(	x,
																																																																																																																					y + 1,
																																																																																																																					z - 1,
																																																																																																																					2) ? true : (world.getIndirectPowerOutput(	x,
																																																																																																																																	y + 1,
																																																																																																																																	z + 1,
																																																																																																																																	3) ? true : (world.getIndirectPowerOutput(	x - 1,
																																																																																																																																													y + 1,
																																																																																																																																													z,
																																																																																																																																													4) ? true : world.getIndirectPowerOutput(	x + 1,
																																																																																																																																																									y + 1,
																																																																																																																																																									z,
																																																																																																																																																									5)))))))))));
	}

	/**
	 * checks to see if this piston could push the blocks in front of it.
	 */
	private static boolean canExtend(World world, int x, int y, int z, int side) {
		int xOffset = x + Facing.offsetsXForSide[side];
		int yOffset = y + Facing.offsetsYForSide[side];
		int zOffset = z + Facing.offsetsZForSide[side];
		int blockPushed = 0;

		while (true) {
			if (blockPushed < 13) {
				if (yOffset <= 0 || yOffset >= world.getHeight() - 1) {
					return false;
				}

				int blockID = world.getBlockId(	xOffset,
												yOffset,
												zOffset);

				if (!world.isAirBlock(	xOffset,
											yOffset,
											zOffset)) {
					if (!canPushBlock(	blockID,
										world,
										xOffset,
										yOffset,
										zOffset,
										true)) {
						return false;
					}

					if (Block.blocksList[blockID].getMobilityFlag() != 1) {
						if (blockPushed == 12) {
							return false;
						}

						xOffset += Facing.offsetsXForSide[side];
						yOffset += Facing.offsetsYForSide[side];
						zOffset += Facing.offsetsZForSide[side];
						++blockPushed;
						continue;
					}
				}
			}

			return true;
		}
	}

	private static boolean canPushBlock(int blockID, World world, int x, int y, int z, boolean mobility) {
		if (blockID == Block.obsidian.blockID) {
			return false;
		} else {
			if (blockID != Block.pistonBase.blockID
				&& blockID != Block.pistonStickyBase.blockID) {
				if (Block.blocksList[blockID].getBlockHardness(world,
															x,
															y,
															z) == -1.0F) {
					return false;
				}
				boolean littleFlag = false;
				if (world instanceof ILittleWorld) {
					littleFlag = ((ILittleWorld) world).isOutSideLittleWorld(	x,
																					y,
																					z);
				}
				if (Block.blocksList[blockID].getMobilityFlag() == 2 || littleFlag) {
					return false;
				}

				if (Block.blocksList[blockID].getMobilityFlag() == 1) {
					if (!mobility) {
						return false;
					}

					return true;
				}
			} else if (isExtended(world.getBlockMetadata(	x,
																y,
																z))) {
				return false;
			}

			return !world.blockHasTileEntity(	x,
													y,
													z);
		}
	}

}