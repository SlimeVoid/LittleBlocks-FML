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
package slimevoid.littleblocks.client.render.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CoreLib;


public class LittleBlocksLittleRenderer {
	private RenderBlocks renderBlocks;
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

	public void renderLittleBlocks(IBlockAccess iblockaccess, int x, int y, int z) {
		if (this.littleBlocksToRender.size() > 0) {
			Tessellator tessellator = Tessellator.instance;
	        int mode = tessellator.drawMode;
	        tessellator.draw();
	        GL11.glPushMatrix();			
	        
			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);
	
			if (!CoreLib.OPTIFINE_INSTALLED) GL11.glTranslated(xS, yS, zS);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			float scale = 1 / (float) LBCore.littleBlocksSize;
			GL11.glScalef(scale, scale, scale);
			if (!CoreLib.OPTIFINE_INSTALLED) GL11.glTranslated(-xS, -yS, -zS);
			
			tessellator.startDrawing(mode);
			for (LittleBlockToRender littleBlockToRender : this.littleBlocksToRender) {
				this.renderBlocks.renderBlockByRenderType(
						littleBlockToRender.block,
						littleBlockToRender.x,
						littleBlockToRender.y,
						littleBlockToRender.z);
			}
			tessellator.draw();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			tessellator.startDrawingQuads();
		}
	}
}