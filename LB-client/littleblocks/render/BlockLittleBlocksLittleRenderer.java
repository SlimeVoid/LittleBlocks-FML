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
package littleblocks.render;

import java.util.HashMap;
import java.util.Map;

import littleblocks.world.LittleWorld;
import net.minecraft.src.Block;

public class BlockLittleBlocksLittleRenderer {
	private static BlockLittleBlocksLittleRenderer instance;
	private Map<Integer, BlockToRender> blocksToRender;
	private LittleWorld littleWorld;

	private BlockLittleBlocksLittleRenderer(LittleWorld littleWorld) {
		this.blocksToRender = new HashMap<Integer, BlockToRender>();
		this.littleWorld = littleWorld;
	}

	public static BlockLittleBlocksLittleRenderer getInstance(LittleWorld littleWorld) {
		if (instance == null || instance.littleWorld.hashCode() != littleWorld
				.hashCode())
			instance = new BlockLittleBlocksLittleRenderer(littleWorld);

		return instance;
	}

	public void addMem(int index, int[] newcoords, Block littleBlock) {
		BlockToRender blockToRender = new BlockToRender(littleBlock, newcoords);
		this.blocksToRender.put(index, blockToRender);
	}

	public void remMem(int index) {
		this.blocksToRender.remove(index);
	}

	public int[] getCoords(int index) {
		BlockToRender node = this.blocksToRender.get(index);
		if (node == null) {
			return null;
		} else {
			return node.coords;
		}
	}

	public Block getBlock(int index) {
		BlockToRender node = this.blocksToRender.get(index);
		if (node == null) {
			return null;
		} else {
			return node.block;
		}
	}

	public class BlockToRender {
		Block block;
		int[] coords;

		public BlockToRender(Block block, int[] coords) {
			this.block = block;
			this.coords = coords;
		}
	}

	public int getSize() {
		return blocksToRender.size();
	}

	public void clear() {
		this.blocksToRender.clear();
	}
}