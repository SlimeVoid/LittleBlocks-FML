package littleblocks.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.WorldRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
			
			BlockLittleBlocksLittleRenderer blocksToRender =
			BlockLittleBlocksLittleRenderer.getInstance(tile.getLittleWorld());

			RenderBlocks littleRenderer = new RenderBlocks(tile.getLittleWorld());

			Tessellator tessellator = Tessellator.instance;
			
			GL11.glPushMatrix();

				if (tessellator.isDrawing) {
					tessellator.draw();
					tessellator.startDrawingQuads();
				}
				double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);
				GL11.glTranslated(xS, yS, zS);
				GL11.glEnable(32826 /* GL_RESCALE_NORMAL_EXT */);
				float scale = 1 / 8F;
				GL11.glScalef(scale, scale, scale);
				GL11.glTranslated(-xS, -yS, -zS);
				
				int i = 0;
				for (int x1 = 0; x1 < content.length; x1++) {
					for (int y1 = 0; y1 < content[x1].length; y1++) {
						for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
							int blockId = content[x1][y1][z1];
							if (blockId > 0) {
								Block littleBlock = Block.blocksList[blockId];
								int[] coords = {(x << 3) + x1, (y << 3) + y1, (z << 3) + z1};
								if (tessellator.isDrawing) {
									tessellator.draw();
									tessellator.startDrawingQuads();
								}
								MinecraftForgeClient.renderBlock(
										littleRenderer, 
										littleBlock,
										coords[0],
										coords[1],
										coords[2]);
								blocksToRender.addMem(i, coords, littleBlock);
								i++;
								if (tessellator.isDrawing) {
									tessellator.draw();
									tessellator.startDrawingQuads();
								}
							}
						}
					}
				}
				//System.out.println("Blocks to render: " + blocksToRender.getSize());
				for (int index = 0; index < blocksToRender.getSize(); index++) {
					int[] theCoords = blocksToRender.getCoords(index);
					Block theBlock = blocksToRender.getBlock(index);
				}
				blocksToRender.getInstance(tile.getLittleWorld()).clear();
				GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);

			GL11.glPopMatrix();

			if (tessellator.isDrawing) {
				tessellator.draw();
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
