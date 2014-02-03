package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoidlib.nbt.NBTHelper;
import slimevoidlib.network.PacketEntity;

public class PacketLittleBlocksCollection extends PacketEntity {
    public HashMap<Integer, ItemStack> itemstackCollection = new HashMap<Integer, ItemStack>();

    public PacketLittleBlocksCollection() {
        super();
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    public PacketLittleBlocksCollection(EntityItemLittleBlocksCollection entitylb) {
        this();
        this.setPosition((int) entitylb.posX,
                         (int) entitylb.posY,
                         (int) entitylb.posZ,
                         0);
        this.setEntityId(entitylb.entityId);
        this.setCommand(CommandLib.ENTITY_COLLECTION);
        this.itemstackCollection = entitylb.getCollection();
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        super.readData(data);
        int stacks = data.readInt();
        for (int i = 0; i < stacks; i++) {
            this.addItemToDrop(ItemStack.loadItemStackFromNBT(NBTHelper.readNBTTagCompound(data)));
        }
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeInt(itemstackCollection.size());
        for (ItemStack itemstack : itemstackCollection.values()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTHelper.writeNBTTagCompound(itemstack.writeToNBT(nbttagcompound),
                                          data);
        }
    }

    public void addItemToDrop(ItemStack itemstack) {
        if (itemstackCollection.containsKey(itemstack.getItem().itemID)) {
            itemstackCollection.get(itemstack.getItem().itemID).stackSize++;
        } else {
            itemstackCollection.put(itemstack.getItem().itemID,
                                    itemstack);
        }
    }
}
