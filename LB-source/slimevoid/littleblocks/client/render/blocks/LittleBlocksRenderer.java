package slimevoid.littleblocks.client.render.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class LittleBlocksRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (block.blockID == LBCore.littleChunkID) {
			//if (!LBCore.optifine) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) world
						.getBlockTileEntity(x, y, z);
				if (tile == null) {
					return false;
				}
	
				int[][][] content = tile.getContents();
				
				LittleBlocksLittleRenderer littleBlocks = new LittleBlocksLittleRenderer(LBCore.getLittleRenderer(tile.worldObj));
				
				for (int x1 = 0; x1 < content.length; x1++) {
					for (int y1 = 0; y1 < content[x1].length; y1++) {
						for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
							int blockId = content[x1][y1][z1];
							if (blockId > 0) {
								Block littleBlock = Block.blocksList[blockId];
								if (littleBlock !=  null) {
									int[] coords = {
											(x << 3) + x1,
											(y << 3) + y1,
											(z << 3) + z1 };
									littleBlocks.addLittleBlockToRender(littleBlock, coords[0], coords[1], coords[2]);
								} else {
									FMLCommonHandler.instance().getFMLLogger().warning("Attempted to render a block that was null!");
								}
							}
						}
					}
				}
				littleBlocks.renderLittleBlocks(world, x, y, z);
			//}
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
