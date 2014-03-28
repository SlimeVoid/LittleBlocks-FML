package net.slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;

public class EntityItemLittleBlocksCollection extends EntityItem {
    private HashMap<Item, ItemStack> itemstackCollection = new HashMap<Item, ItemStack>();

    public HashMap<Item, ItemStack> getCollection() {
        return this.itemstackCollection;
    }

    public void setCollection(HashMap<Item, ItemStack> itemstackCollection) {
        this.itemstackCollection = itemstackCollection;
    }

    public EntityItemLittleBlocksCollection(World world) {
        super(world);
        this.setEntityItemStack(new ItemStack(ConfigurationLib.littleChunk));
    }

    public EntityItemLittleBlocksCollection(World world, double x, double y, double z, ItemStack itemStack) {
        super(world, x, y, z, itemStack);
    }

    public int dropItems(EntityPlayer entityplayer) {
        int i = 0;
        for (ItemStack itemstack : this.itemstackCollection.values()) {
            entityplayer.inventory.addItemStackToInventory(itemstack);
            this.playSound("random.pop",
                           0.2F,
                           ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            ++i;
        }
        return i;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        NBTTagList itemStacksTag = nbttagcompound.getTagList("ItemStacks",
                                                             10);
        for (int i = 0; i < itemStacksTag.tagCount(); i++) {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound) itemStacksTag.getCompoundTagAt(i));
            this.addItemToDrop(itemstack);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        HashMap<Item, ItemStack> collection = this.itemstackCollection;
        NBTTagList itemStacksTag = new NBTTagList();
        for (ItemStack itemstack : collection.values()) {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemstack.writeToNBT(itemTag);
            itemStacksTag.appendTag(itemTag);
        }
        nbttagcompound.setTag("ItemStacks",
                              itemStacksTag);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        this.writeToNBT(nbttagcompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        this.readFromNBT(nbttagcompound);
    }

    public void addItemToDrop(ItemStack itemstack) {
        if (itemstackCollection.containsKey(itemstack.getItem())) {
            itemstackCollection.get(itemstack.getItem()).stackSize++;
        } else {
            itemstackCollection.put(itemstack.getItem(),
                                    itemstack);
        }
    }

    public boolean isEmpty() {
        return !(this.itemstackCollection.size() > 0);
    }
}
