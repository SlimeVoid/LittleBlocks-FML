package net.slimevoid.littleblocks.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.core.lib.IconLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLittleBlocksWand extends Item {

    protected IIcon[] iconList;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        iconList = new IIcon[1];
        iconList[0] = iconRegister.registerIcon(IconLib.LB_WAND);
    }

    @Override
    public IIcon getIconFromDamage(int i) {
        return iconList[0];
    }

    public ItemLittleBlocksWand(int id) {
        super();
        this.setNoRepair();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean isFull3D() {
        return true;
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (entityplayer.canPlayerEdit(x,
                                       y,
                                       z,
                                       side,
                                       itemstack)) {
            if (!world.isRemote) {
                return EnumWandAction.doWandAction(itemstack,
                                                   entityplayer,
                                                   world,
                                                   x,
                                                   y,
                                                   z,
                                                   side,
                                                   hitX,
                                                   hitY,
                                                   hitZ);
            }
        }
        return false;
    }
}
