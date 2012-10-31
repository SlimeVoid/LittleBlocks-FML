package littleblocks.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.WorldClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockLittleBlocksRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block.blockID == LBCore.littleBlocksID) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile == null) {
				return false;
			}

			int[][][] content = tile.getContent();

			Tessellator tessellator = Tessellator.instance;		
			
			if (tessellator.isDrawing) {
				tessellator.draw();
			}
			
			GL11.glPushMatrix();
			tessellator.startDrawingQuads();
			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);

			GL11.glTranslated(xS, yS, zS);
			GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
			float scale = 1 / (float) LBCore.littleBlocksSize;
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(-xS, -yS, -zS);
			
			int defaultTexture = ModLoader.getMinecraftInstance().renderEngine
					.getTexture("/terrain.png");
			for (int x1 = 0; x1 < content.length; x1++) {
				for (int y1 = 0; y1 < content[x1].length; y1++) {
					for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
						int blockId = content[x1][y1][z1];
						if (blockId > 0) {
							Block littleBlock = Block.blocksList[blockId];
							int[] coords = {
									(x << 3) + x1,
									(y << 3) + y1,
									(z << 3) + z1 };
							if (!littleBlock.isDefaultTexture) {
								System.out.println("Not Default");
								if (tessellator.isDrawing) {
									tessellator.draw();
								}
								if (!tessellator.isDrawing) {
									tessellator.startDrawingQuads();
								}
								int texture = ModLoader.getMinecraftInstance().renderEngine
										.getTexture(littleBlock
												.getTextureFile());
								GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
								LBCore.getLittleRenderer(tile.worldObj).renderBlockByRenderType(
										littleBlock,
										coords[0],
										coords[1],
										coords[2]);
								if (tessellator.isDrawing) {
									tessellator.draw();
								}
								GL11.glBindTexture(GL11.GL_TEXTURE_2D, defaultTexture);
							} else {
								if (!tessellator.isDrawing) {
									System.out.println("Not Drawing");
									tessellator.startDrawingQuads();
								}
								LBCore.getLittleRenderer(tile.worldObj).renderBlockByRenderType(
										littleBlock,
										coords[0],
										coords[1],
										coords[2]);
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

			if (!tessellator.isDrawing) {
				tessellator.startDrawingQuads();
			}
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
