/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package net.slimevoid.littleblocks.client.render.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.slimevoid.library.data.Logger.LogLevel;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class LittleBlocksLittleRenderer {
    private RenderBlocks              renderBlocks;
    private List<LittleBlockToRender> littleBlocksToRender;

    public LittleBlocksLittleRenderer(RenderBlocks renderBlocks) {
        this.renderBlocks = renderBlocks;
        this.littleBlocksToRender = new ArrayList<LittleBlockToRender>();
    }

    public void addLittleBlockToRender(Block block, int x, int y, int z) {
        LittleBlockToRender render = new LittleBlockToRender(block, x, y, z);
        if (!this.littleBlocksToRender.contains(render)) {
            this.littleBlocksToRender.add(render);
        }
    }

	public void removeLittleBlock(Block block, int x, int y, int z) {
        LittleBlockToRender render = new LittleBlockToRender(block, x, y, z);
        if (this.littleBlocksToRender.contains(render)) {
            this.littleBlocksToRender.remove(render);
        }
	}

    public void renderLittleBlocks(IBlockAccess iblockaccess, int x, int y, int z) {
        if (this.littleBlocksToRender.size() > 0) {
            Tessellator tessellator = Tessellator.instance;
            tessellator.draw();
            GL11.glPushMatrix();

            double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);

            if (!CoreLib.OPTIFINE_INSTALLED) GL11.glTranslated(xS,
                                                               yS,
                                                               zS);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float scale = 1 / (float) ConfigurationLib.littleBlocksSize;
            GL11.glScalef(scale,
                          scale,
                          scale);
            if (!CoreLib.OPTIFINE_INSTALLED) GL11.glTranslated(-xS,
                                                               -yS,
                                                               -zS);

            tessellator.startDrawingQuads();
            for (LittleBlockToRender littleBlockToRender : this.littleBlocksToRender) {
                try {
                    this.renderBlocks.renderBlockByRenderType(littleBlockToRender.block,
                                                              littleBlockToRender.x,
                                                              littleBlockToRender.y,
                                                              littleBlockToRender.z);
                } catch (IllegalArgumentException e) {
                    LoggerLittleBlocks.getInstance("LittleBlocksMod").write(true,
                                                                            "Issue when rendering block ["
                                                                                    + littleBlockToRender.block.getLocalizedName()
                                                                                    + "] failed with error ["
                                                                                    + e.getLocalizedMessage()
                                                                                    + "]",
                                                                            LogLevel.DEBUG);
                }
            }
            tessellator.draw();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            tessellator.startDrawingQuads();
        }
    }

    public boolean needsRefresh(RenderBlocks renderer) {
        return !this.renderBlocks.equals(renderer);
    }
}