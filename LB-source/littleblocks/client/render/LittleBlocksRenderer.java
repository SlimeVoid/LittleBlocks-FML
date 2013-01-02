package littleblocks.client.render;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
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
			
			LittleBlocksLittleRenderer littleBlocks = new LittleBlocksLittleRenderer(LBCore.getLittleRenderer(tile.worldObj));
			
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
			
			littleBlocks.renderLittleBlocks(world, x, y, z);
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
