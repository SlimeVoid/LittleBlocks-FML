package slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.ConfigurationLib;

public class EntityItemLittleBlocksCollection extends EntityItem {
	private HashMap<Integer, ItemStack>	itemstackCollection	= new HashMap<Integer, ItemStack>();

	public HashMap<Integer, ItemStack> getCollection() {
		return this.itemstackCollection;
	}

	public void setCollection(HashMap<Integer, ItemStack> itemstackCollection) {
		this.itemstackCollection = itemstackCollection;
	}

	public EntityItemLittleBlocksCollection(World world) {
		super(world);
		this.setEntityItemStack(new ItemStack(ConfigurationLib.littleChunk));
	}

	public EntityItemLittleBlocksCollection(World world, double x, double y, double z, ItemStack itemStack) {
		super(world, x, y, z, itemStack);
	}

	public void dropItems(EntityPlayer entityplayer) {
		for (ItemStack itemstack : this.itemstackCollection.values()) {
			entityplayer.inventory.addItemStackToInventory(itemstack);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		NBTTagList itemStacksTag = nbttagcompound.getTagList("ItemStacks");
		for (int i = 0; i < itemStacksTag.tagCount(); i++) {
			ItemStack itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound) itemStacksTag.tagAt(i));
			this.addItemToDrop(itemstack);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		HashMap<Integer, ItemStack> collection = this.itemstackCollection;
		NBTTagList itemStacksTag = new NBTTagList();
		for (ItemStack itemstack : collection.values()) {
			NBTTagCompound itemTag = new NBTTagCompound();
			itemstack.writeToNBT(itemTag);
			itemStacksTag.appendTag(itemTag);
		}
		nbttagcompound.setTag(	"ItemStacks",
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
		if (itemstackCollection.containsKey(itemstack.getItem().itemID)) {
			itemstackCollection.get(itemstack.getItem().itemID).stackSize++;
		} else {
			itemstackCollection.put(itemstack.getItem().itemID,
									itemstack);
		}
	}

	public boolean isEmpty() {
		return !(this.itemstackCollection.size() > 0);
	}
}
