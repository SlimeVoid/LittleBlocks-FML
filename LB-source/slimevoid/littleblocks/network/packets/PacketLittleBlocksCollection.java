package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import slimevoid.lib.network.PacketEntity;
import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;

public class PacketLittleBlocksCollection extends PacketEntity {
	public HashMap<String, ItemStack> itemstackCollection = new HashMap();

	public PacketLittleBlocksCollection() {
		super();
		this.setChannel(LBInit.LBM.getModChannel());
	}

	public PacketLittleBlocksCollection(EntityItemLittleBlocksCollection entitylb) {
		this();
		this.setPosition(
				(int)entitylb.posX,
				(int)entitylb.posY,
				(int)entitylb.posZ,
				0);
		this.setEntityId(entitylb.entityId);
		this.itemstackCollection = entitylb.itemstackCollection;
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		int stacks = data.readInt();
		for (int i = 0; i < stacks; i++) {
			this.readItemStackFromData(data);
		}
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(itemstackCollection.size());
		for (ItemStack itemstack : itemstackCollection.values()) {
			writeItemStackToData(data, itemstack);
		}
	}
	
	public void addItemToDrop(ItemStack itemstack) {
		if (itemstackCollection.containsKey(itemstack.getItemName())) {
			itemstackCollection.get(itemstack.getItemName()).stackSize ++;
		} else {
			itemstackCollection.put(itemstack.getItemName(), itemstack);
		}
	}
	
	private void readItemStackFromData(DataInputStream data) throws IOException {
		int id = data.readInt();
		int damage = data.readInt();
		int stackSize = data.readInt();
		ItemStack itemstack = new ItemStack(id, damage, stackSize);
		this.addItemToDrop(itemstack);
	}

	private void writeItemStackToData(DataOutputStream data, ItemStack itemstack) throws IOException {
		int id = itemstack.itemID;
		int damage = itemstack.getItemDamage();
		int stackSize = itemstack.stackSize;
		data.writeInt(id);
		data.writeInt(damage);
		data.writeInt(stackSize);
	}

}
