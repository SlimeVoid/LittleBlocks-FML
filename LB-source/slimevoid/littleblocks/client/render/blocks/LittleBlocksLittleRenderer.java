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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import slimevoid.littleblocks.core.LBCore;


public class LittleBlocksLittleRenderer {
	private RenderBlocks renderBlocks;
	private Set<String> textures = new HashSet<String>();
	private HashMap<String, HashMap<Integer, LittleBlockToRender>> texturedBlocksToRender = new HashMap<String, HashMap<Integer, LittleBlockToRender>>();
	
	public LittleBlocksLittleRenderer(RenderBlocks renderBlocks) {
		this.renderBlocks = renderBlocks;
	}

	public void addLittleBlockToRender(Block block, int x, int y, int z) {
		LittleBlockToRender render = new LittleBlockToRender(block, x, y, z);
		if (texturedBlocksToRender.containsKey(block.getTextureFile())) {
			HashMap<Integer, LittleBlockToRender> littleBlocksToRender = texturedBlocksToRender.get(block.getTextureFile());
			int nextInt = littleBlocksToRender.size();
			littleBlocksToRender.put(nextInt, render);
		} else {
			this.textures.add(block.getTextureFile());
			HashMap<Integer, LittleBlockToRender> littleBlocksToRender = new HashMap<Integer, LittleBlockToRender>();
			littleBlocksToRender.put(0, render);
			texturedBlocksToRender.put(block.getTextureFile(), littleBlocksToRender);
		}
	}

	public void renderLittleBlocks(IBlockAccess iblockaccess, int x, int y, int z) {
		if (this.texturedBlocksToRender.size() > 0) {
			Tessellator tessellator = Tessellator.instance;
	        int mode = tessellator.drawMode;
			tessellator.draw();
			GL11.glPushMatrix();
			tessellator.startDrawing(mode);
			
			double xS = -((x >> 4) << 4), yS = -((y >> 4) << 4), zS = -((z >> 4) << 4);
	
			GL11.glTranslated(xS, yS, zS);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			float scale = 1 / (float) LBCore.littleBlocksSize;
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslated(-xS, -yS, -zS);
			
			for (String textureFile : this.textures) {
				if (this.texturedBlocksToRender.containsKey(textureFile)) {
					HashMap<Integer, LittleBlockToRender> littleBlocksToRender = this.texturedBlocksToRender.get(textureFile);
					
					tessellator.draw();
					tessellator.startDrawing(mode);
					
					boolean customTexture = !textureFile.equals("/terrain.png");
					if (customTexture) {
						bindTexture(textureFile);
					}
					
					for (LittleBlockToRender block: littleBlocksToRender.values()) {
						this.renderBlocks.
								renderBlockByRenderType(
											block.block,
											block.x,
											block.y,
											block.z);
					}
					tessellator.draw();
					tessellator.startDrawing(mode);
	
					if (customTexture) {
						unbindTexture();
					}
				}
			}
			
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}
	
	private void bindTexture(String textureFile) {
		int texture = ModLoader.getMinecraftInstance(
				).renderEngine.getTexture(
						textureFile
		);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
	
	private void unbindTexture() {
		int texture = ModLoader.getMinecraftInstance(
				).renderEngine.getTexture(
						"/terrain.png"
		);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}
}