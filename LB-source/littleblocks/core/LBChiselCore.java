package littleblocks.core;

import java.util.HashMap;

import littleblocks.api.IChiselable;

import net.minecraft.block.Block;

public class LBChiselCore {
	HashMap<Class<? extends Block>, Integer> chiselableBlocks = new HashMap<Class<? extends Block>, Integer>();
	
	public void registerChiselableBlock(Class<? extends Block> blockClass, int chiselableAmount) {
		if (!chiselableBlocks.containsKey(blockClass)) {
			chiselableBlocks.put(blockClass, chiselableAmount);
		} else {
			LoggerLittleBlocks.getInstance(
					"LBChiselCore"
			).write(
					true,
					"Block is already registered as chiselable", 
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
	}
	
	public boolean isBlockChiselable(Block block) {
		if (block instanceof IChiselable) {
			return ((IChiselable)block).isChiselable();
		} else {
			Class<? extends Block> blockClass = block.getClass();
			if (chiselableBlocks.containsKey(blockClass)) {
				return true;
			}
		}
		return false;
	}
	
	public int getChiselableAmount(Block block) {
		if (block instanceof IChiselable) {
			return ((IChiselable)block).getChiseledAmount();
		} else {
			Class<? extends Block> blockClass = block.getClass();
			if (chiselableBlocks.containsKey(blockClass)) {
				return chiselableBlocks.get(blockClass);
			}
		}
		return 0;
	}
}
