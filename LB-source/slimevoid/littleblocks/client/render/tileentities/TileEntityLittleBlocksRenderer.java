package slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.FMLCommonHandler;

public class TileEntityLittleBlocksRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
        renderTileEntityLittleBlocks((TileEntityLittleChunk) tileentity,
                                     d,
                                     d1,
                                     d2,
                                     f);
    }

    private void renderTileEntityLittleBlocks(TileEntityLittleChunk tileentitylittleblocks, double x, double y, double z, float f) {

        if (tileentitylittleblocks == null || tileentitylittleblocks.isEmpty()) {
            return;
        }

        LittleTilesLittleRenderer littleTiles = new LittleTilesLittleRenderer(this.tileEntityRenderer, (World) tileentitylittleblocks.getLittleWorld());

        int[][][] content = tileentitylittleblocks.getContents();

        for (int x1 = 0; x1 < content.length; x1++) {
            for (int y1 = 0; y1 < content[x1].length; y1++) {
                for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
                    int blockId = content[x1][y1][z1];
                    if (blockId > 0) {
                        Block littleBlock = Block.blocksList[blockId];
                        if (littleBlock != null) {
                            if (littleBlock.hasTileEntity(tileentitylittleblocks.getBlockMetadata(x1,
                                                                                                  y1,
                                                                                                  z1))) {
                                TileEntity tileentity = tileentitylittleblocks.getLittleWorld().getBlockTileEntity(((tileentitylittleblocks.xCoord << 3) + x1),
                                                                                                                   ((tileentitylittleblocks.yCoord << 3) + y1),
                                                                                                                   ((tileentitylittleblocks.zCoord << 3) + z1));
                                if (tileentity != null) {
                                    littleTiles.addLittleTileToRender(tileentity);
                                } else {
                                    FMLCommonHandler.instance().getFMLLogger().warning("Attempted to render a tile for ["
                                                                                       + littleBlock
                                                                                       + "] that was null!");
                                }
                            }
                        }
                    }
                }
            }
        }
        littleTiles.renderLittleTiles(tileentitylittleblocks,
                                      x,
                                      y,
                                      z,
                                      f);
    }
}
