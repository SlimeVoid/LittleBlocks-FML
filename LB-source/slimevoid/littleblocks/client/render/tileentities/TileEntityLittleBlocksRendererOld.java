package slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;

public class TileEntityLittleBlocksRendererOld extends TileEntitySpecialRenderer {
	private Tessellator tessellator = Tessellator.instance;

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
							Block littleBlock = Block.blocksList[blockId];
							if (littleBlock != null) {
								int[] coords = {
										(tile.xCoord << 3) + x1,
										(tile.yCoord << 3) + y1,
										(tile.zCoord << 3) + z1 };
								bindTextureByName("/terrain.png");
								if (!littleBlock.isDefaultTexture) {
									if (tessellator.isDrawing) {
										tessellator.draw();
									}
									if (!tessellator.isDrawing) {
										tessellator.startDrawingQuads();
									}
									bindTextureByName(littleBlock.getTextureFile());
									if (LBCore.optifine) {
										LBCore
											.getLittleRenderer(tile.getWorldObj())
												.renderBlockByRenderType(
														littleBlock,
														coords[0],
														coords[1],
														coords[2]);
									}
									if (littleBlock.hasTileEntity(0)) {
										renderLittleTile(
												tile,
												littleBlock,
												f,
												coords[0],
												coords[1],
												coords[2]);
									}
									if (tessellator.isDrawing) {
										tessellator.draw();
									}
									bindTextureByName("/terrain.png");
								} else {
									if (!tessellator.isDrawing) {
										tessellator.startDrawingQuads();
									}
									if (LBCore.optifine) {
										LBCore
											.getLittleRenderer(tile.getWorldObj())
												.renderBlockByRenderType(
														littleBlock,
														coords[0],
														coords[1],
														coords[2]);
									}
									if (littleBlock.hasTileEntity(0)) {
										renderLittleTile(
												tile,
												littleBlock,
												f,
												coords[0],
												coords[1],
												coords[2]);
									}
								}
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

	public void renderLittleTile(TileEntityLittleBlocks tile, Block littleBlock, float f, int x, int y, int z) {
		if (littleBlock.hasTileEntity(0)) {
			TileEntity littleTile = tile.getLittleWorld().getBlockTileEntity(
					x,
					y,
					z);
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
	}
}
