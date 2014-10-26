package net.slimevoid.littleblocks.client.render.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class LittleBlocksRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    LittleBlocksLittleRenderer littleSolidBlocks;
    LittleBlocksLittleRenderer littleAlphaBlocks;

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (block == ConfigurationLib.littleChunk) {
            TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                     y,
                                                                                     z);
            if (tile == null) {
                return false;
            }

            this.updateRenderers(ConfigurationLib.getLittleRenderer(tile.getWorldObj()));
            
            boolean isSolidPass = BlockLittleChunk.currentPass == 0;

            for (int x1 = 0; x1 < tile.size; x1++) {
                for (int y1 = 0; y1 < tile.size; y1++) {
                    for (int z1 = 0; z1 < tile.size; z1++) {
                        Block littleBlock = tile.getBlock(x1,
                                                          y1,
                                                          z1);
                        int[] coords = {
                                (x << 3) + x1,
                                (y << 3) + y1,
                                (z << 3) + z1 };
                        if (isSolidPass) {
                        	updateBlocks(this.littleSolidBlocks, littleBlock, coords);
                        } else {
                        	updateBlocks(this.littleAlphaBlocks, littleBlock, coords);
                        }
                    }
                }
            }
            if (isSolidPass) {
            	this.littleSolidBlocks.renderLittleBlocks(world, x, y, z);
            } else {
            	this.littleAlphaBlocks.renderLittleBlocks(world, x, y, z);
            }
            return true;
        }
        return false;
    }
    
    private static void updateBlocks(LittleBlocksLittleRenderer blocks, Block littleBlock, int[] coords) {
    	boolean blockAdded = false;
        if (littleBlock != null) {
            if (littleBlock.getMaterial() != Material.air) {
            	blocks.addLittleBlockToRender(littleBlock,
                                            coords[0],
                                            coords[1],
                                            coords[2]);
                blockAdded = true;
            }
        }
        if (!blockAdded) {
        	blocks.removeLittleBlock(littleBlock,
                                    coords[0],
                                    coords[1],
                                    coords[2]);
        }
    }

	private void updateRenderers(RenderBlocks renderBlocks) {
    	if (this.littleSolidBlocks == null || this.littleSolidBlocks.needsRefresh(renderBlocks)) {
    		this.littleSolidBlocks = new LittleBlocksLittleRenderer(renderBlocks);
    	}
    	if (this.littleAlphaBlocks == null || this.littleAlphaBlocks.needsRefresh(renderBlocks)) {
    		this.littleAlphaBlocks = new LittleBlocksLittleRenderer(renderBlocks);
    	}
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
