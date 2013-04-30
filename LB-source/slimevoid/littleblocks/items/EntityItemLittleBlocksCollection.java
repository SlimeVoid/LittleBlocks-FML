package slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LBCore;

public class EntityItemLittleBlocksCollection extends EntityItem {
	private HashMap<Integer, ItemStack> itemstackCollection = new HashMap<Integer, ItemStack>();
	
	public HashMap<Integer, ItemStack> getCollection() {
		return this.itemstackCollection;
	}

	public void setCollection(HashMap<Integer, ItemStack> itemstackCollection) {
		this.itemstackCollection = itemstackCollection;
	}

    public EntityItemLittleBlocksCollection(World world) {
    	super(world);
    }

	public EntityItemLittleBlocksCollection(World world, double x, double y, double z) {
		super(world, x, y, z, new ItemStack(LBCore.littleChunkID, 1, 0));
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
		if (itemstackCollection.containsKey(itemstack.getItem().itemID)) {
			itemstackCollection.get(itemstack.getItem().itemID).stackSize ++;
		} else {
			itemstackCollection.put(itemstack.getItem().itemID, itemstack);
		}
	}
	
	private void readItemStackFromNBT(NBTTagCompound nbttagcompound, int itemnumber) {
		int id = nbttagcompound.getInteger("ItemID["+itemnumber+"]");
		int damage = nbttagcompound.getInteger("ItemDamage["+itemnumber+"]");
		int stackSize = nbttagcompound.getInteger("StackSize["+itemnumber+"]");
		System.out.println("read: " + stackSize);
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
	
	public boolean isEmpty() {
		return !(this.itemstackCollection.size() > 0);
	}
}
