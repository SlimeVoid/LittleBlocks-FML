package slimevoid.littleblocks.core;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import slimevoid.littleblocks.api.IChiselable;

public class LBChiselCore {
	private static HashMap<Class<? extends Block>, Integer> chiselableBlocks = new HashMap<Class<? extends Block>, Integer>();
	
	public static void registerChiselableBlock(Class<? extends Block> blockClass, int chiselableAmount) {
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
	
	public static boolean isBlockChiselable(Block block) {
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
	
	public static int getChiselableAmount(Item item) {
		if (item instanceof IChiselable) {
			return ((IChiselable)item).chiseledAmount();
		} else {
			if (item instanceof ItemBlock) {
				Class <? extends ItemBlock> blockClass = ((ItemBlock)item).getClass();
				if (chiselableBlocks.containsKey(blockClass)) {
					return chiselableBlocks.get(blockClass);
				}
			}
		}
		return 512;
	}

	public static String getChiseledName(ItemStack itemstack) {
		String stackName = "";
    	if (itemstack != null && itemstack.stackTagCompound != null) {
    		stackName = itemstack.stackTagCompound.getString("littleName");
    	}
    	return stackName;
	}
	
	private static ItemStack getNewChiseledStack(int stackSize) {
		ItemStack chiseledStack = new ItemStack(LBCore.littleBlock, stackSize);
		chiseledStack.stackTagCompound = new NBTTagCompound();
		return chiseledStack;
	}

	public static ItemStack getChiseledStack(ItemStack blockStack) {
		ItemStack chiseledStack = getNewChiseledStack(getChiselableAmount(blockStack.getItem()));
		if (chiseledStack != null && chiseledStack.stackTagCompound != null) {
			chiseledStack.stackTagCompound.setInteger("littleID", blockStack.itemID);
			chiseledStack.stackTagCompound.setInteger("littleDamage", blockStack.getItemDamage());
			chiseledStack.stackTagCompound.setString("littleName", blockStack.getItemName());
			return chiseledStack;
		}
		return null;
	}
}
