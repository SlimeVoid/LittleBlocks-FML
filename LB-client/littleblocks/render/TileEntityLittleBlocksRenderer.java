package littleblocks.render;

import java.util.ArrayList;
import java.util.List;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;

public class TileEntityLittleBlocksRenderer extends TileEntitySpecialRenderer {
	private Tessellator tessellator = Tessellator.instance;
	private List<TileEntity> entitiesToDraw = new ArrayList<TileEntity>();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1,
			double d2, float f) {
		renderTileEntityLittleBlocks((TileEntityLittleBlocks) tileentity, d,
				d1, d2, f);
	}

	private void renderTileEntityLittleBlocks(TileEntityLittleBlocks tile,
			double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-tile.xCoord, -tile.yCoord, -tile.zCoord);

		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);

		float scale = 1F / TileEntityLittleBlocks.size;
		GL11.glScaled(scale, scale, scale);

		int[][][] content = tile.getContent();

		RenderBlocks rb = new RenderBlocks(tile.getLittleWorld());

		bindTextureByName("/terrain.png");
		RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glDisable(2884 /* GL_CULL_FACE */);
		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(7425 /* GL_SMOOTH */);
		} else {
			GL11.glShadeModel(7424 /* GL_FLAT */);
		}

		tessellator.startDrawingQuads();

		entitiesToDraw.clear();
		for (int x1 = 0; x1 < content.length; x1++) {
			for (int y1 = 0; y1 < content[x1].length; y1++) {
				for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
					if (content[x1][y1][z1] > 0) {
						Block block = Block.blocksList[content[x1][y1][z1]];
						if (LBCore.optifine) {
							rb.renderBlockByRenderType(block,
									(tile.xCoord << 3) + x1, (tile.yCoord << 3)
											+ y1, (tile.zCoord << 3) + z1);
						}
						if (block instanceof BlockContainer) {
							TileEntity littleTile = tile.getLittleWorld()
									.getBlockTileEntity(
											(tile.xCoord << 3) + x1,
											(tile.yCoord << 3) + y1,
											(tile.zCoord << 3) + z1);
							if (littleTile != null) {
								if (!(littleTile instanceof TileEntityLittleBlocks)) {
									entitiesToDraw.add(littleTile);
								}
							}
						}
					}
				}
			}
		}

		tessellator.draw();

		for (TileEntity littleTile : entitiesToDraw) {
			tileEntityRenderer.renderTileEntityAt(littleTile,
					littleTile.xCoord, littleTile.yCoord, littleTile.zCoord, f);
		}

		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
	}
}
