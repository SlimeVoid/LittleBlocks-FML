package slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
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

	private void renderTileEntityLittleBlocks(TileEntityLittleBlocks tileentitylittleblocks, double x, double y, double z, float f) {

		if (tileentitylittleblocks == null || tileentitylittleblocks.isEmpty()) {
			return;
		}
		
		LittleTilesLittleRenderer littleTiles = new LittleTilesLittleRenderer(this.tileEntityRenderer);
		
		
		int[][][] content = tileentitylittleblocks.getContents();
		//boolean optifineEnabled = LBCore.optifine;
		
		for (int x1 = 0; x1 < content.length; x1++) {
			for (int y1 = 0; y1 < content[x1].length; y1++) {
				for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
					int blockId = content[x1][y1][z1];
					if (blockId > 0) {
						Block littleBlock = Block.blocksList[blockId];
						if (littleBlock != null) {
							if (littleBlock.hasTileEntity(tileentitylittleblocks.getBlockMetadata(x1, y1, z1))) {
								TileEntity tileentity = tileentitylittleblocks.getTileEntity(x1, y1, z1);
								if (tileentity != null) {
									littleTiles.addLittleTileToRender(tileentity/**, littleBlock.getTextureFile()**/);
								} else {
									//FMLCommonHandler.instance().getFMLLogger().warning("Attempted to render a tile for [" + littleBlock.getLocalizedName() + "] that was null!");
								}
							}
						}
					}
				}
			}
		}
		littleTiles.renderLittleTiles(
				tileentitylittleblocks,
				x,
				y,
				z,
				f);
	}
}
