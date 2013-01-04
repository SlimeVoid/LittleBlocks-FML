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
package slimevoid.littleblocks.client.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import slimevoid.littleblocks.core.LBCore;


public class LittleTilesLittleRenderer {
	private TileEntityRenderer tileEntityRenderer;
	public Set<String> textures = new HashSet<String>();
	public HashMap<String, HashMap<Integer, LittleTileToRender>> texturedTilesToRender = new HashMap<String, HashMap<Integer, LittleTileToRender>>();
	
	public LittleTilesLittleRenderer(TileEntityRenderer tileEntityRenderer) {
		this.tileEntityRenderer = tileEntityRenderer;
	}

	public void addLittleTileToRender(TileEntity tileentity, String textureFile) {
		LittleTileToRender render = new LittleTileToRender(tileentity);
		if (this.texturedTilesToRender.containsKey(textureFile)) {
			HashMap<Integer, LittleTileToRender> littleTilesToRender = this.texturedTilesToRender.get(textureFile);
			int nextInt = littleTilesToRender.size();
			littleTilesToRender.put(nextInt, render);
		} else {
			this.textures.add(textureFile);
			HashMap<Integer, LittleTileToRender> littleTilesToRender = new HashMap();
			littleTilesToRender.put(0, render);
			this.texturedTilesToRender.put(textureFile, littleTilesToRender);
		}
	}
	
	public class LittleTileToRender {
		public TileEntity tileentity;
		int x, y, z;
		
		public LittleTileToRender(TileEntity tileentity) {
			this.tileentity = tileentity;
			this.x = tileentity.xCoord;
			this.y = tileentity.yCoord;
			this.z = tileentity.zCoord;
		}
	}

	public void renderLittleTiles(double x, double y, double z, int tileX, int tileY, int tileZ, float f) {
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y, z);
		GL11.glTranslated(-tileX, -tileY, -tileZ);
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
		
		for (String textureFile : this.textures) {
			if (this.texturedTilesToRender.containsKey(textureFile)) {
				HashMap<Integer, LittleTileToRender> littleTilesToRender = this.texturedTilesToRender.get(textureFile);
				
				for (LittleTileToRender tile: littleTilesToRender.values()) {
					boolean needsRendering = true;
					if (this.tileEntityRenderer.hasSpecialRenderer(tile.tileentity)) {
						TileEntitySpecialRenderer specialRenderer = this.tileEntityRenderer.getSpecialRendererForEntity(tile.tileentity);
						//ModLoader.getLogger().fine("Renderer[" + specialRenderer.toString() + "]-Tile[" + tile.tileentity.toString() + "]");
						if (specialRenderer != null) {
							specialRenderer.renderTileEntityAt(
									tile.tileentity,
									tile.x,
									tile.y,
									tile.z,
									f);
							needsRendering = false;
						}
					}
					if (needsRendering) {
						this.tileEntityRenderer.renderTileEntityAt(
								tile.tileentity,
								tile.x,
								tile.y,
								tile.z,
								f);
					}
				}
			}
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}