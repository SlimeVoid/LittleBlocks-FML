package net.slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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

    private void renderTileEntityLittleBlocks(TileEntityLittleChunk tileentitylittleblocks, double x, double y, double z, float f) {

        if (tileentitylittleblocks != null) {
            ILittleWorld littleWorld = tileentitylittleblocks.getLittleWorld();

            this.rendererDispatcher.setWorld((World) littleWorld);/* tileEntityRenderer.setWorld( */

            GL11.glPushMatrix();

            GL11.glTranslated(x,
                              y,
                              z);
            GL11.glTranslated(-tileentitylittleblocks.getPos().getX(),
                              -tileentitylittleblocks.getPos().getY(),
                              -tileentitylittleblocks.getPos().getZ());
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            float scale = 1F / ConfigurationLib.littleBlocksSize;

            GL11.glScalef(scale,
                          scale,
                          scale);
            
            Collection<TileEntity> tilesToRender = tileentitylittleblocks.getTileEntityList();

            for (TileEntity tileentity : tilesToRender) {
                this.rendererDispatcher/* tileEntityRenderer */.renderTileEntityAt(tileentity,
                                                                               tileentity.getPos().getX(),
                                                                               tileentity.getPos().getY(),
                                                                               tileentity.getPos().getZ(),
                                                                               f);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();

            this.rendererDispatcher.setWorld(/* tileEntityRenderer.setWorld( */(tileentitylittleblocks.getWorld()));
        }
    }
}
