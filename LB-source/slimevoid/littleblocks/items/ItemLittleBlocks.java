package slimevoid.littleblocks.items;

import slimevoid.littleblocks.core.LBChiselCore;
import slimevoid.littleblocks.core.LBCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemLittleBlocks extends Item {
	
	public ItemLittleBlocks(int id) {
		super(id);
		this.setNoRepair();
		this.setMaxStackSize(1);
		this.setMaxDamage(LBCore.littleBlocksSize * LBCore.littleBlocksSize * LBCore.littleBlocksSize);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public String getItemNameIS(ItemStack itemstack) {
		String name = "Little ";
		String blockName = LBChiselCore.getChiseledName(itemstack);
		if (!blockName.isEmpty()) {
			name += blockName;
		} else {
			name += "Block [Unknown]";
		}
		return name;
	}	

}
