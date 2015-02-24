package net.slimevoid.littleblocks.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;

public class ItemLittleBlocksWand extends Item {

    public ItemLittleBlocksWand(int id) {
        super();
        this.setNoRepair();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean isFull3D() {
        return true;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (entityplayer.canPlayerEdit(pos,
                                       side,
                                       itemstack)) {
            if (!world.isRemote) {
                return EnumWandAction.doWandAction(itemstack,
                                                   entityplayer,
                                                   world,
                                                   pos,
                                                   side,
                                                   hitX,
                                                   hitY,
                                                   hitZ);
            }
        }
        return false;
    }
}
