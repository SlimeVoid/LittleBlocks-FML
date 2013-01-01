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
package littleblocks.client.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import littleblocks.core.LBCore;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;


public class BlockLittleBlocksLittleRenderer {
	private World world;
	public Set<String> textures = new HashSet();
	public HashMap<String, HashMap<Integer, LittleBlockToRender>> texturedBlocksToRender = new HashMap();
	
	public BlockLittleBlocksLittleRenderer(World worldObj) {
		this.world = worldObj;
	}

	public void addLittleBlockToRender(Block block, int x, int y, int z) {
		LittleBlockToRender render = new LittleBlockToRender(block, x, y, z);
		if (texturedBlocksToRender.containsKey(block.getTextureFile())) {
			HashMap<Integer, LittleBlockToRender> littleBlocksToRender = texturedBlocksToRender.get(block.getTextureFile());
			int nextInt = littleBlocksToRender.size();
			littleBlocksToRender.put(nextInt, render);
		} else {
			this.textures.add(block.getTextureFile());
			HashMap<Integer, LittleBlockToRender> littleBlocksToRender = new HashMap();
			littleBlocksToRender.put(0, render);
			texturedBlocksToRender.put(block.getTextureFile(), littleBlocksToRender);
		}
	}
	
	public class LittleBlockToRender {
		public Block block;
		public int x, y, z;
		
		public LittleBlockToRender(Block block, int x, int y, int z) {
			this.block = block;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	public void renderBlocks() {
		for (String textureFile : this.textures) {
			if (this.texturedBlocksToRender.containsKey(textureFile)) {
				Tessellator tessellator = Tessellator.instance;
				int mode = tessellator.drawMode;
				tessellator.draw();
				tessellator.startDrawing(mode);
				
				HashMap<Integer, LittleBlockToRender> littleBlocksToRender = this.texturedBlocksToRender.get(textureFile);
				int texture = ModLoader.getMinecraftInstance().renderEngine
						.getTexture(textureFile);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
				for (LittleBlockToRender block: littleBlocksToRender.values()) {
					//MinecraftForgeClient.renderBlock(LBCore.getLittleRenderer(world), block.block, block.x, block.y, block.z);
					LBCore.getLittleRenderer(world).
							renderBlockByRenderType(
										block.block,
										block.x,
										block.y,
										block.z);
				}
				
				tessellator.draw();
				tessellator.startDrawing(mode);
			}
		}
	}
}