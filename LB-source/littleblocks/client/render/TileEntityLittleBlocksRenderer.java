package littleblocks.client.render;

import java.util.HashMap;

import littleblocks.client.render.BlockLittleBlocksLittleRenderer.LittleBlockToRender;
import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityLittleBlocksRenderer extends TileEntitySpecialRenderer {
		
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

		if (tile == null || tile.isEmpty()) {
			return;
		}
		
		BlockLittleBlocksLittleRenderer littleBlocks = new BlockLittleBlocksLittleRenderer(tile.worldObj);
		
		int[][][] content = tile.getContent();
		
		for (int x1 = 0; x1 < content.length; x1++) {
			for (int y1 = 0; y1 < content[x1].length; y1++) {
				for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
					int blockId = content[x1][y1][z1];
					if (blockId > 0) {
						Block littleBlock = Block.blocksList[blockId];
						if (littleBlock.hasTileEntity(tile.getMetadata(x1, y1, z1))) {
							int[] coords = {
									(tile.xCoord << 3) + x1,
									(tile.yCoord << 3) + y1,
									(tile.zCoord << 3) + z1 };
							littleBlocks.addLittleBlockToRender(littleBlock, coords[0], coords[1], coords[2]);
						}
					}
				}
			}
		}

		//Tessellator tessellator = Tessellator.instance;
        //int mode = tessellator.drawMode;

		//if (tessellator.isDrawing) {
		//	tessellator.draw();
		//}
		
		GL11.glPushMatrix();
		//tessellator.startDrawing(mode);
		
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-tile.xCoord, -tile.yCoord, -tile.zCoord);

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		float scale = 1F / LBCore.littleBlocksSize;
		GL11.glScaled(scale, scale, scale);

		RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		} else {
			GL11.glShadeModel(GL11.GL_FLAT);
		}
		
		renderTiles(littleBlocks, tile, f);

        //this.bindTextureByName("/terrain.png");
		
        //if (tessellator.isDrawing) {
        //	tessellator.draw();
        //}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		//tessellator.startDrawing(mode);
    	//tessellator.draw();
	}

	public void renderTiles(BlockLittleBlocksLittleRenderer littleBlocks, TileEntityLittleBlocks tile, float f) {
		for (String textureFile : littleBlocks.textures) {
			if (littleBlocks.texturedBlocksToRender.containsKey(textureFile)) {
				//Tessellator tessellator = Tessellator.instance;
				//int mode = tessellator.drawMode;
				//if (tessellator.isDrawing) {
				//	tessellator.draw();
				//	tessellator.startDrawing(mode);
				//}
					ModLoader.getLogger().fine("[LittleBlocks]-TileRender-Texture["+textureFile+"]");
				//this.bindTextureByName(textureFile);
				//if (tessellator.isDrawing) {
				//	tessellator.draw();
				//}
				HashMap<Integer, BlockLittleBlocksLittleRenderer.LittleBlockToRender> littleBlocksToRender = littleBlocks.texturedBlocksToRender.get(textureFile);
				for (LittleBlockToRender block: littleBlocksToRender.values()) {
					TileEntity littleTile = tile.getLittleWorld().getBlockTileEntity(
							block.x,
							block.y,
							block.z);
					if (littleTile != null) {
							ModLoader.getLogger().fine("[LittleBlocks]-TileRender-Tile["+littleTile.toString()+"]");
						tileEntityRenderer.renderTileEntityAt(
								littleTile,
								littleTile.xCoord,
								littleTile.yCoord,
								littleTile.zCoord,
								f);
					}
				}
				//if (tessellator.isDrawing) {
				//	tessellator.draw();
				//}
			}
		}
	}
}
