package slimevoid.littleblocks.client.render.entities;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.ConfigurationLib;

public class LittleBlockDiggingFX extends EntityDiggingFX {

	public LittleBlockDiggingFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, Block par14Block, int par15) {
		super(par1World, par2, par4, par6, par8, par10, par12, par14Block, par15, par1World.rand.nextInt(6));
		this.particleScale /= ConfigurationLib.littleBlocksSize;
	}

	public static boolean doBlockDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer, BlockLittleChunk block) {
		int xx = (x << 3) + BlockLittleChunk.xSelected;
		int yy = (y << 3) + BlockLittleChunk.ySelected;
		int zz = (z << 3) + BlockLittleChunk.zSelected;
		World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(	world,
																		false);
		if (littleWorld != null) {
			int blockID = littleWorld.getBlockId(	xx,
													yy,
													zz);
			Block littleBlock = Block.blocksList[blockID];
			int littleMeta = littleWorld.getBlockMetadata(	xx,
															yy,
															zz);
			if (littleBlock != null) {
				byte b0 = 4;

				for (int j1 = 0; j1 < b0; ++j1) {
					for (int k1 = 0; k1 < b0; ++k1) {
						for (int l1 = 0; l1 < b0; ++l1) {
							double d0 = (double) x + ((double) j1 + 0.5D)
										/ (double) b0;
							double d1 = (double) y + ((double) k1 + 0.5D)
										/ (double) b0;
							double d2 = (double) z + ((double) l1 + 0.5D)
										/ (double) b0;
							LittleBlockDiggingFX particle = new LittleBlockDiggingFX(world, d0, d1, d2, d0
																										- (double) x
																										- 0.5D, d1
																												- (double) y
																												- 0.5D, d2
																														- (double) z
																														- 0.5D, littleBlock, littleMeta);
							effectRenderer.addEffect(particle.applyColourMultiplier(x,
																					y,
																					z));
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean doBlockHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer, BlockLittleChunk block) {
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int xx = (x << 3) + BlockLittleChunk.xSelected;
		int yy = (y << 3) + BlockLittleChunk.ySelected;
		int zz = (z << 3) + BlockLittleChunk.zSelected;
		World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(	world,
																		false);
		if (littleWorld != null) {
			int blockID = littleWorld.getBlockId(	xx,
													yy,
													zz);
			Block littleBlock = Block.blocksList[blockID];
			int littleMeta = littleWorld.getBlockMetadata(	xx,
															yy,
															zz);
			if (littleBlock != null) {
				float f = 0.1F;
				double d0 = (double) x
							+ world.rand.nextDouble()
							* (littleBlock.getBlockBoundsMaxX()
								- littleBlock.getBlockBoundsMinX() - (double) (f * 2.0F))
							+ (double) f + littleBlock.getBlockBoundsMinX();
				double d1 = (double) y
							+ world.rand.nextDouble()
							* (littleBlock.getBlockBoundsMaxY()
								- littleBlock.getBlockBoundsMinY() - (double) (f * 2.0F))
							+ (double) f + littleBlock.getBlockBoundsMinY();
				double d2 = (double) z
							+ world.rand.nextDouble()
							* (littleBlock.getBlockBoundsMaxZ()
								- littleBlock.getBlockBoundsMinZ() - (double) (f * 2.0F))
							+ (double) f + littleBlock.getBlockBoundsMinZ();

				if (BlockLittleChunk.side == 0) {
					d1 = (double) y + littleBlock.getBlockBoundsMinY()
							- (double) f;
				}

				if (BlockLittleChunk.side == 1) {
					d1 = (double) y + littleBlock.getBlockBoundsMaxY()
							+ (double) f;
				}

				if (BlockLittleChunk.side == 2) {
					d2 = (double) z + littleBlock.getBlockBoundsMinZ()
							- (double) f;
				}

				if (BlockLittleChunk.side == 3) {
					d2 = (double) z + littleBlock.getBlockBoundsMaxZ()
							+ (double) f;
				}

				if (BlockLittleChunk.side == 4) {
					d0 = (double) x + littleBlock.getBlockBoundsMinX()
							- (double) f;
				}

				if (BlockLittleChunk.side == 5) {
					d0 = (double) x + littleBlock.getBlockBoundsMaxX()
							+ (double) f;
				}
				LittleBlockDiggingFX particle = new LittleBlockDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, littleBlock, littleMeta);
				effectRenderer.addEffect(particle.applyColourMultiplier(x,
																		y,
																		z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
			}
		}
		return true;
	}
}
