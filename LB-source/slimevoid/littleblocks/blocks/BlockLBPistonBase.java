package slimevoid.littleblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleWorld;

public class BlockLBPistonBase extends BlockPistonBase {

	public BlockLBPistonBase(int par1, boolean par2) {
		super(par1, par2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
		int l = determineOrientation(	par1World,
										par2,
										par3,
										par4,
										par5EntityLiving);
		par1World.setBlockMetadataWithNotify(	par2,
												par3,
												par4,
												l,
												2);

		if (!par1World.isRemote) {
			this.updatePistonState(	par1World,
									par2,
									par3,
									par4);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z,
	 * neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		if (!par1World.isRemote) {
			this.updatePistonState(	par1World,
									par2,
									par3,
									par4);
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		if (!par1World.isRemote && par1World.getBlockTileEntity(par2,
																par3,
																par4) == null) {
			this.updatePistonState(	par1World,
									par2,
									par3,
									par4);
		}
	}

	/**
	 * handles attempts to extend or retract the piston.
	 */
	private void updatePistonState(World par1World, int par2, int par3, int par4) {
		int l = par1World.getBlockMetadata(	par2,
											par3,
											par4);
		int i1 = getOrientation(l);

		if (i1 != 7) {
			boolean flag = this.isIndirectlyPowered(par1World,
													par2,
													par3,
													par4,
													i1);

			if (flag && !isExtended(l)) {
				if (canExtend(	par1World,
								par2,
								par3,
								par4,
								i1)) {
					par1World.addBlockEvent(par2,
											par3,
											par4,
											this.blockID,
											0,
											i1);
				}
			} else if (!flag && isExtended(l)) {
				par1World.setBlockMetadataWithNotify(	par2,
														par3,
														par4,
														i1,
														2);
				par1World.addBlockEvent(par2,
										par3,
										par4,
										this.blockID,
										1,
										i1);
			}
		}
	}

	private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4, int par5) {
		return par5 != 0 && par1World.getIndirectPowerOutput(	par2,
																par3 - 1,
																par4,
																0) ? true : (par5 != 1
																				&& par1World.getIndirectPowerOutput(par2,
																													par3 + 1,
																													par4,
																													1) ? true : (par5 != 2
																																	&& par1World.getIndirectPowerOutput(par2,
																																										par3,
																																										par4 - 1,
																																										2) ? true : (par5 != 3
																																														&& par1World.getIndirectPowerOutput(par2,
																																																							par3,
																																																							par4 + 1,
																																																							3) ? true : (par5 != 5
																																																											&& par1World.getIndirectPowerOutput(par2 + 1,
																																																																				par3,
																																																																				par4,
																																																																				5) ? true : (par5 != 4
																																																																								&& par1World.getIndirectPowerOutput(par2 - 1,
																																																																																	par3,
																																																																																	par4,
																																																																																	4) ? true : (par1World.getIndirectPowerOutput(	par2,
																																																																																													par3,
																																																																																													par4,
																																																																																													0) ? true : (par1World.getIndirectPowerOutput(	par2,
																																																																																																									par3 + 2,
																																																																																																									par4,
																																																																																																									1) ? true : (par1World.getIndirectPowerOutput(	par2,
																																																																																																																					par3 + 1,
																																																																																																																					par4 - 1,
																																																																																																																					2) ? true : (par1World.getIndirectPowerOutput(	par2,
																																																																																																																																	par3 + 1,
																																																																																																																																	par4 + 1,
																																																																																																																																	3) ? true : (par1World.getIndirectPowerOutput(	par2 - 1,
																																																																																																																																													par3 + 1,
																																																																																																																																													par4,
																																																																																																																																													4) ? true : par1World.getIndirectPowerOutput(	par2 + 1,
																																																																																																																																																									par3 + 1,
																																																																																																																																																									par4,
																																																																																																																																																									5)))))))))));
	}

	/**
	 * checks to see if this piston could push the blocks in front of it.
	 */
	private static boolean canExtend(World par0World, int par1, int par2, int par3, int par4) {
		int i1 = par1 + Facing.offsetsXForSide[par4];
		int j1 = par2 + Facing.offsetsYForSide[par4];
		int k1 = par3 + Facing.offsetsZForSide[par4];
		int l1 = 0;

		while (true) {
			if (l1 < 13) {
				if (j1 <= 0 || j1 >= par0World.getHeight() - 1) {
					return false;
				}

				int i2 = par0World.getBlockId(	i1,
												j1,
												k1);

				if (!par0World.isAirBlock(	i1,
											j1,
											k1)) {
					if (!canPushBlock(	i2,
										par0World,
										i1,
										j1,
										k1,
										true)) {
						return false;
					}

					if (Block.blocksList[i2].getMobilityFlag() != 1) {
						if (l1 == 12) {
							return false;
						}

						i1 += Facing.offsetsXForSide[par4];
						j1 += Facing.offsetsYForSide[par4];
						k1 += Facing.offsetsZForSide[par4];
						++l1;
						continue;
					}
				}
			}

			return true;
		}
	}

	private static boolean canPushBlock(int par0, World par1World, int par2, int par3, int par4, boolean par5) {
		if (par0 == Block.obsidian.blockID) {
			return false;
		} else {
			if (par0 != Block.pistonBase.blockID
				&& par0 != Block.pistonStickyBase.blockID) {
				if (Block.blocksList[par0].getBlockHardness(par1World,
															par2,
															par3,
															par4) == -1.0F) {
					return false;
				}
				boolean littleFlag = false;
				if (par1World instanceof ILittleWorld) {
					littleFlag = ((ILittleWorld) par1World).isOutSideLittleWorld(	par2,
																					par3,
																					par4);
				}
				if (Block.blocksList[par0].getMobilityFlag() == 2 || littleFlag) {
					return false;
				}

				if (Block.blocksList[par0].getMobilityFlag() == 1) {
					if (!par5) {
						return false;
					}

					return true;
				}
			} else if (isExtended(par1World.getBlockMetadata(	par2,
																par3,
																par4))) {
				return false;
			}

			return !par1World.blockHasTileEntity(	par2,
													par3,
													par4);
		}
	}

}