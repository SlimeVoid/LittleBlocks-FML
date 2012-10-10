package littleblocks.render;

import java.util.ArrayList;
import java.util.List;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.ModLoader;
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
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		renderTileEntityLittleBlocks(
				(TileEntityLittleBlocks) tileentity,
				d,
				d1,
				d2,
				f);
	}

	private void renderTileEntityLittleBlocks(TileEntityLittleBlocks tile, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-tile.xCoord, -tile.yCoord, -tile.zCoord);

		GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);

		float scale = 1F / LBCore.littleBlocksSize;
		GL11.glScaled(scale, scale, scale);

		int[][][] content = tile.getContent();

		RenderBlocks littleRenderer = new RenderBlocks(tile.getLittleWorld());

		RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042 /* GL_BLEND */);
		GL11.glDisable(2884 /* GL_CULL_FACE */);
		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(7425 /* GL_SMOOTH */);
		} else {
			GL11.glShadeModel(7424 /* GL_FLAT */);
		}

		if (tessellator.isDrawing) {
			tessellator.draw();
		}
		if (!tessellator.isDrawing) {
			tessellator.startDrawingQuads();
		}
		for (int x1 = 0; x1 < content.length; x1++) {
			for (int y1 = 0; y1 < content[x1].length; y1++) {
				for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
					if (content[x1][y1][z1] > 0) {
						int blockId = content[x1][y1][z1];
						if (blockId > 0) {
							if (!tessellator.isDrawing) {
								tessellator.startDrawingQuads();
							}
							Block littleBlock = Block.blocksList[blockId];
							int[] coords = {
									(tile.xCoord << 3) + x1,
									(tile.yCoord << 3) + y1,
									(tile.zCoord << 3) + z1};
							bindTextureByName("/terrain.png");
							if (!littleBlock.isDefaultTexture) {
								if (tessellator.isDrawing) {
									tessellator.draw();
								}
								tessellator.startDrawingQuads();
								bindTextureByName(littleBlock
												.getTextureFile());
								if (LBCore.optifine) {
									littleRenderer.renderBlockByRenderType(
											littleBlock,
											coords[0],
											coords[1],
											coords[2]);
								}
								renderLittleTile(tile, littleBlock, f, x1, y1, z1);
								tessellator.draw();
								bindTextureByName("/terrain.png");
							} else {
								if (LBCore.optifine) {
									littleRenderer.renderBlockByRenderType(
											littleBlock,
											coords[0],
											coords[1],
											coords[2]);
								}
								renderLittleTile(tile, littleBlock, f, x1, y1, z1);
							}
						}
					}
				}
			}
		}

		if (tessellator.isDrawing) {
			tessellator.draw();
		}

		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		GL11.glPopMatrix();
	}
	
	public void renderLittleTile(TileEntityLittleBlocks tile, Block littleBlock, float f, int x1, int y1, int z1) {
		if (tessellator.isDrawing) {
			tessellator.draw();
		}
		if (littleBlock instanceof BlockContainer) {
			TileEntity littleTile = tile
					.getLittleWorld()
						.getBlockTileEntity(
								(tile.xCoord << 3) + x1,
								(tile.yCoord << 3) + y1,
								(tile.zCoord << 3) + z1);
			if (littleTile != null) {
				if (!(littleTile instanceof TileEntityLittleBlocks)) {
					tileEntityRenderer.renderTileEntityAt(
							littleTile,
							littleTile.xCoord,
							littleTile.yCoord,
							littleTile.zCoord,
							f);
				}
			}
		}
		if (tessellator.isDrawing) {
			tessellator.draw();
		}
		if (!tessellator.isDrawing) {
			tessellator.startDrawingQuads();
		}
	}
}
