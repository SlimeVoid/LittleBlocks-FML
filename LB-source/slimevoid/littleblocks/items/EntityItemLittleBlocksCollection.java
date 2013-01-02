package slimevoid.littleblocks.items;

import java.util.HashMap;

import slimevoid.littleblocks.core.LBCore;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityItemLittleBlocksCollection extends EntityItem {
	public HashMap<String, ItemStack> itemstackCollection = new HashMap();

    public EntityItemLittleBlocksCollection(World world) {
    	super(world);
    }

	public EntityItemLittleBlocksCollection(World world, double x, double y, double z) {
		super(world, x, y, z, new ItemStack(LBCore.littleBlocksID, 1, 0));
	}

	public void dropItems(EntityPlayer entityplayer) {
		for (ItemStack itemstack : this.itemstackCollection.values()) {
			entityplayer.inventory.addItemStackToInventory(itemstack);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		int stacks = nbttagcompound.getInteger("LittleBlocks");
		for (int i = 0; i < stacks; i++) {
			this.readItemStackFromNBT(nbttagcompound, i);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		int stacks = 0;
		nbttagcompound.setInteger("LittleBlocks", itemstackCollection.size());
		for (ItemStack itemstack : itemstackCollection.values()) {
			writeItemStackToNBT(nbttagcompound, itemstack, stacks);
			stacks++;
		}
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
		if (itemstackCollection.containsKey(itemstack.getItemName())) {
			itemstackCollection.get(itemstack.getItemName()).stackSize ++;
		} else {
			itemstackCollection.put(itemstack.getItemName(), itemstack);
		}
	}
	
	private void readItemStackFromNBT(NBTTagCompound nbttagcompound, int itemnumber) {
		int id = nbttagcompound.getInteger("ItemID["+itemnumber+"]");
		int damage = nbttagcompound.getInteger("ItemDamage["+itemnumber+"]");
		int stackSize = nbttagcompound.getInteger("StackSize["+itemnumber+"]");
		ItemStack itemstack = new ItemStack(id, damage, stackSize);
		this.addItemToDrop(itemstack);
	}

	private void writeItemStackToNBT(NBTTagCompound nbttagcompound, ItemStack itemstack, int itemnumber) {
		int id = itemstack.itemID;
		int damage = itemstack.getItemDamage();
		int stackSize = itemstack.stackSize;
		nbttagcompound.setInteger("ItemID["+itemnumber+"]", id);
		nbttagcompound.setInteger("ItemDamage["+itemnumber+"]", damage);
		nbttagcompound.setInteger("StackSize["+itemnumber+"]", stackSize);
	}
}
