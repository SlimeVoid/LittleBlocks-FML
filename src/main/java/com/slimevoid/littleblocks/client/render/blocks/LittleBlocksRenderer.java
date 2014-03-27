package com.slimevoid.littleblocks.client.render.blocks;

import com.slimevoid.littleblocks.blocks.BlockLittleChunk;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class LittleBlocksRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		if (block == ConfigurationLib.littleChunk) {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) world
					.getTileEntity(x, y, z);
			if (tile == null) {
				return false;
			}

			LittleBlocksLittleRenderer littleBlocks = new LittleBlocksLittleRenderer(
					ConfigurationLib.getLittleRenderer(tile.getWorldObj()));

			for (int x1 = 0; x1 < tile.size; x1++) {
				for (int y1 = 0; y1 < tile.size; y1++) {
					for (int z1 = 0; z1 < tile.size; z1++) {
						Block littleBlock = tile.getBlock(x1, y1, z1);
						if (littleBlock != Blocks.air) {
							if (littleBlock != null) {
								int[] coords = { (x << 3) + x1, (y << 3) + y1,
										(z << 3) + z1 };
								if (littleBlock
										.canRenderInPass(BlockLittleChunk.currentPass)) {
									littleBlocks.addLittleBlockToRender(
											littleBlock, coords[0], coords[1],
											coords[2]);
								}
							} else {
								FMLCommonHandler
										.instance()
										.getFMLLogger()
										.warn("Attempted to render a block that was null!");
							}
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
	public boolean shouldRender3DInInventory(int modelID) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ConfigurationLib.renderType;
	}
}
