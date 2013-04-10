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
package slimevoid.littleblocks.client.render.tileentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;


public class LittleTilesLittleRenderer {
	private TileEntityRenderer tileEntityRenderer;
	private Set<String> textures = new HashSet<String>();
	private HashMap<String, HashMap<Integer, LittleTileToRender>> texturedTilesToRender;
	private List<LittleTileToRender> tilesToRender;
	
	public LittleTilesLittleRenderer(TileEntityRenderer tileEntityRenderer) {
		this.tileEntityRenderer = tileEntityRenderer;
		this.texturedTilesToRender = new HashMap<String, HashMap<Integer, LittleTileToRender>>();
		this.tilesToRender = new ArrayList<LittleTileToRender>();
	}
	
	public void addLittleTileToRender(TileEntity tileentity) {
		LittleTileToRender render = new LittleTileToRender(tileentity);
		if (!tilesToRender.contains(render)) {
			tilesToRender.add(render);
		}
	}

	public void addLittleTileToRender(TileEntity tileentity, String textureFile) {
		LittleTileToRender render = new LittleTileToRender(tileentity);
		if (this.texturedTilesToRender.containsKey(textureFile)) {
			HashMap<Integer, LittleTileToRender> littleTilesToRender = this.texturedTilesToRender.get(textureFile);
			int nextInt = littleTilesToRender.size();
			littleTilesToRender.put(nextInt, render);
		} else {
			this.textures.add(textureFile);
			HashMap<Integer, LittleTileToRender> littleTilesToRender = new HashMap<Integer, LittleTileToRender>();
			littleTilesToRender.put(0, render);
			this.texturedTilesToRender.put(textureFile, littleTilesToRender);
		}
	}

	public void renderLittleTiles(TileEntityLittleBlocks tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-tileentity.xCoord, -tileentity.yCoord, -tileentity.zCoord);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float scale = 1F / LBCore.littleBlocksSize;
		GL11.glScaled(scale, scale, scale);

		RenderHelper.disableStandardItemLighting();
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		if (Minecraft.isAmbientOcclusionEnabled()) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		} else {
			GL11.glShadeModel(GL11.GL_FLAT);
		}
		
		/**for (String textureFile : this.textures) {
			boolean customTexture = !textureFile.equals("/terrain.png");
			if (this.texturedTilesToRender.containsKey(textureFile)) {
				HashMap<Integer, LittleTileToRender> littleTilesToRender = this.texturedTilesToRender.get(textureFile);
				
				if (customTexture) {
					bindTexture(textureFile);
				}
				for (LittleTileToRender littleTile: littleTilesToRender.values()) {
					this.tileEntityRenderer.renderTileEntityAt(
							littleTile.tileentity,
							littleTile.x,
							littleTile.y,
							littleTile.z,
							f);
				}
			}
			if (customTexture) {
				bindTexture("/terrain.png");
			}
		}**/
		
		for (LittleTileToRender tileToRender : this.tilesToRender) {
			this.tileEntityRenderer.renderTileEntityAt(tileToRender.tileentity, tileToRender.x, tileToRender.y, tileToRender.z, f);
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
/**    public void bindTexture(String textureFile) {
    	RenderEngine re = this.tileEntityRenderer.renderEngine;
    	
    	if (re != null) {
    		re.bindTexture(re.getTexture(textureFile));
    	}
    }**/
}