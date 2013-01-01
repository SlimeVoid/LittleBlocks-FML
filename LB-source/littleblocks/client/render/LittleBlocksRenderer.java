package littleblocks.client.render;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LittleBlocksRenderer implements ISimpleBlockRenderingHandler {

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
			
			BlockLittleBlocksLittleRenderer littleBlocks = new BlockLittleBlocksLittleRenderer(tile.worldObj);
			
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
							littleBlocks.addLittleBlockToRender(littleBlock, coords[0], coords[1], coords[2]);
						}
					}
				}
			}


			Tessellator tessellator = Tessellator.instance;

			if (tessellator.isDrawing) {
				tessellator.draw();
			}
			
			GL11.glPushMatrix();
            int mode = tessellator.drawMode;
			tessellator.startDrawing(mode);
			
			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);

			GL11.glTranslated(xS, yS, zS);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			float scale = 1 / (float) LBCore.littleBlocksSize;
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(-xS, -yS, -zS);
			
			littleBlocks.renderBlocks();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine
            		.getTexture("/terrain.png"));
			
            mode = tessellator.drawMode;
            if (tessellator.isDrawing) {
            	tessellator.draw();
            }
			
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			
			tessellator.startDrawing(mode);
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
