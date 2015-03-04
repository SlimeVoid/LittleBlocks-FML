package net.slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sun.security.provider.certpath.Vertex;

import java.util.Collection;

public class TileEntityLittleBlocksRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f, int unknown) {
        renderTileEntityLittleBlocks((TileEntityLittleChunk) tileentity,
                                     d,
                                     d1,
                                     d2,
                                     f);
    }

    private void renderTileEntityLittleBlocks(TileEntityLittleChunk tile, double x, double y, double z, float f) {
        if (tile != null) {
            BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
            BlockModelRenderer bmr = brd.getBlockModelRenderer();
            ILittleWorld littleWorld = tile.getLittleWorld();

            this.rendererDispatcher.setWorld((World) littleWorld);/* tileEntityRenderer.setWorld( */

            bindTexture(TextureMap.locationBlocksTexture);

            BlockPos pos = tile.getPos();

            RenderHelper.disableStandardItemLighting();
            GlStateManager.matrixMode(5888);
            EntityRenderer er = Minecraft.getMinecraft().entityRenderer;
            er.enableLightmap();

            GL11.glPushMatrix();
            GL11.glTranslated(x - pos.getX(),
                    y - pos.getY(),
                    z - pos.getZ());
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            float scale = 1F / ConfigurationLib.littleBlocksSize;
            GL11.glScalef(scale,
                    scale,
                    scale);

            Tessellator tess = Tessellator.getInstance();
            WorldRenderer wr = tess.getWorldRenderer();
            wr.startDrawingQuads();
            wr.setVertexFormat(DefaultVertexFormats.BLOCK);

            for(int i = 0; i < 512; i ++) {
                BlockPos posm7 = new BlockPos(i % 8, (i / 8) % 8, i / 64);
                BlockPos lpos =  BlockUtil.getLittleChunkPos(tile.getPos()).add(posm7);
                try {

                    IBlockState state = tile.getBlockState(posm7);
                    if(!littleWorld.isAirBlock(lpos)) {

                        brd.renderBlock(state, lpos, littleWorld, wr);

                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            tess.draw();


            Collection<TileEntity> tilesToRender = tile.getTileEntityList();

            for (TileEntity tileentity : tilesToRender) {
                this.rendererDispatcher/* tileEntityRenderer */.renderTileEntityAt(tileentity,
                                                                               tileentity.getPos().getX(),
                                                                               tileentity.getPos().getY(),
                                                                               tileentity.getPos().getZ(),
                                                                               f);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            this.rendererDispatcher.setWorld(/* tileEntityRenderer.setWorld( */(tile.getWorld()));
        }
    }
}
