package littleblocks.render;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockLittleBlocksRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (block.blockID == LBCore.littleBlocksID && !LBCore.optifine) {
			Tessellator tessellator = Tessellator.instance;

			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile == null) {
				return false;
			}

			tessellator.draw();

			GL11.glPushMatrix();

			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);

			GL11.glTranslated(xS, yS, zS);

			GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);

			float scale = 1 / 8F;
			GL11.glScalef(scale, scale, scale);

			GL11.glTranslated(-xS, -yS, -zS);

			int[][][] content = tile.getContent();

			RenderBlocks renderBlocks = new RenderBlocks(tile.getLittleWorld());

			tessellator.startDrawingQuads();
			for (int x1 = 0; x1 < content.length; x1++) {
				for (int y1 = 0; y1 < content[x1].length; y1++) {
					for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
						if (content[x1][y1][z1] > 0) {
							renderBlocks
									.renderBlockByRenderType(
											Block.blocksList[content[x1][y1][z1]],
											(x << 3) + x1, (y << 3) + y1,
											(z << 3) + z1);
						}
					}
				}
			}
			tessellator.draw();

			GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);

			GL11.glPopMatrix();

			tessellator.startDrawingQuads();

			return true;
		}
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return LBCore.renderType;
	}
}
