package slimevoid.littleblocks.client.render.tileentities;

import java.util.Collection;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

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

        if (tileentitylittleblocks == null
            || tileentitylittleblocks.getTileEntityList().isEmpty()) {
            return;
        }

        Collection<TileEntity> tilesToRender = tileentitylittleblocks.getTileEntityList();

        this.tileEntityRenderer.setWorld((World) tileentitylittleblocks.getLittleWorld());

        GL11.glPushMatrix();

        GL11.glTranslated(x,
                          y,
                          z);
        GL11.glTranslated(-tileentitylittleblocks.xCoord,
                          -tileentitylittleblocks.yCoord,
                          -tileentitylittleblocks.zCoord);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        float scale = 1F / ConfigurationLib.littleBlocksSize;

        GL11.glScalef(scale,
                      scale,
                      scale);

        for (TileEntity tileentity : tilesToRender) {
            this.tileEntityRenderer.renderTileEntityAt(tileentity,
                                                       tileentity.xCoord,
                                                       tileentity.yCoord,
                                                       tileentity.zCoord,
                                                       f);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();

        this.tileEntityRenderer.setWorld(tileentitylittleblocks.getWorldObj());
    }
}
